package com.xkball.xklib.api.gui.css;

import com.xkball.xklib.api.gui.widget.IDecoration;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.render.IGUIGraphics;

public interface IStyleProperty<T> extends IDecoration {
    
    String propertyName();
    
    String valueString();
    
    T value();
    
    void setValue(T value);
    
    void apply(IStyleSheet sheet, IGuiWidget widget);
    
    default boolean renderable(){
        return false;
    }
    
    @Override
    default void render(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a){
    }
}
