package com.xkball.xklib.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.ITextDisplayWidget;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.render.SequenceComponent;
import com.xkball.xklib.ui.system.GuiSystem;
import dev.vfyjxf.taffy.style.TextAlign;
import org.jspecify.annotations.Nullable;

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
    public @Nullable IGuiWidget createTooltip(int mouseX, int mouseY) {
        if(this.text.style().tooltip() != null) {
            var tooltip = this.text.style().tooltip().get();
            if(tooltip instanceof IGuiWidget widget) return widget;
        }
        var c = findComponent(mouseX);
        if(c != null && c.style().tooltip() != null){
            var tooltip = c.style().tooltip().get();
            if(tooltip instanceof IGuiWidget widget) return widget;
        }
        return null;
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if(event.button() != 0) return false;
        if(this.text.style().clickEvent() != null) {
            this.text.style().clickEvent().run();
            return true;
        }
        var c = findComponent((float) event.x());
        if(c != null && c.style().clickEvent() != null) {
            c.style().clickEvent().run();
            return true;
        }
        return false;
    }
    
    private @Nullable IComponent findComponent(float mouseX){
        if(this.text instanceof SequenceComponent sc){
            float x = 0;
            float xLast = 0;
            float px = mouseX - this.x;
            var font = GuiSystem.INSTANCE.get().getGuiGraphics().defaultFont();
            var lineHeight = textScale.getTextHeight(this.lineHeight, font, text, this.width - 4, this.height * 0.9f);
            for(var i : sc.parts()){
                x += font.width(i, lineHeight);
                if(px > xLast && px < x){
                    return i;
                }
                xLast = x;
            }
        }
        return null;
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
    
    @Override
    public void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
        super.renderDebug(graphics, mouseX, mouseY);
        if(this.text instanceof SequenceComponent component){
            float x = 0;
            var lineHeight = textScale.getTextHeight(this.lineHeight, graphics.defaultFont(), text, this.width - 4, this.height * 0.9f);
            for(var i : component.parts()){
                x += graphics.defaultFont().width(i, lineHeight);
                graphics.vLine(this.x + x, this.getY(), this.getMaxY(),-1);
            }
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
