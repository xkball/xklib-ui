package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.ui.layout.HorizontalAlign;
import com.xkball.xklib.ui.layout.SizeParam;
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
    
    void setPaddingLeft(SizeParam padding);
    
    void setPaddingRight(SizeParam padding);
    
    void setPaddingTop(SizeParam padding);
    
    void setPaddingBottom(SizeParam padding);
    
    void setMarginLeft(SizeParam margin);
    
    void setMarginRight(SizeParam margin);
    
    void setMarginTop(SizeParam margin);
    
    void setMarginBottom(SizeParam margin);
    
    default void setPaddingLeft(String padding){
        this.setPaddingLeft(SizeParam.parse(padding));
    }

    default void setPaddingRight(String padding){
        this.setPaddingRight(SizeParam.parse(padding));
    }
    
    default void setPaddingTop(String padding){
        this.setPaddingTop(SizeParam.parse(padding));
    }
    
    default void setPaddingBottom(String padding){
        this.setPaddingBottom(SizeParam.parse(padding));
    }
    
    default void setMarginLeft(String margin){
        this.setMarginLeft(SizeParam.parse(margin));
    }
    
    default void setMarginRight(String margin){
        this.setMarginRight(SizeParam.parse(margin));
    }
    
    default void setMarginTop(String margin){
        this.setMarginTop(SizeParam.parse(margin));
    }
    
    default void setMarginBottom(String margin){
        this.setMarginBottom(SizeParam.parse(margin));
    }
    
    default void setFixSize(int width, int height){
        this.setFixWidth(width);
        this.setFixHeight(height);
    }
    
    default void setInnerAlign(HorizontalAlign horizontal, VerticalAlign vertical){
        this.setInnerHorizontalAlign(horizontal);
        this.setInnerVerticalAlign(vertical);
    }
    
    default void setPadding(String left, String right, String top, String bottom){
        this.setPaddingLeft(left);
        this.setPaddingRight(right);
        this.setPaddingTop(top);
        this.setPaddingBottom(bottom);
    }
    
    default void setPadding(String padding){
        this.setPaddingLeft(padding);
        this.setPaddingRight(padding);
        this.setPaddingTop(padding);
        this.setPaddingBottom(padding);
    }
    
    default void setMargin(int left, int right, int top, int bottom){
        this.setMarginLeft(new SizeParam.Pixel(left));
        this.setMarginRight(new SizeParam.Pixel(right));
        this.setMarginTop(new SizeParam.Pixel(top));
        this.setMarginBottom(new SizeParam.Pixel(bottom));
    }
    
    default void setMargin(String left, String right, String top, String bottom){
        this.setMarginLeft(left);
        this.setMarginRight(right);
        this.setMarginTop(top);
        this.setMarginBottom(bottom);
    }
    
    default void setMargin(String margin){
        this.setMarginLeft(margin);
        this.setMarginRight(margin);
        this.setMarginTop(margin);
        this.setMarginBottom(margin);
    }
}
