package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.render.IGUIGraphics;

public class ButtonHoverProperty implements IStyleProperty<Integer> {
    
    private boolean haveShape = false;
    private int value;
    
    public ButtonHoverProperty(int value) {
        this.value = value;
    }
    
    @Override
    public String propertyName() {
        return "button-hover-color";
    }
    
    @Override
    public String valueString() {
        return String.valueOf(value);
    }
    
    @Override
    public Integer value() {
        return value;
    }
    
    @Override
    public void setValue(Integer value) {
        this.value = value;
    }
    
    @Override
    public void apply(IStyleSheet sheet, IGuiWidget widget) {
        this.haveShape = sheet.getProperty("button-shape") instanceof ButtonShapeProperty;
    }
    
    @Override
    public boolean renderable() {
        return true;
    }
    
    @Override
    public void render(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if(!haveShape && widget.isHovered()){
            graphics.fill(widget.getX(), widget.getY(), widget.getMaxX(), widget.getMaxY(), value);
        }
    }
}
