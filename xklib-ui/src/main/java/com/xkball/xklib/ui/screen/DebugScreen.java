package com.xkball.xklib.ui.screen;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.CheckBox;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklib.ui.widget.container.TabContainer;
import com.xkball.xklib.utils.AdjacencyList;
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
    public static final IComponent ICON_RIGHT = IComponent.icon(ResourceLocation.of("textures/icon/right.png"));
    public static final IComponent ICON_DOWN = IComponent.icon(ResourceLocation.of("textures/icon/down.png"));
    private static final int TEXT_COLOR = 0xFF1E293B;
    private static final int HEADER_BG = 0xFFE2E8F0;
    private static final int PANEL_BG = 0xFFF8FAFC;
    private static final int HOVER_COLOR = 0x33000000;
    private static final String ROOT_CSS = """
            DebugScreen {
                size: 100% 100%;
                flex-direction: column;
                align-items: stretch;
            }
            ContainerWidget.debug-tree-content {
                flex-direction: column;
                align-items: start;
                size: 100% 100%;
            }
            ContainerWidget.debug-field-content {
                flex-direction: column;
                align-items: start;
                size: 100% 100%;
            }
            ContainerWidget.debug-tree-panel {
                flex-direction: column;
                align-items: stretch;
            }
            ContainerWidget.debug-field-panel {
                flex-direction: column;
                align-items: stretch;
            }
            Label.debug-header {
                size: 100% 22;
                flex-shrink: 0;
            }
            .debug-keep-updating-l {
                size: auto 100%;
                margin: 0 0 0 auto;
                padding: 4;
            }
            .debug-keep-updating-c {
                size: 48 24;
                margin: 4;
            }
            Label.debug-node-row {
                flex-direction: row;
                align-items: stretch;
                size: auto 22;
                flex-shrink: 0;
            }
            Label.debug-field-row {
                flex-direction: row;
                align-items: stretch;
                size: auto 22;
                flex-shrink: 0;
            }
            """;

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
    public String createCSSAsRoot() {
        return ROOT_CSS;
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

        treeViewContent.setCSSClassName("debug-tree-content");
        treeViewContent.addDecoration(new Background(PANEL_BG));
        treeViewContent.setYScrollEnable();
        treeViewContent.setXScrollEnable();

        fieldViewContent.setCSSClassName("debug-field-content");
        fieldViewContent.addDecoration(new Background(PANEL_BG));
        fieldViewContent.setYScrollEnable();
        fieldViewContent.setXScrollEnable();

        treeView.getPanel(0).addDecoration(new Background(PANEL_BG));
        treeView.getPanel(0).addChild(makeHeader(""));

        var treePanel = treeView.getPanel(1);
        treePanel.setCSSClassName("debug-tree-panel");
        treePanel.addDecoration(new Background(PANEL_BG));
        treePanel.addChild(makeHeader("组件树"));
        treePanel.addChild(treeViewContent);

        var fieldPanel = treeView.getPanel(2);
        fieldPanel.setCSSClassName("debug-field-panel");
        fieldPanel.addDecoration(new Background(PANEL_BG));
        fieldPanel.addChild(makeHeader("组件字段"));
        fieldPanel.addChild(fieldViewContent);

        this.addChild(this.tabs
                .addTabPage(treeView, "查看器")
                .addTabPage(performanceScreen, "性能监视器"));

        var keepUpdatingLabel = new Label("保持更新", TextAlign.LEFT, TEXT_COLOR);
        keepUpdatingLabel.setCSSClassName("debug-keep-updating-l");
        keepUpdatingLabel.setTextScale(TextScale.EXPAND_WIDTH);

        var keepUpdatingCb = new CheckBox();
        keepUpdatingCb.setCSSClassName("debug-keep-updating-c");
        keepUpdatingCb.bind(keepUpdating);

        this.tabs.getTabBar().addChild(keepUpdatingLabel);
        this.tabs.getTabBar().addChild(keepUpdatingCb);
        this.markDirty();
    }

    private Label makeHeader(String text) {
        var label = new Label(text, TextAlign.CENTER, TEXT_COLOR);
        label.setCSSClassName("debug-header");
        label.addDecoration(new Background(HEADER_BG));
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

        var prefix = IComponent.literal("  ".repeat(depth)).append(hasChildren ? (isOpen ? ICON_DOWN : ICON_RIGHT) : IComponent.literal("  "));
        var layout = widget.getLayout();
        var display = widget.getChildren().isEmpty() ? "" : widget.getStyle().display.toString();
        var size = widget.getStyle().size;
        String sizeStr = size != null ? String.format("w=%s,h=%s",size.width,size.height) : "null";
        String layoutStr = layout != null
                ? String.format("x=%.0f y=%.0f w=%.0f h=%.0f",
                        layout.contentBoxX(), layout.contentBoxY(),
                        layout.contentBoxWidth(), layout.contentBoxHeight())
                : "x=? y=? w=? h=?";
        var text = prefix.append(IComponent.literal(widget.getClass().getSimpleName() + " " + display + " " + sizeStr + "  " + layoutStr));

        var label = new NodeRow(widget, text, this.lastSelectedNode != null && widget == this.lastSelectedNode.target);
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
            if (obj instanceof Iterable<?> iterable) {
                buildIterableFields(container, iterable, depth, visited);
                return;
            }
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
                    boolean isExpandable = isExpandable(value);

                    boolean isOpen = isExpandable && openFields.getOrDefault(value, false);
                    boolean hasCycle = isExpandable && visited.containsKey(value);

                    var prefix = "  ".repeat(depth);
                    var valueStr = buildValueText(value, isExpandable, isOpen, hasCycle);
                    var text = IComponent.literal(prefix + field.getName() + ": ").append(valueStr);

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

        private void buildIterableFields(ContainerWidget container, Iterable<?> iterable, int depth, IdentityHashMap<Object, Boolean> visited) {
            int index = 0;
            for (Object value : iterable) {
                boolean isExpandable = isExpandable(value);
                boolean isOpen = isExpandable && openFields.getOrDefault(value, false);
                boolean hasCycle = isExpandable && visited.containsKey(value);
                var prefix = "  ".repeat(depth);
                var valueStr = buildValueText(value, isExpandable, isOpen, hasCycle);
                var text = IComponent.literal(prefix + "[" + index + "]: ").append(valueStr);
                var row = new FieldRow(text, isExpandable && !hasCycle ? value : null);
                row.addDecoration(ButtonLooks.transparent(HOVER_COLOR));
                container.addChild(row);
                if (isOpen && !hasCycle) {
                    visited.put(value, true);
                    buildObjectFields(container, value, depth + 1, visited);
                    visited.remove(value);
                }
                index++;
            }
        }

        private boolean isExpandable(Object value) {
            return value != null
                    && !(value instanceof String)
                    && !(value instanceof Number)
                    && !(value instanceof Boolean)
                    && !(value instanceof Character)
                    && !value.getClass().isPrimitive()
                    && !value.getClass().isEnum();
        }

        private IComponent buildValueText(Object value, boolean isExpandable, boolean isOpen, boolean hasCycle) {
            if (value == null) {
                return IComponent.literal("null");
            }
            if (hasCycle) {
                return IComponent.literal("<循环引用: " + value.getClass().getSimpleName() + ">");
            }
            if (isExpandable) {
                return (isOpen ? ICON_DOWN : ICON_RIGHT).append(IComponent.literal(value.getClass().getSimpleName()));
            }
            return IComponent.literal(String.valueOf(value));
        }

        private class FieldRow extends Label {
            private final Object fieldValue;

            FieldRow(IComponent text, Object fieldValue) {
                super(text, TextAlign.LEFT, TEXT_COLOR);
                this.fieldValue = fieldValue;
                this.setCSSClassName("debug-field-row");
            }

            @Override
            public void init() {
                super.init();
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
        private boolean isSelected;

        NodeRow(Widget target, IComponent text, boolean isSelected) {
            super(text, TextAlign.LEFT, TEXT_COLOR);
            this.target = target;
            this.isSelected = isSelected;
            this.setCSSClassName("debug-node-row");
        }

        @Override
        public void init() {
            super.init();
            this.setTextScale(TextScale.EXPAND_WIDTH);
        }
        
        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            if(this.isSelected){
                graphics.fill(this.x,this.y,this.x + this.width+10000,this.y + this.height,0x22267457);
            }
            super.doRender(graphics, mouseX, mouseY, a);
        }
        
        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            DebugScreen.this.markDirty();
            boolean doFocus = false;
            if(DebugScreen.this.lastSelectedNode == null || DebugScreen.this.lastSelectedNode.target != this.target){
                DebugScreen.this.lastSelectedNode = new WidgetData(this.target);
                doFocus = true;
            }
            if (!target.getChildren().isEmpty()) {
                boolean wasOpen = openNodes.stream().anyMatch(r -> r.get() == target);
                if (wasOpen) {
                    if(!doFocus) openNodes.removeIf(r -> r.get() == target);
                } else {
                    openNodes.add(new WeakReference<>(target));
                }
                return true;
            }
            return false;
        }
    }
}
