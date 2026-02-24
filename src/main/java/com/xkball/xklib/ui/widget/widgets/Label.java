package com.xkball.xklib.ui.widget.widgets;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.AbstractWidget;

public class Label extends AbstractWidget {
    
    protected String text;
    protected int lineHeight = 20;
    protected int textColor = 0xFF000000;
    
    public Label() {
        super();
    }
    
    public Label(String text) {
        super();
        this.text = text;
    }
    
    public Label(String text, int lineHeight) {
        super();
        this.text = text;
        this.lineHeight = lineHeight;
    }
    
    public Label(String text, int lineHeight, int textColor) {
        super();
        this.text = text;
        this.lineHeight = lineHeight;
        this.textColor = textColor;
    }
    
    public String getText() {
        return this.text;
    }
    
    public Label setText(String text) {
        this.text = text;
        return this;
    }
    
    public int getLineHeight() {
        return this.lineHeight;
    }
    
    public Label setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        this.markDirty();
        return this;
    }
    
    public int getTextColor() {
        return this.textColor;
    }
    
    public Label setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }
    
    @Override
    public void resize() {
        this.contentX = this.x;
        this.contentY = this.y;
        this.contentWidth = this.lineHeight;
        this.contentHeight = this.lineHeight;
    }
    
    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if (this.text != null && !this.text.isEmpty()) {
            graphics.drawString(this.text, this.x, this.y, this.textColor, this.lineHeight);
        }
        super.render(graphics, mouseX, mouseY, a);
    }
    
    @Override
    public int expectWidth() {
        if (this.text == null || this.text.isEmpty()) {
            return 0;
        }
        var font = XKLib.gui.getGuiGraphics().defaultFont();
        float scale = this.lineHeight / (float) font.lineHeight();
        return (int) (font.width(this.text) * scale);
    }
    
    @Override
    public int expectHeight() {
        return this.lineHeight;
    }
    
    @Override
    public boolean isFocusable() {
        return false;
    }
}
