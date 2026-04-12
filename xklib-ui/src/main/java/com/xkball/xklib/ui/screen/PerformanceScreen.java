package com.xkball.xklib.ui.screen;

import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.widget.CheckBox;
import com.xkball.xklib.ui.widget.FlameGraph;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.LineGraph;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklib.utils.AdjacencyList;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

//todo 火焰图
public class PerformanceScreen extends SplitContainer {

    private static final int ALLOC_BUFFER_SIZE = 60;
    private static final int HEADER_BG = 0xFFE2E8F0;
    private static final int PANEL_BG = 0xFFF8FAFC;
    private static final int TEXT_COLOR = 0xFF1E293B;
    private static final int ROW_HEIGHT = 24;
    private static final int CHECKBOX_WIDTH = 50;
    private static final int GRAPH_HEIGHT = 100;
    private static final int FLAME_ROW_HEIGHT = 24;
    private static final int CHART_TITLE_HEIGHT = 20;

    private final IntLayoutVariable bufferSize = new IntLayoutVariable(1000);
    private AdjacencyList<String, Long> profilerData = new AdjacencyList<>();

    private final Deque<Long> frameTime = new ArrayDeque<>();
    private final Deque<Long> renderTime = new ArrayDeque<>();
    private final Deque<Long> layoutTime = new ArrayDeque<>();
    private final Deque<Long> memUsage = new ArrayDeque<>();
    private final Deque<Long> memAlloc = new ArrayDeque<>();
    private final Deque<Long> fpsHistory = new ArrayDeque<>();

    private final List<MetricEntry> metrics = new ArrayList<>();
    private final MetricEntry flameEntry;
    private int flameRows = 1;

    private final AllocationRateCalculator allocationRateCalculator = new AllocationRateCalculator();
    private long lastAllocUpdateTime = 0L;

    public PerformanceScreen() {
        super(false, 2);
        metrics.add(new MetricEntry("FPS", fpsHistory, 1f, " fps", false));
        metrics.add(new MetricEntry("Frame Time", frameTime, 1f / 1_000_000f, " ms", false));
        metrics.add(new MetricEntry("GUI Render", renderTime, 1f / 1_000_000f, " ms", false));
        metrics.add(new MetricEntry("GUI Update", layoutTime, 1f / 1_000_000f, " ms", false));
        metrics.add(new MetricEntry("Memory Usage", memUsage, 1f, " MB", false));
        metrics.add(new MetricEntry("Alloc Rate", memAlloc, 1f, " MB/s", false));
        flameEntry = new MetricEntry("Profiler Tree", null, 1f, "", true);
    }
    
    @Override
    public void setStyle(TaffyStyle style) {
        super.setStyle(style);
    }
    
    @Override
    public IGuiWidget setStyle(Consumer<TaffyStyle> styleUpdate) {
        return super.setStyle(styleUpdate);
    }
    
    @Override
    public void init() {
        super.init();

        var left = getPanel(0);
        left.inlineStyle("background-color: " + PANEL_BG + ";");
        left.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
        });

        var leftHeader = new Label("监看项目", TEXT_COLOR);
        leftHeader.inlineStyle("background-color: " + HEADER_BG + ";text-align: center;");
        leftHeader.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(ROW_HEIGHT));
            s.flexShrink = 0;
        });
        leftHeader.setTextScale(TextScale.EXPAND_WIDTH);
        left.addChild(leftHeader);

        var leftList = new ContainerWidget();
        leftList.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });
        leftList.setYScrollEnable();
        left.addChild(leftList);

        for (MetricEntry entry : metrics) {
            leftList.addChild(buildMetricRow(entry));
        }
        leftList.addChild(buildFlameRow());

        var right = getPanel(1);
        right.inlineStyle("background-color: " + PANEL_BG + ";");
        right.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
        });

        var rightHeader = new Label("图表", TEXT_COLOR);
        rightHeader.inlineStyle("background-color: " + HEADER_BG + ";text-align: center;");
        rightHeader.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(ROW_HEIGHT));
            s.flexShrink = 0;
        });
        rightHeader.setTextScale(TextScale.EXPAND_WIDTH);
        right.addChild(rightHeader);

        var rightScrollPanel = new ContainerWidget();
        rightScrollPanel.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });
        rightScrollPanel.setYScrollEnable();
        right.addChild(rightScrollPanel);

        for (MetricEntry entry : metrics) {
            var graphPanel = new ContainerWidget();
            graphPanel.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.alignItems = AlignItems.STRETCH;
                s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(CHART_TITLE_HEIGHT + GRAPH_HEIGHT));
                s.display = TaffyDisplay.NONE;
                s.flexShrink = 0;
            });

            var title = new Label(entry.name, TEXT_COLOR);
            title.inlineStyle("text-align: center;");
            title.setStyle(s -> {
                s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(CHART_TITLE_HEIGHT));
                s.flexShrink = 0;
            });
            graphPanel.addChild(title);

            var graph = new LineGraph();
            graph.setStyle(s -> {
                s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(GRAPH_HEIGHT));
                s.flexShrink = 0;
            });
            entry.graph = graph;
            entry.graphPanel = graphPanel;
            graphPanel.addChild(graph);
            rightScrollPanel.addChild(graphPanel);
        }

        var flamePanel = new ContainerWidget();
        flamePanel.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(CHART_TITLE_HEIGHT + FLAME_ROW_HEIGHT));
            s.display = TaffyDisplay.NONE;
            s.flexShrink = 0;
        });

        var flameTitle = new Label(flameEntry.name, TEXT_COLOR);
        flameTitle.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(CHART_TITLE_HEIGHT));
            s.flexShrink = 0;
        });
        flamePanel.addChild(flameTitle);

        var flameGraph = new FlameGraph();
        flameGraph.setRowHeight(FLAME_ROW_HEIGHT);
        flameGraph.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(FLAME_ROW_HEIGHT));
            s.flexShrink = 0;
        });
        flameEntry.flameGraph = flameGraph;
        flameEntry.graphPanel = flamePanel;
        flamePanel.addChild(flameGraph);
        rightScrollPanel.addChild(flamePanel);

        for (MetricEntry entry : metrics) {
            final MetricEntry e = entry;
            e.visible.addCallback(checked -> {
                if (e.graphPanel != null) {
                    e.graphPanel.setStyle(s -> s.display = checked ? TaffyDisplay.FLEX : TaffyDisplay.NONE);
                }
            });
        }
        flameEntry.visible.addCallback(checked -> {
            if (flameEntry.graphPanel != null) {
                flameEntry.graphPanel.setStyle(s -> s.display = checked ? TaffyDisplay.FLEX : TaffyDisplay.NONE);
            }
        });

        for (MetricEntry entry : metrics) {
            if (entry.graphPanel != null) {
                entry.graphPanel.setStyle(s -> s.display = entry.visible.get() ? TaffyDisplay.FLEX : TaffyDisplay.NONE);
            }
        }
        if (flameEntry.graphPanel != null) {
            flameEntry.graphPanel.setStyle(s -> s.display = flameEntry.visible.get() ? TaffyDisplay.FLEX : TaffyDisplay.NONE);
        }
    }

    private ContainerWidget buildMetricRow(MetricEntry entry) {
        var row = new ContainerWidget();
        row.setStyle(s -> {
            s.flexDirection = FlexDirection.ROW;
            s.alignItems = AlignItems.CENTER;
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(ROW_HEIGHT));
            s.flexShrink = 0;
        });

        var nameLabel = new Label(entry.name, TEXT_COLOR);
        nameLabel.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.percent(1f));
            s.flexShrink = 0;
        });
        row.addChild(nameLabel);

        entry.valueLabel = new Label("--", TEXT_COLOR);
        entry.valueLabel.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.percent(1f));
            s.flexGrow = 1;
            s.flexShrink = 1;
            s.textAlign = TextAlign.RIGHT;
        });
        row.addChild(entry.valueLabel);

        var cb = new CheckBox();
        cb.bind(entry.visible);
        cb.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.length(CHECKBOX_WIDTH), TaffyDimension.length(ROW_HEIGHT - 6));
            s.flexShrink = 0;
        });
        row.addChild(cb);

        return row;
    }

    private ContainerWidget buildFlameRow() {
        var row = new ContainerWidget();
        row.setStyle(s -> {
            s.flexDirection = FlexDirection.ROW;
            s.alignItems = AlignItems.CENTER;
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(ROW_HEIGHT));
            s.flexShrink = 0;
        });

        var nameLabel = new Label(flameEntry.name, TEXT_COLOR);
        nameLabel.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.percent(1f));
            s.flexGrow = 1;
            s.flexShrink = 1;
        });
        row.addChild(nameLabel);

        var cb = new CheckBox();
        cb.bind(flameEntry.visible);
        cb.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.length(CHECKBOX_WIDTH), TaffyDimension.length(ROW_HEIGHT - 6));
            s.flexShrink = 0;
        });
        row.addChild(cb);

        return row;
    }

    public synchronized void updateData(AdjacencyList<String, Long> data) {
        this.profilerData = data;

        long frameNs = getOrDefault(data, "frame", 1_000_000L);
        if (frameNs <= 0) frameNs = 1_000_000L;

        putData(frameTime, frameNs, bufferSize.get());
        putData(fpsHistory, 1_000_000_000L / frameNs, bufferSize.get());
        putData(renderTime, getOrDefault(data, "gui render", 0L), bufferSize.get());
        putData(layoutTime, getOrDefault(data, "gui update", 0L), bufferSize.get());

        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long usedBytes = total - free;
        putData(memUsage, bytesToMegabytes(usedBytes), bufferSize.get());

        long now = System.currentTimeMillis();
        if (now - lastAllocUpdateTime >= 1000L) {
            lastAllocUpdateTime = now;
            long allocRate = allocationRateCalculator.bytesAllocatedPerSecond(usedBytes);
            putData(memAlloc, bytesToMegabytes(allocRate), ALLOC_BUFFER_SIZE);
        }
        this.submitTreeUpdateAsync(this::updatePage);
    }

    private long getOrDefault(AdjacencyList<String, Long> data, String key, long def) {
        Long val = data.getData(key);
        return val != null ? val : def;
    }

    private <T> void putData(Deque<T> queue, T value, int maxSize) {
        queue.add(value);
        while (queue.size() > maxSize) {
            queue.removeFirst();
        }
    }

    private static long bytesToMegabytes(long bytes) {
        return bytes / 1024L / 1024L;
    }

    public synchronized void updatePage() {
        if (this.style.display == TaffyDisplay.NONE) return;

        for (MetricEntry entry : metrics) {
            if (entry.queue == null || entry.queue.isEmpty()) continue;
            long lastVal = entry.queue.peekLast();

            if (entry.valueLabel != null) {
                String text;
                if (entry.unit.contains("ms")) {
                    text = String.format("%.2f%s", lastVal * entry.multiplier, entry.unit);
                } else {
                    text = lastVal + entry.unit;
                }
                entry.valueLabel.setText(text);
            }

            if (entry.graph != null && entry.graphPanel != null && entry.graphPanel.style.display != TaffyDisplay.NONE) {
                FloatList ys = new FloatArrayList(entry.queue.size());
                FloatList xs = new FloatArrayList(entry.queue.size());
                int i = 0;
                for (Long v : entry.queue) {
                    ys.add(v * entry.multiplier);
                    xs.add(i++);
                }
                entry.graph.setData(xs, ys);
            }
        }

        if (flameEntry.flameGraph != null && flameEntry.graphPanel != null && flameEntry.graphPanel.style.display != TaffyDisplay.NONE) {
            int depth = calcDepth(profilerData);
            if (depth != flameRows) {
                flameRows = depth;
                int graphHeight = FLAME_ROW_HEIGHT * flameRows;
                flameEntry.graphPanel.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(CHART_TITLE_HEIGHT + graphHeight)));
                flameEntry.flameGraph.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(graphHeight)));
            }
            flameEntry.flameGraph.setData(profilerData);
        }
    }

    private int calcDepth(AdjacencyList<String, Long> data) {
        if (data == null || data.nodeCount() == 0) {
            return 1;
        }
        Set<String> roots = new HashSet<>();
        Iterable<String> rootIterable = data.getChild(null);
        if (rootIterable != null) {
            for (String root : rootIterable) {
                roots.add(root);
            }
        }
        if (roots.isEmpty()) {
            roots.addAll(data.getNodes());
        }

        int maxDepth = 1;
        for (String root : roots) {
            maxDepth = Math.max(maxDepth, calcNodeDepth(data, root, new HashSet<>(), 1));
        }
        return maxDepth;
    }

    private int calcNodeDepth(AdjacencyList<String, Long> data, String node, Set<String> stack, int depth) {
        if (node == null || !stack.add(node)) {
            return depth;
        }

        int maxDepth = depth;
        Iterable<String> children = data.getChild(node);
        if (children != null) {
            for (String child : children) {
                maxDepth = Math.max(maxDepth, calcNodeDepth(data, child, stack, depth + 1));
            }
        }
        stack.remove(node);
        return maxDepth;
    }

    private static class MetricEntry {
        final String name;
        final Deque<Long> queue;
        final float multiplier;
        final String unit;
        final boolean isFlame;
        final BooleanLayoutVariable visible = new BooleanLayoutVariable(false);
        Label valueLabel;
        ContainerWidget graphPanel;
        LineGraph graph;
        FlameGraph flameGraph;

        MetricEntry(String name, Deque<Long> queue, float multiplier, String unit, boolean isFlame) {
            this.name = name;
            this.queue = queue;
            this.multiplier = multiplier;
            this.unit = unit;
            this.isFlame = isFlame;
        }
    }

    static class AllocationRateCalculator {
        private static final List<GarbageCollectorMXBean> GC_MBEANS = ManagementFactory.getGarbageCollectorMXBeans();
        private long lastTime = 0L;
        private long lastHeapUsage = -1L;
        private long lastGcCounts = -1L;
        private long lastRate = 0L;

        long bytesAllocatedPerSecond(long usedMemory) {
            long i = System.currentTimeMillis();
            if (i - this.lastTime < 500L) {
                return this.lastRate;
            }
            long j = gcCounts();
            if (this.lastTime != 0L && j == this.lastGcCounts) {
                double d0 = (double) TimeUnit.SECONDS.toMillis(1L) / (i - this.lastTime);
                long k = usedMemory - this.lastHeapUsage;
                this.lastRate = Math.round(k * d0);
            }
            this.lastTime = i;
            this.lastHeapUsage = usedMemory;
            this.lastGcCounts = j;
            return this.lastRate;
        }

        private static long gcCounts() {
            long i = 0L;
            for (GarbageCollectorMXBean bean : GC_MBEANS) {
                i += bean.getCollectionCount();
            }
            return i;
        }
    }
}
