package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.ITextDisplayWidget;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.system.GuiSystem;

@GuiWidgetClass
public class Button extends Widget implements ITextDisplayWidget {
    
    private TextScale textScale = TextScale.FIXED;
    private String text = "";
    private int color = 0xFF000000;
    public int lineHeight = 16;
    private Runnable callback = () -> {};
    private boolean dropShadow = true;
    private float inlinePadding;
    
    public Button(String text, Runnable callback){
        this.text = text;
        this.callback = callback;
    }
    
    public Button() {
    }
    
    public void onTextChanged(){
        this.submitTreeUpdate(() -> {
            var font = GuiSystem.INSTANCE.get().getGuiGraphics().defaultFont();
            this.tree.setMeasureFunc(this.nodeId,this.textScale.getMeasureFunc(this.lineHeight,this.inlinePadding, font,text));
        });
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        var lineHeight = textScale.getTextHeight(this.lineHeight,graphics.defaultFont(),getText(),this.width*0.9f,this.height * 0.9f);
        graphics.drawCenteredString(this.getText(), this.x + this.width/2, this.y + (this.height - lineHeight)/2 + lineHeight * 0.1f , color, dropShadow, lineHeight);
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
        this.onTextChanged();
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
    
    @Override
    public void setLineHeight(float height) {
        this.lineHeight = (int) height;
        this.onTextChanged();
    }
    
    @Override
    public void setTextColor(int color) {
        this.color = color;
    }
    
    public void setTextScale(TextScale textScale) {
        this.textScale = textScale;
        this.onTextChanged();
    }
    
    @Override
    public void setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
    }
    
    @Override
    public void setExtraWidth(float width) {
        this.inlinePadding = width;
        this.onTextChanged();
    }
}
