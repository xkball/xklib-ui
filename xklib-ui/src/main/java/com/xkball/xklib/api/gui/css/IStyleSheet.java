package com.xkball.xklib.api.gui.css;

import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.css.CascadingStyleSheets;

import javax.annotation.Nullable;
import java.util.List;

public interface IStyleSheet {
    
    void update(CascadingStyleSheets sheets, IGuiWidget widget);
    
    @Nullable
    IStyleProperty<?> getProperty(String key);
    
    <T> T getValue(String key);
    
    List<IStyleProperty<?>> renderableProperty();
    
    @SuppressWarnings("unchecked")
    default <T> IStyleProperty<T> getProperty(String key, Class<T> clazz){
        return (IStyleProperty<T>) getProperty(key);
    }
}
