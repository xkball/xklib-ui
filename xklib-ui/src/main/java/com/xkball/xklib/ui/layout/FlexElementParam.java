package com.xkball.xklib.ui.layout;

import com.xkball.xklib.api.gui.widget.ILayoutParma;

public class FlexElementParam implements ILayoutParma {
    
    public int order;
    public SizeParam width;
    public SizeParam height;
    
    public FlexElementParam(int order, SizeParam width, SizeParam height) {
        this.order = order;
        this.width = width;
        this.height = height;
    }
    
    public int order() {
        return this.order;
    }
    
    public SizeParam width() {
        return this.width;
    }
    
    public SizeParam height() {
        return this.height;
    }

    public static FlexElementParam of(int order, SizeParam width, SizeParam height){
        return new FlexElementParam(order, width, height);
    }
    
    public static FlexElementParam of(int order, String width, String height){
        return new FlexElementParam(order, SizeParam.parse(width), SizeParam.parse(height));
    }
    
    public static FlexElementParam of(SizeParam width, SizeParam height){
        return new FlexElementParam(0, width, height);
    }
    
    public static FlexElementParam of(String width, String height){
        return new FlexElementParam(0,SizeParam.parse(width), SizeParam.parse(height));
    }
    
    @Override
    public String toString() {
        return "FlexElementParam{" +
                "order=" + order +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
