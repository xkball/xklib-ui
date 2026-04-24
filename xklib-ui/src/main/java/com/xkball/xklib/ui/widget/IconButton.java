package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.render.IGUIGraphics;

@GuiWidgetClass
public class IconButton extends Widget {

    private final ResourceLocation sprite;
    private Runnable callback = () -> {};

    private int hoverOverlayColor = 0x88333333;
    private int backgroundColor = 0;

    public IconButton(ResourceLocation sprite, Runnable callback) {
        this.sprite = sprite;
        this.callback = callback;
    }

    public IconButton(ResourceLocation sprite) {
        this.sprite = sprite;
    }

    public Runnable getCallback() {
        return callback;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public void setHoverOverlayColor(int hoverOverlayColor) {
        this.hoverOverlayColor = hoverOverlayColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        float minX = this.x;
        float minY = this.y;
        float maxX = this.getMaxX();
        float maxY = this.getMaxY();
        float radius = Math.max(1, Math.min(this.width, this.height) / 4f);

        if (this.hovered) {
            graphics.fillRounded(minX, minY, maxX, maxY, this.hoverOverlayColor, radius);
        }
        if (this.backgroundColor != 0) {
            graphics.fillRounded(minX, minY, maxX, maxY, this.backgroundColor, radius);
        }

        graphics.blitSprite(this.sprite, minX, minY, this.width, this.height, -1);
    }

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        this.callback.run();
        return true;
    }
}

