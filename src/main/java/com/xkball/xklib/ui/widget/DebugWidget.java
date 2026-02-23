package com.xkball.xklib.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.widget.layout.ScrollableFlexLayout;

import java.util.HashSet;
import java.util.Set;

public class DebugWidget extends ScrollableFlexLayout {
    
    private static final int LINE_HEIGHT = 16;
    private static final int TEXT_COLOR = 0xFFFF0000;
    private static final int BG_COLOR = 0xCC010101;
    
    private final Set<AbstractWidget> expandedWidgets = new HashSet<>();
    private int orderCounter = 0;
    
    public DebugWidget() {
        super(new FlexParam.Builder()
                .direction(FlexParam.Direction.COL)
                .justify(FlexParam.Align.START)
                .align(FlexParam.Align.END)
                .overflow(true)
                .build());
        this.yScrollable = true;
        this.setPaddingLeftPercent(0.6f);
        this.addDecoration(new Background(BG_COLOR));
    }
    
    @Override
    public void init() {
        super.init();
        this.rebuildTree();
    }
    
    private void rebuildTree() {
        this.inner.getChildren().forEach(this.inner::removeChild);
        this.orderCounter = 0;
        
        var guiSystem = XKLib.gui;
        for (int i = guiSystem.screenLayers.size() - 1; i >= 0; i--) {
            AbstractWidget layer = guiSystem.screenLayers.get(i);
            addWidgetEntry(layer, 0, "Layer " + i + ": ");
        }
        
        this.inner.markDirty();
        this.markDirty();
    }
    
    private void addWidgetEntry(AbstractWidget widget, int indent, String prefix) {
        boolean isContainer = widget instanceof AbstractContainerWidget<?, ?>;
        boolean isSelf = widget instanceof DebugWidget;
        boolean isExpanded = this.expandedWidgets.contains(widget);
        
        String icon;
        if (isSelf) {
            icon = "-";
        } else if (isContainer) {
            icon = isExpanded ? "V" : ">";
        } else {
            icon = " ";
        }
        
        String[] split = widget.getClass().getName().split("\\.");
        String className = split[split.length - 1];
        String text = " ".repeat(indent) + icon + " " + prefix + className;
        
        TreeEntryLabel label = new TreeEntryLabel(text, widget, isSelf, isContainer);
        this.inner.addChild(label, FlexElementParam.of(this.orderCounter++, SizeParam.parse("100%"), new SizeParam.Pixel(LINE_HEIGHT)));
        
        if (isExpanded && isContainer && !isSelf) {
            AbstractContainerWidget<?, ?> container = (AbstractContainerWidget<?, ?>) widget;
            for (AbstractWidget child : container.getChildren()) {
                addWidgetEntry(child, indent + 1, "");
            }
        }
    }
    
    private void toggleExpand(AbstractWidget widget) {
        if (this.expandedWidgets.contains(widget)) {
            this.expandedWidgets.remove(widget);
        } else {
            this.expandedWidgets.add(widget);
        }
        this.submitTreeUpdate(this::rebuildTree);
    }
    
    private class TreeEntryLabel extends AbstractWidget {
        
        private static final int CONTENT_COLOR = 0x400000FF;
        private static final int PADDING_COLOR = 0x40FF00FF;
        
        private final String text;
        private final AbstractWidget targetWidget;
        private final boolean isSelf;
        private final boolean isContainer;
        
        public TreeEntryLabel(String text, AbstractWidget targetWidget, boolean isSelf, boolean isContainer) {
            this.text = text;
            this.targetWidget = targetWidget;
            this.isSelf = isSelf;
            this.isContainer = isContainer;
        }
        
        @Override
        public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            super.render(graphics, mouseX, mouseY, a);
            if (this.hovered && !this.isSelf) {
                graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0x40FFFFFF);
                this.renderTargetDebugOverlay(graphics);
            }
            graphics.drawString(this.text, this.x, this.y, TEXT_COLOR, LINE_HEIGHT);
        }
        
        private void renderTargetDebugOverlay(IGUIGraphics graphics) {
            graphics.layerDown();
            int tx = this.targetWidget.getX();
            int ty = this.targetWidget.getY();
            int tw = this.targetWidget.getWidth();
            int th = this.targetWidget.getHeight();
            
            int cx = this.targetWidget.getContentX();
            int cy = this.targetWidget.getContentY();
            int cw = this.targetWidget.getContentWidth();
            int ch = this.targetWidget.getContentHeight();
            
            int paddingLeft = cx - tx;
            int paddingTop = cy - ty;
            int paddingRight = (tx + tw) - (cx + cw);
            int paddingBottom = (ty + th) - (cy + ch);
            
            graphics.hLine(0, XKLib.gui.screenWidth, cy, PADDING_COLOR);
            graphics.hLine(0, XKLib.gui.screenWidth, cy + ch, PADDING_COLOR);
            graphics.vLine(cx, 0, XKLib.gui.screenHeight, PADDING_COLOR);
            graphics.vLine(cx + cw, 0, XKLib.gui.screenHeight, PADDING_COLOR);
            graphics.fill(cx, cy, cx + cw, cy + ch, CONTENT_COLOR);
            
            if (paddingTop > 0) {
                graphics.fill(tx, ty, tx + tw, ty + paddingTop, PADDING_COLOR);
            }
            if (paddingBottom > 0) {
                graphics.fill(tx, ty + th - paddingBottom, tx + tw, ty + th, PADDING_COLOR);
            }
            if (paddingLeft > 0) {
                graphics.fill(tx, ty + paddingTop, tx + paddingLeft, ty + th - paddingBottom, PADDING_COLOR);
            }
            if (paddingRight > 0) {
                graphics.fill(tx + tw - paddingRight, ty + paddingTop, tx + tw, ty + th - paddingBottom, PADDING_COLOR);
            }
            graphics.layerUp();
        }
        
        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (!this.isSelf && this.isContainer) {
                DebugWidget.this.toggleExpand(this.targetWidget);
            }
            return true;
        }
        
        @Override
        public boolean isFocusable() {
            return false;
        }
    }
}
