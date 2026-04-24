package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.render.IGUIGraphics;

import java.util.Locale;

public class ButtonShapeProperty implements IStyleProperty<String> {
    
    private Shape shape;
    private int color = 0xFF7dd3fc;
    private int hoverColor = 0xFF0c4a6e;
    
    public enum Shape {
        RECT,
        ROUND_RECT,
        UNKNOWN;
    }
    
    public ButtonShapeProperty(Shape shape) {
        this.shape = shape;
    }
    
    public ButtonShapeProperty(String shape) {
        this.shape = parseShape(shape);
    }
    
    
    @Override
    public String propertyName() {
        return "button-shape";
    }
    
    @Override
    public String valueString() {
        return shape.name().toLowerCase(Locale.ROOT);
    }
    
    @Override
    public String value() {
        return shape.name().toLowerCase(Locale.ROOT);
    }
    
    @Override
    public void setValue(String value) {
        this.shape = parseShape(value);
    }
    
    @Override
    public void apply(IStyleSheet sheet, IGuiWidget widget) {
        if(sheet.getProperty("button-bg-color") instanceof WidgetIntStyleProperty<?> p){
            this.color = p.value();
        }
        if(sheet.getProperty("button-hover-color") instanceof ButtonHoverProperty p){
            this.hoverColor = p.value();
        }
    }
    
    @Override
    public IStyleProperty<String> gatherInStyleSheet() {
        var result = new ButtonShapeProperty(shape);
        result.color = color;
        result.hoverColor = hoverColor;
        return result;
    }
    
    @Override
    public boolean renderable() {
        return true;
    }
    
    @Override
    public void render(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        var x = widget.getX();
        var y = widget.getY();
        var maxX = widget.getMaxX();
        var maxY = widget.getMaxY();
        switch (shape) {
            case RECT:
                if (widget.isHovered()) {
                    graphics.fill(x - 2, y - 2, maxX + 2, maxY + 2, hoverColor);
                }
                graphics.fill(x, y, maxX, maxY, color);
                break;
            case ROUND_RECT:
                float radius = Math.min(widget.getWidth(),widget.getHeight()) * 0.4f;
                if (widget.isHovered()) {
                    graphics.fillRounded(x - 2, y - 2, maxX + 2, maxY + 2, hoverColor, radius);
                }
                graphics.fillRounded(x, y, maxX, maxY, color, radius);
                break;
        }
    }
    
    private static Shape parseShape(String shape) {
        String s = shape.trim().toLowerCase();
        if (s.equals("rect")) {
            return Shape.RECT;
        }
        if (s.equals("round_rect") || s.equals("round-rect") || s.equals("roundrect")) {
            return Shape.ROUND_RECT;
        }
        return Shape.UNKNOWN;
    }
}
