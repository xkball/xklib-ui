package com.xkball.xklib.ui.widget.widgets;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.widget.AbstractContainerWidget;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.layout.GridLayout;
import com.xkball.xklib.ui.widget.layout.ScrollableFlexLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class DebugWidget extends GridLayout {
    
    private static final int LINE_HEIGHT = 16;
    private static final int TEXT_COLOR = 0xFFFF0000;
    private static final int BG_COLOR = 0xCC010101;
    private static final int SELECTED_BG_COLOR = 0xFF404080;
    
    private final Set<AbstractWidget> expandedWidgets = new HashSet<>();
    private int orderCounter = 0;
    
    private ScrollableFlexLayout leftPanel;
    private ScrollableFlexLayout rightPanel;
    private AbstractWidget selectedWidget = null;
    private TreeEntryLabel selectedLabel = null;
    
    public DebugWidget() {
        super(new GridParam.Builder()
                .addCol("1")
                .addCol("1")
                .addRow("1")
                .build());
        this.setPaddingLeft("20%");
        this.addDecoration(new Background(BG_COLOR));
    }
    
    @Override
    public void init() {
        super.init();
        
        this.leftPanel = new ScrollableFlexLayout(new FlexParam.Builder()
                .direction(FlexParam.Direction.COL)
                .justify(FlexParam.Align.START)
                .align(FlexParam.Align.START)
                .overflow(true)
                .build());
        this.leftPanel.yScrollable = true;
        
        this.rightPanel = new ScrollableFlexLayout(new FlexParam.Builder()
                .direction(FlexParam.Direction.COL)
                .justify(FlexParam.Align.START)
                .align(FlexParam.Align.START)
                .overflow(true)
                .build());
        this.rightPanel.yScrollable = true;
        this.rightPanel.addDecoration(new Background(0xCC202020));
        
        this.addChild(this.leftPanel, new GridElementParam(0, 0, 1, 1));
        this.addChild(this.rightPanel, new GridElementParam(0, 1, 1, 1));
        
        this.rebuildTree();
    }
    
    private void rebuildTree() {
        this.leftPanel.inner.getChildren().forEach(this.leftPanel.inner::removeChild);
        this.orderCounter = 0;
        
        var guiSystem = XKLib.gui;
        for (int i = guiSystem.screenLayers.size() - 1; i >= 0; i--) {
            AbstractWidget layer = guiSystem.screenLayers.get(i);
            addWidgetEntry(layer, 0, "Layer " + i + ": ");
        }
        
        this.leftPanel.inner.markDirty();
        this.leftPanel.markDirty();
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
        this.leftPanel.inner.addChild(label, FlexElementParam.of(this.orderCounter++, SizeParam.parse("100%"), new SizeParam.Pixel(LINE_HEIGHT)));
        
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
    
    private void selectWidget(AbstractWidget widget, TreeEntryLabel label) {
        if (this.selectedLabel != null) {
            this.selectedLabel.selected = false;
        }
        this.selectedWidget = widget;
        this.selectedLabel = label;
        label.selected = true;
        this.submitTreeUpdate(this::rebuildRightPanel);
    }
    
    private void rebuildRightPanel() {
        this.rightPanel.inner.getChildren().forEach(this.rightPanel.inner::removeChild);
        
        if (this.selectedWidget == null) {
            this.rightPanel.inner.markDirty();
            this.rightPanel.markDirty();
            return;
        }
        
        int order = 0;
        
        String[] split = this.selectedWidget.getClass().getName().split("\\.");
        String className = split[split.length - 1];
        Label titleLabel = new Label("== " + className + " ==", LINE_HEIGHT, 0xFFFFFF00);
        this.rightPanel.inner.addChild(titleLabel, FlexElementParam.of(order++, SizeParam.parse("100%"), new SizeParam.Pixel(LINE_HEIGHT)));
        
        Class<?> clazz = this.selectedWidget.getClass();
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                
                String fieldText = getFieldText(field);
                Label fieldLabel = new Label(fieldText, LINE_HEIGHT, 0xFFCCCCCC);
                this.rightPanel.inner.addChild(fieldLabel, FlexElementParam.of(order++, SizeParam.parse("100%"), new SizeParam.Pixel(LINE_HEIGHT)));
            }
            clazz = clazz.getSuperclass();
        }
        
        this.rightPanel.inner.markDirty();
        this.rightPanel.markDirty();
    }
    
    private String getFieldText(Field field) {
        String name = field.getName();
        String value;
        try {
            field.setAccessible(true);
            Object obj = field.get(this.selectedWidget);
            if (obj == null) {
                value = "null";
            } else if (obj instanceof Number || obj instanceof Boolean || obj instanceof Character) {
                value = obj.toString();
            } else if (obj instanceof String) {
                String str = (String) obj;
                if (str.length() > 30) {
                    str = str.substring(0, 27) + "...";
                }
                value = "\"" + str + "\"";
            } else {
                String str = obj.toString();
                if (str.length() > 40) {
                    str = str.substring(0, 37) + "...";
                }
                value = str;
            }
        } catch (Exception e) {
            value = "<error>";
        }
        return "  " + name + ": " + value;
    }
    
    private class TreeEntryLabel extends AbstractWidget {
        
        private static final int CONTENT_COLOR = 0x400000FF;
        private static final int PADDING_COLOR = 0x40FF00FF;
        private static final int MARGIN_COLOR = 0x40FFA500;
        
        private final String text;
        private final AbstractWidget targetWidget;
        private final boolean isSelf;
        private final boolean isContainer;
        private boolean selected = false;
        
        public TreeEntryLabel(String text, AbstractWidget targetWidget, boolean isSelf, boolean isContainer) {
            this.text = text;
            this.targetWidget = targetWidget;
            this.isSelf = isSelf;
            this.isContainer = isContainer;
        }
        
        @Override
        public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            super.render(graphics, mouseX, mouseY, a);
            
            if (this.selected) {
                graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, SELECTED_BG_COLOR);
            } else if (this.hovered && !this.isSelf) {
                graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0x40FFFFFF);
            }
            
            if (this.hovered && !this.isSelf) {
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
            
            var marginRect = this.targetWidget.marginRect;
            if (marginRect != null) {
                int mx = marginRect.left();
                int my = marginRect.top();
                int mw = marginRect.width();
                int mh = marginRect.height();
                
                int marginLeft = tx - mx;
                int marginTop = ty - my;
                int marginRight = (mx + mw) - (tx + tw);
                int marginBottom = (my + mh) - (ty + th);
                
                if (marginTop > 0) {
                    graphics.fill(mx, my, mx + mw, my + marginTop, MARGIN_COLOR);
                }
                if (marginBottom > 0) {
                    graphics.fill(mx, my + mh - marginBottom, mx + mw, my + mh, MARGIN_COLOR);
                }
                if (marginLeft > 0) {
                    graphics.fill(mx, my + marginTop, mx + marginLeft, my + mh - marginBottom, MARGIN_COLOR);
                }
                if (marginRight > 0) {
                    graphics.fill(mx + mw - marginRight, my + marginTop, mx + mw, my + mh - marginBottom, MARGIN_COLOR);
                }
            }
            
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
            if (this.isSelf) {
                return true;
            }
            
            if (doubleClick) {
                DebugWidget.this.selectWidget(this.targetWidget, this);
            } else if (this.isContainer) {
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
