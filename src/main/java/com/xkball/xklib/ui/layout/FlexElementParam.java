package com.xkball.xklib.ui.layout;

import com.xkball.xklib.api.gui.widget.ILayoutParma;

public record FlexElementParam(int order, SizeParam width, SizeParam height) implements ILayoutParma {

    public static FlexElementParam of(int order, SizeParam width, SizeParam height){
        return new FlexElementParam(order, width, height);
    }
    
    public static FlexElementParam of(int order, String width, String height){
        return new FlexElementParam(order, SizeParam.parse(width), SizeParam.parse(height));
    }
    
    public static FlexElementParam of(SizeParam width, SizeParam height){
        return new FlexElementParam(0, width, height);
    }
    
}
