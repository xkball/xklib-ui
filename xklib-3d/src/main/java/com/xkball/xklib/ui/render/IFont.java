package com.xkball.xklib.ui.render;

//todo 宽度为float
public interface IFont {
    
    int width(String text);
    
    int width(IComponent component);
    
    int lineHeight();
    
    default float width(String text, float lineHeight) {
        return width(text) * (lineHeight /(float) lineHeight());
    }
    
    default float width(IComponent text, float lineHeight){
        return width(text) * (lineHeight /(float) lineHeight());
    }
    
    @Deprecated
    default int width(String text, int lineHeight){
        return (int) (width(text) * ((float)lineHeight /(float) lineHeight()));
    }
    
    @Deprecated
    default int width(IComponent text, int lineHeight){
        return (int) (width(text) * ((float)lineHeight /(float) lineHeight()));
    }
}
