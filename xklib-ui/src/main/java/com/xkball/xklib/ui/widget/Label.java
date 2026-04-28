package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.widget.ITextDisplayWidget;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.system.GuiSystem;
import dev.vfyjxf.taffy.style.TextAlign;

@GuiWidgetClass
@SuppressWarnings("unused")
public class Label extends Widget implements ITextDisplayWidget {
    
    protected IComponent text = IComponent.literal("");
    protected int color = 0xFF000000;
    public int lineHeight = 16;
    protected TextScale textScale = TextScale.FIXED;
    protected boolean dropShadow = true;
    protected float inlinePadding;

    public Label(){
    
    }
    
    public Label(IComponent text){
        this.text = text;
      
    }
    
    public Label(IComponent text, int color){
        this.text = text;
        this.color = color;
    }

    public Label(String text, int color){
        this.text = IComponent.literal(text);
        this.color = color;
    }

    public Label(String text) {
        this.text = IComponent.literal(text);
    }

    @Override
    public void init() {
        super.init();
        if(!(this.style.textAlign == TextAlign.LEFT ||  this.style.textAlign == TextAlign.RIGHT || this.style.textAlign == TextAlign.CENTER)){
            this.style.textAlign = TextAlign.LEFT;
        }
    }
    
    public void onTextChanged(){
        this.submitTreeUpdate(() -> {
            var font = GuiSystem.INSTANCE.get().getGuiGraphics().defaultFont();
            this.tree.setMeasureFunc(this.nodeId,this.textScale.getMeasureFunc(this.lineHeight, this.inlinePadding, font, text));
        });
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        var lineHeight = textScale.getTextHeight(this.lineHeight, graphics.defaultFont(), text, this.width - 4, this.height * 0.9f);
        var textY = this.y + (this.height - lineHeight)/2 + lineHeight * 0.1f;
        switch (style.textAlign){
            case LEFT:
                graphics.drawString(this.text, this.x + 2, textY, this.color, dropShadow, lineHeight);
                break;
            case CENTER:
                graphics.drawCenteredString(this.text, this.x + this.width/2f, textY, this.color, dropShadow, lineHeight);
                break;
            case RIGHT:
                var length = graphics.defaultFont().width(this.text);
                graphics.drawString(this.text, this.x + this.width - length - 2, textY, this.color,dropShadow, lineHeight);
                break;
        }
        
    }
    
    public IComponent getText() {
        return text;
    }

    public void setText(String text) {
        this.setText(IComponent.literal(text));
    }
    
    public void setText(IComponent text) {
        this.text = text;
        this.onTextChanged();
    }
    
    public int getColor() {
        return color;
    }
    
    public void setColor(int color) {
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
    
    @Override
    public Label setCSSClassName(String name) {
        super.setCSSClassName(name);
        return this;
    }
}
