package com.xkball.xklib.ui.deco;

import com.xkball.xklib.api.gui.widget.IDecoration;

public class ButtonLooks {
    
    public static IDecoration roundRect(){
        return roundRect(0xFF7dd3fc,0xFF0c4a6e);
    }
    
    public static IDecoration rect(){
        return roundRect(0xFF7dd3fc,0xFF0c4a6e);
    }
    
    public static IDecoration transparent(){
        return transparent(0XCCF0F0F0);
    }
    
    public static IDecoration roundRect(int color, int hoverColor){
        return (widget, graphics, _, _, _) -> {
            var x = widget.getX();
            var y = widget.getY();
            var maxX = widget.getMaxX();
            var maxY = widget.getMaxY();
            int radius = (int) (Math.max(widget.getWidth(),widget.getHeight()) * 0.2f);
            if (widget.isHovered()) {
                graphics.fillRounded(x - 2, y - 2, maxX + 2, maxY + 2, hoverColor, radius);
            }
            graphics.fillRounded(x, y, maxX, maxY, color, radius);
        };
    }
    
    public static IDecoration rect(int color, int hoverColor){
        return (widget, graphics, _, _, _) -> {
            var x = widget.getX();
            var y = widget.getY();
            var maxX = widget.getMaxX();
            var maxY = widget.getMaxY();
            if (widget.isHovered()) {
                graphics.fill(x - 2, y - 2, maxX + 2, maxY + 2, hoverColor);
            }
            graphics.fill(x, y, maxX, maxY, color);
        };
    }
    
    public static IDecoration transparent(int hoverColor){
        return (widget, graphics, _, _, _) -> {
            if (widget.isHovered()) {
                graphics.fill(widget.getX(), widget.getY(), widget.getMaxX(), widget.getMaxY(), hoverColor);
            }
        };
    }
}
