package com.xkball.xklib.ui.render;

//todo 宽度为float
public interface IFont {
    
    int width(String text);
    
    int lineHeight();
    
    default int width(String text, int lineHeight){
        return (int) (width(text) * ((float)lineHeight /(float) lineHeight()));
    }
}
