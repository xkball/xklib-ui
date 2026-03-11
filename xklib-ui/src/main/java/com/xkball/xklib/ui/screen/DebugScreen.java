package com.xkball.xklib.ui.screen;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklib.ui.widget.container.TabContainer;
import com.xkball.xklib.utils.Pair;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DebugScreen extends ContainerWidget {

    private static final int NODE_HEIGHT = 20;

    private final GuiSystem theOtherSystem;
    private final TabContainer tabs = new TabContainer();
    private final SplitContainer treeView = new SplitContainer(false, 3);
    private final ContainerWidget treeViewContent = new ContainerWidget();
    private final ContainerWidget widgetDataPanel = new ContainerWidget();
    private final List<WeakReference<Widget>> openNodes = new ArrayList<>();
    private WidgetData lastSelectedNode = null;
    private boolean dirty = false;
    private boolean keepUpdating = false;

    public DebugScreen(GuiSystem theOtherSystem) {
        this.theOtherSystem = theOtherSystem;
    }
    
    @Override
    public void onRemove() {
        super.onRemove();
        if(!this.theOtherSystem.isClosed()){
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

        treeViewContent.addDecoration(new Background(0xFF1E2330));
        treeViewContent.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.START;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });
        treeViewContent.setYScrollEnable();
        treeViewContent.setXScrollEnable();

        treeView.getPanel(0).addDecoration(new Background(0xFF16213E));
        treeView.getPanel(1).addChild(treeViewContent);
        treeView.getPanel(2).addChild(widgetDataPanel);
        
        this.addChild(this.tabs.addTabPage(treeView, "查看器"));
        this.markDirty();
    }
    
    @Override
    public void markDirty(){
        this.dirty = true;
        super.markDirty();
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        if(this.keepUpdating) this.dirty = true;
        if(this.dirty){
            this.dirty = false;
            this.submitTreeUpdate(this::update);
        }
        super.resize(offsetX, offsetY);
    }
    
    public void update(){
        synchronized (theOtherSystem) {
            this.buildTreeView();
            this.buildWidgetDataPanel();
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
    
    private void buildWidgetDataPanel(){
        if(this.lastSelectedNode != null){
            this.widgetDataPanel.clearChildren();
            this.lastSelectedNode.buildContent(this.widgetDataPanel);
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

        var label = new NodeRow(widget, depth, text, TextAlign.LEFT, 0xFFCDD5E0);
        label.addDecoration(ButtonLooks.transparent(0x33FFFFFF));
        treeViewContent.addChild(label);

        if (isOpen) {
            for (var child : widget.getChildren()) {
                if (child instanceof Widget w) {
                    buildNodeRow(w, depth + 1);
                }
            }
        }
    }
    
    private class WidgetData{
        private final Widget target;
        /*
            int为深度, 防止循环引用导致无限递归展开
         */
        private final List<Pair<WeakReference<Object>,Integer>> opens = new ArrayList<>();
        
        private WidgetData(Widget target) {
            this.target = target;
        }
        
        public void buildContent(ContainerWidget container){
        
        }
    }

    private class NodeRow extends Label {

        private final Widget target;

        NodeRow(Widget target, int depth, String text, TextAlign align, int color) {
            super(text, align, color);
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
            DebugScreen.this.lastSelectedNode = new WidgetData(this.target);
            if (!target.getChildren().isEmpty()) {
                boolean wasOpen = openNodes.stream().anyMatch(r -> r.get() == target);
                if (wasOpen) {
                    openNodes.removeIf(r -> r.get() == target);
                } else {
                    openNodes.add(new WeakReference<>(target));
                }
                DebugScreen.this.markDirty();
                return true;
            }
            return false;
        }
    }
}
