package com.xkball.xklib.ui.deco;

import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IDecoration;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.widget.AbstractWidget;

public record Background(int color) implements IDecoration {
    
    @Override
    public void render(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if(widget instanceof AbstractWidget aw){
            graphics.fill(aw.getContentX(), aw.getContentY(), aw.getContentX() + aw.getContentWidth(), aw.getContentY() + aw.getContentHeight(), this.color);
        }
        
    }
}
