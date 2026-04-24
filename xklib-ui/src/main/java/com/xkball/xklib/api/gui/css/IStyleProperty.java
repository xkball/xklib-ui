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
    
    /**
     *  对于没有额外状态的属性, 返回自己
     *  对于有额外状态的属性, 返回自己的拷贝
     *  额外状态指不通过构造函数传入的状态, 比如在apply时获取的状态
     */
    default IStyleProperty<T> gatherInStyleSheet(){
        return this;
    }
}
