package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.ui.layout.HorizontalAlign;
import com.xkball.xklib.ui.layout.VerticalAlign;

/**
 * 盒模型的布局元素, 不支持子元素扩大父元素大小
 */
public interface ILayoutElement {
    
    int getContentX();
    
    int getContentY();
    
    int getContentWidth();
    
    int getContentHeight();

    void setFixWidth(int width);
    
    void setFixHeight(int height);
    
    void setInnerHorizontalAlign(HorizontalAlign align);
    
    void setInnerVerticalAlign(VerticalAlign align);
    
    void setPaddingLeft(int padding);
    
    void setPaddingRight(int padding);
    
    void setPaddingTop(int padding);
    
    void setPaddingBottom(int padding);
    
    void setPaddingLeftPercent(float percent);
    
    void setPaddingRightPercent(float percent);
    
    void setPaddingTopPercent(float percent);
    
    void setPaddingBottomPercent(float percent);
    
    void setMarginLeft(int margin);
    
    void setMarginRight(int margin);
    
    void setMarginTop(int margin);
    
    void setMarginBottom(int margin);
    
    void setMarginLeftPercent(float percent);
    
    void setMarginRightPercent(float percent);
    
    void setMarginTopPercent(float percent);
    
    void setMarginBottomPercent(float percent);

    default void setFixSize(int width, int height){
        this.setFixWidth(width);
        this.setFixHeight(height);
    }
    
    default void setInnerAlign(HorizontalAlign horizontal, VerticalAlign vertical){
        this.setInnerHorizontalAlign(horizontal);
        this.setInnerVerticalAlign(vertical);
    }
    
    default void setPadding(int left, int right, int top, int bottom){
        this.setPaddingLeft(left);
        this.setPaddingRight(right);
        this.setPaddingTop(top);
        this.setPaddingBottom(bottom);
    }
    
    default void setMargin(int left, int right, int top, int bottom){
        this.setMarginLeft(left);
        this.setMarginRight(right);
        this.setMarginTop(top);
        this.setMarginBottom(bottom);
    }
}
