package com.xkball.xklib.ui.render;

public interface IFont {
    
    int width(String text);
    
    int lineHeight();
    
    default int width(String text, int lineHeight){
        return width(text) * (lineHeight / lineHeight());
    }
}
