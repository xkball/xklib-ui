package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.ui.render.IGUIGraphics;

public interface IDecoration {
    
    default void render(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a){
    
    }
    
    default void renderAbove(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a){
    
    }
    
    default void renderBelow(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a){
    
    }
}
