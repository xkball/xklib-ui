package com.xkball.xklib.ui.widget;

import com.xkball.xklib.XKLibWorkaround;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;

public class Button extends AbstractWidget{
    
    public static final int DEFAULT_COLOR = 0xFF7dd3fc;
    public static final int DEFAULT_HOVER_BORDER_COLOR = 0xFF0c4a6e;
    
    protected String text;
    protected Runnable onClick;
    protected ButtonStyle style;
    
    public Button(String text, Runnable onClick) {
        this(text, onClick, DefaultButtonStyles.ROUND_RECT);
    }
    
    public Button(String text, Runnable onClick, ButtonStyle style) {
        super();
        this.text = text;
        this.onClick = onClick;
        this.style = style;
    }
    
    public Button(int x, int y, int width, int height, String text, Runnable onClick) {
        this(x, y, width, height, text, onClick, DefaultButtonStyles.ROUND_RECT);
    }
    
    public Button(int x, int y, int width, int height, String text, Runnable onClick, ButtonStyle style) {
        super(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
        this.style = style;
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (this.onClick != null) {
            this.onClick.run();
        }
        return true;
    }
    
    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.style.render(graphics, this, mouseX, mouseY, partialTicks);
        var textHeight = (int) Math.min(this.contentHeight*0.9f, graphics.defaultFont().lineHeight());
        graphics.drawCenteredString(this.getText(), this.contentX + this.contentWidth/2, this.contentY + (this.contentHeight - textHeight)/2 - 2, 0xFF000000, textHeight);
    }
    
    @Override
    public int expectWidth() {
        return (int) (XKLibWorkaround.gui.getGuiGraphics().defaultFont().width(this.getText()) * 1.2f);
    }
    
    @Override
    public int expectHeight() {
        return (int) (XKLibWorkaround.gui.getGuiGraphics().defaultFont().lineHeight() * 1.2f);
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Runnable getOnClick() {
        return this.onClick;
    }
    
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
    
    public ButtonStyle getStyle() {
        return this.style;
    }
    
    public void setStyle(ButtonStyle style) {
        this.style = style;
    }
    
    public interface ButtonStyle{
        void render(IGUIGraphics graphics, Button button, int mouseX, int mouseY, float a);
    }
    
    public enum DefaultButtonStyles implements ButtonStyle{
        ROUND_RECT{
            @Override
            public void render(IGUIGraphics graphics, Button button, int mouseX, int mouseY, float a) {
                int x = button.contentX;
                int y = button.contentY;
                int w = button.contentWidth;
                int h = button.contentHeight;
                int radius = (int) (Math.max(w,h) * 0.2f);
                if (button.isHovered()) {
                    graphics.fillRounded(x - 2, y - 2, x + w + 2, y + h + 2, DEFAULT_HOVER_BORDER_COLOR, radius);
                }
                graphics.fillRounded(x, y, x + w, y + h, DEFAULT_COLOR, radius);
            }
        },
        RECT{
            @Override
            public void render(IGUIGraphics graphics, Button button, int mouseX, int mouseY, float a) {
                int x = button.contentX;
                int y = button.contentY;
                int w = button.contentWidth;
                int h = button.contentHeight;
                if (button.isHovered()) {
                    graphics.fill(x - 2, y - 2, x + w + 2, y + h + 2, DEFAULT_HOVER_BORDER_COLOR);
                }
                graphics.fill(x, y, x + w, y + h, DEFAULT_COLOR);
            }
        }
    }
}
