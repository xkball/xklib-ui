package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IGuiEventListener;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.IRenderable;
import com.xkball.xklib.ui.navigation.ScreenRectangle;

public class AbstractWidget implements IGuiWidget, IRenderable, IGuiEventListener {
    
    
    @Override
    public void setFocused(boolean focused) {
    
    }
    
    @Override
    public boolean isFocused() {
        return false;
    }
    
    @Override
    public void setX(int x) {
    
    }
    
    @Override
    public void setY(int y) {
    
    }
    
    @Override
    public int getX() {
        return 0;
    }
    
    @Override
    public int getY() {
        return 0;
    }
    
    @Override
    public int getWidth() {
        return 0;
    }
    
    @Override
    public int getHeight() {
        return 0;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
    
    }
    
    @Override
    public boolean enabled() {
        return false;
    }
    
    @Override
    public void setVisible(boolean visible) {
    
    }
    
    @Override
    public boolean visible() {
        return false;
    }
    
    @Override
    public boolean isHovered() {
        return false;
    }
    
    @Override
    public ScreenRectangle getRectangle() {
        return IGuiWidget.super.getRectangle();
    }
    
    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
    
    }
}
