package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.api.gui.render.IGUIGraphics;

public interface IRenderable {
    /**
     * 重写时不需要检查自己的可见性, 调用者会检查
     */
    void render(IGUIGraphics graphics, int mouseX, int mouseY, float a);
    
    default void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
    }
}