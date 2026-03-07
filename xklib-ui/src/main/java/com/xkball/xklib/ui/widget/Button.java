package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.render.IGUIGraphics;

public class Button extends Widget {
    
    private TextScale textScale = TextScale.FIXED;
    private String text = "";
    private int color = 0xFF000000;
    private Runnable callback = () -> {};
    
    public Button(String text, Runnable callback){
        this.text = text;
        this.callback = callback;
    }
    
    public Button() {}
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        var lineHeight = textScale.getTextHeight(graphics.defaultFont(),getText(),this.width*0.9f,this.height * 0.9f);
        graphics.drawCenteredString(this.getText(), this.x + this.width/2, this.y + (this.height - lineHeight)/2 - 2, color, lineHeight);
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        callback.run();
        return true;
    }
    
    public Runnable getCallback() {
        return callback;
    }
    
    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getColor(){
        return color;
    }
    
    public void setColor(int color){
        this.color = color;
    }
    
    public TextScale getTextScale() {
        return textScale;
    }
    
    public void setTextScale(TextScale textScale) {
        this.textScale = textScale;
    }
}
