package com.xkball.xklib.api.gui.widget;

public interface IAbsoluteLayoutElement {
    
    float getAbsoluteX();
    
    float getAbsoluteY();
    
    void setAbsoluteX(float absoluteX);
    
    void setAbsoluteY(float absoluteY);
    
    void setAbsoluteSize(float x, float y);
    
    void setAbsoluteLayout(float x, float y, float width, float height);
    
}
