package com.xkball.xklib.ui.screen;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.CheckBox;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklib.ui.widget.container.TabContainer;
import com.xkball.xklib.utils.AdjacencyList;
import dev.vfyjxf.taffy.geometry.TaffyRect;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.LengthPercentageAuto;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class DebugScreen extends ContainerWidget {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugScreen.class);
    private static final int NODE_HEIGHT = 22;
    private static final int TEXT_COLOR = 0xFF1E293B;
    private static final int HEADER_BG = 0xFFE2E8F0;
    private static final int PANEL_BG = 0xFFF8FAFC;
    private static final int HOVER_COLOR = 0x33000000;

    private final GuiSystem theOtherSystem;
    private final TabContainer tabs = new TabContainer();
    private final SplitContainer treeView = new SplitContainer(false, 3);
    private final ContainerWidget treeViewContent = new ContainerWidget();
    private final ContainerWidget fieldViewContent = new ContainerWidget();
    private final PerformanceScreen performanceScreen = new PerformanceScreen();
    private final List<WeakReference<Widget>> openNodes = new ArrayList<>();
    private WidgetData lastSelectedNode = null;
    private boolean dirty = false;
    private final BooleanLayoutVariable keepUpdating = new BooleanLayoutVariable(false);

    public DebugScreen(GuiSystem theOtherSystem) {
        this.theOtherSystem = theOtherSystem;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        if (!this.theOtherSystem.isClosed()) {
            theOtherSystem.debugScreen = null;
        }
    }

    @Override
    public void init() {
        super.init();
        this.setStyle(s -> {
            s.size = TaffySize.all(TaffyDimension.percent(1f));
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
        });

        treeViewContent.addDecoration(new Background(PANEL_BG));
        treeViewContent.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.START;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });
        treeViewContent.setYScrollEnable();
        treeViewContent.setXScrollEnable();

        fieldViewContent.addDecoration(new Background(PANEL_BG));
        fieldViewContent.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.START;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });
        fieldViewContent.setYScrollEnable();
        fieldViewContent.setXScrollEnable();

        treeView.getPanel(0).addDecoration(new Background(PANEL_BG));
        treeView.getPanel(0).addChild(makeHeader(""));

        var treePanel = treeView.getPanel(1);
        treePanel.addDecoration(new Background(PANEL_BG));
        treePanel.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
        });
        treePanel.addChild(makeHeader("组件树"));
        treePanel.addChild(treeViewContent);

        var fieldPanel = treeView.getPanel(2);
        fieldPanel.addDecoration(new Background(PANEL_BG));
        fieldPanel.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
        });
        fieldPanel.addChild(makeHeader("组件字段"));
        fieldPanel.addChild(fieldViewContent);

        this.addChild(this.tabs
                .addTabPage(treeView, "查看器")
                .addTabPage(performanceScreen, "性能监视器"));

        var keepUpdatingLabel = new Label("保持更新", TextAlign.LEFT, TEXT_COLOR);
        keepUpdatingLabel.setSize("auto","100%");
        keepUpdatingLabel.setStyle( s -> {
            s.margin = new TaffyRect<>(
                    LengthPercentageAuto.auto(),LengthPercentageAuto.length(0),
                    LengthPercentageAuto.length(0),LengthPercentageAuto.length(0));
            s.padding = TaffyRect.all(LengthPercentage.length(4));
        });
        keepUpdatingLabel.setTextScale(TextScale.EXPAND_WIDTH);

        var keepUpdatingCb = new CheckBox();
        keepUpdatingCb.bind(keepUpdating);
        keepUpdatingCb.setStyle( s -> s.margin = TaffyRect.all(LengthPercentageAuto.length(4)));
        keepUpdatingCb.setSize("48","24");

        this.tabs.getTabBar().addChild(keepUpdatingLabel);
        this.tabs.getTabBar().addChild(keepUpdatingCb);
        this.markDirty();
    }

    private Label makeHeader(String text) {
        var label = new Label(text, TextAlign.CENTER, TEXT_COLOR);
        label.addDecoration(new Background(HEADER_BG));
        label.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(NODE_HEIGHT));
            s.flexShrink = 0;
        });
        label.setTextScale(TextScale.EXPAND_WIDTH);
        return label;
    }
    
    public void updatePerformanceData(AdjacencyList<String,Long> data){
        this.performanceScreen.updateData(data);
    }

    @Override
    public void markDirty() {
        this.dirty = true;
        super.markDirty();
    }

    @Override
    public void resize(float offsetX, float offsetY) {
        if (this.keepUpdating.get()) this.dirty = true;
        if (this.dirty) {
            this.dirty = false;
            this.submitTreeUpdate(this::update);
        }
        super.resize(offsetX, offsetY);
    }

    public void update() {
        synchronized (theOtherSystem) {
            this.buildTreeView();
            this.buildFieldView();
        }
    }

    private void buildTreeView() {
        openNodes.removeIf(ref -> ref.get() == null);
        treeViewContent.clearChildren();
        for (var pair : theOtherSystem.screenLayers) {
            var root = pair.getFirst();
            if (root == this) continue;
            if (openNodes.stream().noneMatch(r -> r.get() == root)) {
                openNodes.add(new WeakReference<>(root));
            }
            buildNodeRow(root, 0);
        }
    }

    private void buildFieldView() {
        fieldViewContent.clearChildren();
        if (this.lastSelectedNode != null) {
            this.lastSelectedNode.buildContent(this.fieldViewContent);
        }
    }

    private void buildNodeRow(Widget widget, int depth) {
        var isOpen = openNodes.stream().anyMatch(r -> r.get() == widget);
        var hasChildren = !widget.getChildren().isEmpty();

        var prefix = "  ".repeat(depth) + (hasChildren ? (isOpen ? "▼ " : "▶ ") : "  ");
        var layout = widget.getLayout();
        var display = widget.getChildren().isEmpty() ? "" : widget.getStyle().display.toString();
        var size = widget.getStyle().size;
        String sizeStr = size != null ? String.format("w=%s,h=%s",size.width,size.height) : "null";
        String layoutStr = layout != null
                ? String.format("x=%.0f y=%.0f w=%.0f h=%.0f",
                        layout.contentBoxX(), layout.contentBoxY(),
                        layout.contentBoxWidth(), layout.contentBoxHeight())
                : "x=? y=? w=? h=?";
        var text = prefix + widget.getClass().getSimpleName() + " " + display + " " + sizeStr + "  " + layoutStr;

        var label = new NodeRow(widget, text);
        label.addDecoration(ButtonLooks.transparent(HOVER_COLOR));
        treeViewContent.addChild(label);

        if (isOpen) {
            for (var child : widget.getChildren()) {
                if (child instanceof Widget w) {
                    buildNodeRow(w, depth + 1);
                }
            }
        }
    }

    private class WidgetData {
        private final Widget target;
        private final Map<Object, Boolean> openFields = new IdentityHashMap<>();

        private WidgetData(Widget target) {
            this.target = target;
        }

        public void buildContent(ContainerWidget container) {
            buildObjectFields(container, target, 0, new IdentityHashMap<>());
        }

        private void buildObjectFields(ContainerWidget container, Object obj, int depth, IdentityHashMap<Object, Boolean> visited) {
            if (obj == null) return;
            Class<?> clazz = obj.getClass();
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    Object value;
                    try {
                        field.setAccessible(true);
                        value = field.get(obj);
                    } catch (Exception e) {
                        LOGGER.warn("can not access field {} of object {}", field.getName(), obj, e);
                        value = "<can not access>";
                    }
                    boolean isExpandable = value != null
                            && !(value instanceof String)
                            && !(value instanceof Number)
                            && !(value instanceof Boolean)
                            && !(value instanceof Character)
                            && !value.getClass().isPrimitive()
                            && !value.getClass().isEnum();

                    boolean isOpen = isExpandable && openFields.getOrDefault(value, false);
                    boolean hasCycle = isExpandable && visited.containsKey(value);

                    String prefix = "  ".repeat(depth);
                    String valueStr = value == null ? "null"
                            : hasCycle ? "<循环引用: " + value.getClass().getSimpleName() + ">"
                            : isExpandable ? (isOpen ? "▼ " : "▶ ") + value.getClass().getSimpleName()
                            : String.valueOf(value);
                    String text = prefix + field.getName() + ": " + valueStr;

                    final Object capturedValue = value;
                    var row = new FieldRow(text, isExpandable && !hasCycle ? capturedValue : null);
                    row.addDecoration(ButtonLooks.transparent(HOVER_COLOR));
                    container.addChild(row);

                    if (isOpen && !hasCycle) {
                        visited.put(capturedValue, true);
                        buildObjectFields(container, capturedValue, depth + 1, visited);
                        visited.remove(capturedValue);
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }

        private class FieldRow extends Label {
            private final Object fieldValue;

            FieldRow(String text, Object fieldValue) {
                super(text, TextAlign.LEFT, TEXT_COLOR);
                this.fieldValue = fieldValue;
            }

            @Override
            public void init() {
                super.init();
                this.setStyle(s -> {
                    s.flexDirection = FlexDirection.ROW;
                    s.alignItems = AlignItems.STRETCH;
                    s.size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.length(NODE_HEIGHT));
                    s.flexShrink = 0;
                });
                this.setTextScale(TextScale.EXPAND_WIDTH);
            }

            @Override
            protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
                if (fieldValue != null) {
                    boolean wasOpen = openFields.getOrDefault(fieldValue, false);
                    openFields.put(fieldValue, !wasOpen);
                    DebugScreen.this.markDirty();
                    return true;
                }
                return false;
            }
        }
    }

    private class NodeRow extends Label {

        private final Widget target;

        NodeRow(Widget target, String text) {
            super(text, TextAlign.LEFT, TEXT_COLOR);
            this.target = target;
        }

        @Override
        public void init() {
            super.init();
            this.setStyle(s -> {
                s.flexDirection = FlexDirection.ROW;
                s.alignItems = AlignItems.STRETCH;
                s.size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.length(NODE_HEIGHT));
                s.flexShrink = 0;
            });
            this.setTextScale(TextScale.EXPAND_WIDTH);
        }

        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            DebugScreen.this.markDirty();
            if(DebugScreen.this.lastSelectedNode == null || DebugScreen.this.lastSelectedNode.target != this.target){
                DebugScreen.this.lastSelectedNode = new WidgetData(this.target);
                return true;
            }
            if (!target.getChildren().isEmpty()) {
                boolean wasOpen = openNodes.stream().anyMatch(r -> r.get() == target);
                if (wasOpen) {
                    openNodes.removeIf(r -> r.get() == target);
                } else {
                    openNodes.add(new WeakReference<>(target));
                }
                return true;
            }
            return false;
        }
    }
}
