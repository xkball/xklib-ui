package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyStyle;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LineGraphTest {

    private static final int FIXED_BUF_SIZE = 60;
    private static final Random RANDOM = new Random(42);

    private static long frameCount = 0;

    public static void main(String[] ignored) throws Exception {
        List<LineGraph> graphs = new ArrayList<>();

        LineGraph sinLinear = makeGraph(
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.DefaultYAxisMaxStrategy.PERCENT110,
                0xFF63B3ED, "sin+linear+110%"
        );
        LineGraph sinLog = makeGraph(
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.AxisStrategy.LOG10,
                LineGraph.DefaultYAxisMaxStrategy.MAX,
                0xFF68D391, "sin+logY+MAX"
        );
        LineGraph expLinear = makeGraph(
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.DefaultYAxisMaxStrategy.PERCENT110,
                0xFFF6AD55, "exp+linear+110%"
        );
        LineGraph expLog = makeGraph(
                LineGraph.AxisStrategy.LOG10,
                LineGraph.AxisStrategy.LOG10,
                LineGraph.DefaultYAxisMaxStrategy.PERCENT110,
                0xFFFC8181, "exp+logXY+110%"
        );
        LineGraph randomFixed = makeGraph(
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.DefaultYAxisMaxStrategy.PERCENT90,
                0xFFB794F4, "random+fixed+90%"
        );
        LineGraph logLinear = makeGraph(
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.AxisStrategy.LINEAR,
                new LineGraph.FixedYAxisMaxStrategy(10f),
                0xFF76E4F7, "log+fixedMax10"
        );
        LineGraph sinExpCombined = makeGraph(
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.DefaultYAxisMaxStrategy.PERCENT110,
                0xFFFF6B9D, "sin*exp+linear+110%"
        );
        LineGraph randomGrowingBuf = makeGraph(
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.AxisStrategy.LINEAR,
                LineGraph.DefaultYAxisMaxStrategy.MAX,
                0xFFFFD93D, "random+growing+MAX"
        );

        graphs.add(sinLinear);
        graphs.add(sinLog);
        graphs.add(expLinear);
        graphs.add(expLog);
        graphs.add(randomFixed);
        graphs.add(logLinear);
        graphs.add(sinExpCombined);
        graphs.add(randomGrowingBuf);

        try (var frame = new WidgetTestFrame(() -> {
            var outerSplit = new SplitContainer(false, 3);
            outerSplit.asTreeRoot();

            var leftPlaceholder = new ContainerWidget();
            leftPlaceholder.addDecoration(new Background(0xFF0D1117));
            outerSplit.setPanel(0, leftPlaceholder);

            var centerColumn = buildCenterColumn(graphs);
            outerSplit.setPanel(1, centerColumn);

            var rightPlaceholder = new ContainerWidget();
            rightPlaceholder.addDecoration(new Background(0xFF0D1117));
            outerSplit.setPanel(2, rightPlaceholder);

            return outerSplit;
        }) {
            @Override
            public void render() {
                frameCount++;
                if (frameCount % 8 == 0) {
                    updateAllGraphs(graphs);
                }
                super.render();
            }
        }) {
            frame.run();
        }
    }

    private static LineGraph makeGraph(
            LineGraph.AxisStrategy xStrat,
            LineGraph.AxisStrategy yStrat,
            LineGraph.YAxisMaxStrategy yMax,
            int lineColor,
            String name
    ) {
        var g = new LineGraph();
        g.xAxisStrategy = xStrat;
        g.yAxisStrategy = yStrat;
        g.yAxisMaxStrategy = yMax;
        g.lineColor = lineColor;
        g.bgColor = 0xFF0D1117;
        g.axisColor = 0xFF30363D;
        g.numColor = 0xFF8B949E;
        g.gridColor = 0x1AFFFFFF;
        g.name = name;
        return g;
    }

    private static ContainerWidget buildCenterColumn(List<LineGraph> graphs) {
        var verticalSplit = new SplitContainer(true, graphs.size());

        for (int i = 0; i < graphs.size(); i++) {
            var graph = graphs.get(i);
            var panel = wrapWithLabel(graph);
            verticalSplit.setPanel(i, panel);
        }
        return verticalSplit;
    }

    private static ContainerWidget wrapWithLabel(LineGraph graph) {
        var container = new ContainerWidget();
        container.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });

        var title = new Label(graph.name, dev.vfyjxf.taffy.style.TextAlign.LEFT, 0xFF8B949E);
        title.setStyle(s -> {
            s.size = TaffySize.of(TaffyDimension.percent(1f), TaffyDimension.length(14));
            s.flexShrink = 0;
        });
        container.addChild(title);

        var graphStyle = new TaffyStyle();
        graphStyle.size = TaffySize.all(TaffyDimension.percent(1f));
        graphStyle.minSize = TaffySize.all(TaffyDimension.ZERO);
        graph.setStyle(graphStyle);
        container.addChild(graph);

        return container;
    }

    private static final FloatList growingBuf = new FloatArrayList();
    private static int growingStep = 0;

    private static void updateAllGraphs(List<LineGraph> graphs) {
        int n = FIXED_BUF_SIZE;
        double t = growingStep * 0.1;

        graphs.get(0).setData(generateX(n, 1), generateSin(n, 1.0, t));

        FloatList sinPositive = generateSinPositive(n, 1.0, t);
        graphs.get(1).setData(generateX(n, 1), sinPositive);

        graphs.get(2).setData(generateX(n, 1), generateExp(n, 0.05, t));

        graphs.get(3).setData(generateXLog(n, 1, 100), generateExp(n, 0.05, t));

        graphs.get(4).setData(generateX(n, 1), generateRandom(n, 100f));

        graphs.get(5).setData(generateX(n, 1), generateLog(n, 1.0, t));

        graphs.get(6).setData(generateX(n, 1), generateSinExp(n, t));

        growingBuf.add((float) (50 + 40 * Math.sin(growingStep * 0.15) + RANDOM.nextFloat() * 20));
        FloatList xGrow = new FloatArrayList();
        for (int i = 0; i < growingBuf.size(); i++) {
            xGrow.add(i);
        }
        graphs.get(7).setData(xGrow, growingBuf);

        growingStep++;
    }

    private static FloatList generateX(int n, float step) {
        var list = new FloatArrayList(n);
        for (int i = 0; i < n; i++) {
            list.add(i * step);
        }
        return list;
    }

    private static FloatList generateXLog(int n, float xMin, float xMax) {
        var list = new FloatArrayList(n);
        double logMin = Math.log10(Math.max(xMin, 1e-10));
        double logMax = Math.log10(Math.max(xMax, 1e-10));
        for (int i = 0; i < n; i++) {
            double t = (double) i / (n - 1);
            list.add((float) Math.pow(10, logMin + t * (logMax - logMin)));
        }
        return list;
    }

    private static FloatList generateSin(int n, double amplitude, double offset) {
        var list = new FloatArrayList(n);
        for (int i = 0; i < n; i++) {
            list.add((float) (amplitude * Math.sin(i * 0.2 + offset) * 50 + 50));
        }
        return list;
    }

    private static FloatList generateSinPositive(int n, double amplitude, double offset) {
        var list = new FloatArrayList(n);
        for (int i = 0; i < n; i++) {
            double val = amplitude * Math.sin(i * 0.2 + offset) * 50 + 51;
            list.add((float) Math.max(val, 0.1));
        }
        return list;
    }

    private static FloatList generateExp(int n, double rate, double offset) {
        var list = new FloatArrayList(n);
        for (int i = 0; i < n; i++) {
            list.add((float) Math.exp(rate * i + Math.sin(offset) * 0.5));
        }
        return list;
    }

    private static FloatList generateLog(int n, double scale, double offset) {
        var list = new FloatArrayList(n);
        for (int i = 0; i < n; i++) {
            double val = scale * Math.log(i + 1 + Math.abs(Math.sin(offset)) * 5) * 10;
            list.add((float) val);
        }
        return list;
    }

    private static FloatList generateSinExp(int n, double offset) {
        var list = new FloatArrayList(n);
        for (int i = 0; i < n; i++) {
            double val = Math.sin(i * 0.3 + offset) * Math.exp(i * 0.03);
            list.add((float) (val + Math.abs(val) + 0.01));
        }
        return list;
    }

    private static FloatList generateRandom(int n, float maxVal) {
        var list = new FloatArrayList(n);
        for (int i = 0; i < n; i++) {
            list.add(RANDOM.nextFloat() * maxVal);
        }
        return list;
    }
}



