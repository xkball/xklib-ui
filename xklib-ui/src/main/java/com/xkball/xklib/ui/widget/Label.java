package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.system.GuiSystem;
import dev.vfyjxf.taffy.style.TextAlign;

@SuppressWarnings("unused")
public class Label extends Widget {

    private static final String SELF_CSS = """
            label-text-color: 0xFF000000;
            """;
    
    private TextAlign align = TextAlign.LEFT;
    private IComponent text = IComponent.literal("");
    private int color = 0xFF000000;
    private TextScale textScale = TextScale.FIXED;

    public Label(){
        this.inlineStyle(SELF_CSS);
    }
    
    public Label(IComponent text, TextAlign align, int color){
        this.text = text;
        this.align = align;
        this.color = color;
        this.inlineStyle(SELF_CSS);
    }
    
    public Label(IComponent text, TextAlign align){
        this.text = text;
        this.align = align;
        this.inlineStyle(SELF_CSS);
    }
    
    public Label(IComponent text, int color){
        this.text = text;
        this.color = color;
        this.inlineStyle(SELF_CSS);
    }
    
    public Label(IComponent text) {
        this.text = text;
        this.inlineStyle(SELF_CSS);
    }

    public Label(String text, TextAlign align, int color){
        this.text = IComponent.literal(text);
        this.align = align;
        this.color = color;
        this.inlineStyle(SELF_CSS);
    }

    public Label(String text, TextAlign align){
        this.text = IComponent.literal(text);
        this.align = align;
        this.inlineStyle(SELF_CSS);
    }

    public Label(String text, int color){
        this.text = IComponent.literal(text);
        this.color = color;
        this.inlineStyle(SELF_CSS);
    }

    public Label(String text) {
        this.text = IComponent.literal(text);
        this.inlineStyle(SELF_CSS);
    }

    @Override
    public void init() {
        super.init();
        
    }
    
    public void onTextChanged(){
        this.submitTreeUpdate(() -> {
            var font = GuiSystem.INSTANCE.get().getGuiGraphics().defaultFont();
            this.tree.setMeasureFunc(this.nodeId,this.textScale.getMeasureFunc(font,text));
        });
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        var lineHeight = textScale.getTextHeight(graphics.defaultFont(),text, this.width - 4,this.height * 0.9f);
        var textY = this.y + (this.height - lineHeight)/2 - 2;
        switch (align){
            case LEFT:
                graphics.drawString(this.text, this.x + 2, textY, this.color, lineHeight);
                break;
            case CENTER:
                graphics.drawCenteredString(this.text, this.x + this.width/2f, textY, this.color, lineHeight);
                break;
            case RIGHT:
                var length = graphics.defaultFont().width(this.text);
                graphics.drawString(this.text, this.x + this.width - length - 2, textY, this.color, lineHeight);
                break;
        }
        
    }
    
    public IComponent getText() {
        return text;
    }

    public void setText(String text) {
        this.text = IComponent.literal(text);
        this.onTextChanged();
    }
    
    public int getColor() {
        return color;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public TextAlign getAlign() {
        return align;
    }
    
    public void setAlign(TextAlign align) {
        this.align = align;
        this.onTextChanged();
    }
    
    public TextScale getTextScale() {
        return textScale;
    }
    
    public void setTextScale(TextScale textScale) {
        this.textScale = textScale;
        this.onTextChanged();
    }
}
