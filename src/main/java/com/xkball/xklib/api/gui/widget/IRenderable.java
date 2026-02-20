package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.api.gui.render.IGUIGraphics;

public interface IRenderable {
    void render(IGUIGraphics graphics, int mouseX, int mouseY, float a);
}