package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.api.gui.render.IGUIGraphics;

public interface IDecoration {
    
    void render(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a);
}
