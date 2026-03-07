package com.xkball.xklib.ui.layout;

import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;

//TODO: 常用的迁移到这里
public class DefaultStyles {
    
    private static TaffyStyle defaultStyle(){
        return new TaffyStyle();
    }
    
    public static TaffyStyle fill(){
        return fill(defaultStyle());
    }
    
    public static TaffyStyle flexCenteredColumn(){
        return flexCenteredColum(defaultStyle());
    }
    
    public static TaffyStyle fill(TaffyStyle style){
        style.size = TaffySize.all(TaffyDimension.percent(1));
        return style;
    }
    
    public static TaffyStyle block(TaffyStyle style){
        style.display = TaffyDisplay.BLOCK;
        return style;
    }
    
    public static TaffyStyle flexCenteredColum(TaffyStyle style){
        style.display = TaffyDisplay.FLEX;
        style.flexDirection = FlexDirection.COLUMN;
        style.justifyContent = AlignContent.START;
        style.alignItems = AlignItems.CENTER;
        return style;
    }
}
