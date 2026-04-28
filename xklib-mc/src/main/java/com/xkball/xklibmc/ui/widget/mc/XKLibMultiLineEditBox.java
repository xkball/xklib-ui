package com.xkball.xklibmc.ui.widget.mc;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.xkball.xklib.XKLib;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import dev.vfyjxf.taffy.style.TextAlign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

@NonNullByDefault
public class XKLibMultiLineEditBox extends MultiLineEditBox {
    
    public TextAlign textAlign = TextAlign.LEFT;
    
    public XKLibMultiLineEditBox(Font font, int x, int y, int width, int height, Component placeholder, Component narration, int textColor, boolean textShadow, int cursorColor, boolean showBackground, boolean showDecorations) {
        super(font, x, y, width, height, placeholder, narration, textColor, textShadow, cursorColor, showBackground, showDecorations);
    }
    
    public XKLibMultiLineEditBox(){
        super(Minecraft.getInstance().font,
                0,0,0,0,
                Component.empty(), Component.empty(),
                -2039584,true,-3092272,true,true);
    }
    
    public int getLineCount(){
        return this.textField.getLineCount();
    }
    
    private void onWidthChange(){
        this.textField.width = this.width;
        this.textField.onValueChange();
    }
    
    @Override
    public void setWidth(int width) {
        var old = this.width;
        super.setWidth(width);
        if(old != width) this.onWidthChange();
    }
    
    @Override
    public void setSize(int width, int height) {
        var old = this.width;
        super.setSize(width, height);
        if(old != width) this.onWidthChange();
    }
    
    @Override
    protected void extractBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        if(XKLib.RENDER_CONTEXT.get().getGUIGraphics() instanceof B3dGuiGraphics guiGraphics){
            Identifier sprite = BACKGROUND_SPRITES.get(this.isActive(), this.isFocused());
            guiGraphics.blitSprite((IRenderPipeline) RenderPipelines.GUI_TEXTURED, VanillaUtils.convertId(sprite), x, y, width, height, -1);
        }
        else {
            super.extractBorder(graphics,x,y,width,height);
        }
    }
    
    public TextAlign getTextAlign() {
        if(this.textAlign == TextAlign.CENTER) return TextAlign.CENTER;
        if(this.textAlign == TextAlign.RIGHT) return TextAlign.RIGHT;
        return TextAlign.LEFT;
    }
    
    public int getAlignOffset(int width){
        var w = this.textField.width;
        if(this.getTextAlign() == TextAlign.CENTER) return (w - this.innerPadding() - 2 - width) / 2;
        if(this.getTextAlign() == TextAlign.RIGHT) return w - this.innerPadding() - 2 - width;
        return 0;
    }
    
    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        String value = this.textField.value();
        if (value.isEmpty() && !this.isFocused()) {
            graphics.textWithWordWrap(
                    this.font, this.placeholder, this.getInnerLeft(), this.getInnerTop(), this.width - this.totalInnerPadding(), PLACEHOLDER_TEXT_COLOR
            );
        } else {
            int cursor = this.textField.cursor();
            boolean showCursor = this.isFocused() && TextCursorUtils.isCursorVisible(Util.getMillis() - this.focusedTime);
            boolean needsValidCursorPos = this.preeditOverlay != null;
            boolean insertCursor = cursor < value.length();
            int cursorX = 0;
            int cursorY = 0;
            int drawTop = this.getInnerTop();
            int innerLeft = this.getInnerLeft();
            boolean hasDrawnCursor = false;
            
            for (MultilineTextField.StringView lineView : this.textField.iterateLines()) {
                boolean lineWithinVisibleBounds = this.withinContentAreaTopBottom(drawTop, drawTop + 9);
                if (!hasDrawnCursor && (needsValidCursorPos || showCursor) && insertCursor && cursor >= lineView.beginIndex() && cursor <= lineView.endIndex()) {
                    if (lineWithinVisibleBounds) {
                        String textBeforeCursor = value.substring(lineView.beginIndex(), cursor);
                        String textAfterCursor = value.substring(cursor, lineView.endIndex());
                        var w1 = this.font.width(textBeforeCursor);
                        var w2 = this.font.width(textAfterCursor);
                        var px = innerLeft + getAlignOffset(w1 + w2);
                        int textBeforeCursorPosRight = px + w1;
                        graphics.text(this.font, textBeforeCursor, px, drawTop, this.textColor, this.textShadow);
                        graphics.text(this.font, textAfterCursor, textBeforeCursorPosRight, drawTop, this.textColor, this.textShadow);
                        cursorX = textBeforeCursorPosRight;
                        cursorY = drawTop;
                        if (showCursor) {
                            TextCursorUtils.extractInsertCursor(graphics, textBeforeCursorPosRight, drawTop, this.cursorColor, 9 + 1);
                        }
                        
                        hasDrawnCursor = true;
                    }
                } else if (lineWithinVisibleBounds) {
                    String substring = value.substring(lineView.beginIndex(), lineView.endIndex());
                    var w = this.font.width(substring);
                    var px = innerLeft + getAlignOffset(w);
                    graphics.text(this.font, substring, px, drawTop, this.textColor, this.textShadow);
                    if ((needsValidCursorPos || showCursor) && !insertCursor) {
                        cursorX = px;
                        cursorY = drawTop;
                    }
                }
                
                drawTop += 9;
            }
            
            if (showCursor && !insertCursor && this.withinContentAreaTopBottom(cursorY, cursorY + 9)) {
                TextCursorUtils.extractAppendCursor(graphics, this.font, cursorX, cursorY, this.cursorColor, this.textShadow);
            }
            
            if (this.textField.hasSelection()) {
                MultilineTextField.StringView selection = this.textField.getSelected();
                int drawX = this.getInnerLeft();
                drawTop = this.getInnerTop();
                
                for (MultilineTextField.StringView lineView : this.textField.iterateLines()) {
                    if (selection.beginIndex() <= lineView.endIndex()) {
                        if (lineView.beginIndex() > selection.endIndex()) {
                            break;
                        }
                        
                        if (this.withinContentAreaTopBottom(drawTop, drawTop + 9)) {
                            String substring = value.substring(lineView.beginIndex(), lineView.endIndex());
                            var px = this.getAlignOffset(this.font.width(substring));
                            int drawBegin = px + this.font.width(value.substring(lineView.beginIndex(), Math.max(selection.beginIndex(), lineView.beginIndex())));
                            int drawEnd;
                            if (selection.endIndex() > lineView.endIndex()) {
                                drawEnd = this.width - this.innerPadding();
                            } else {
                                drawEnd = px + this.font.width(value.substring(lineView.beginIndex(), selection.endIndex()));
                            }
                            
                            graphics.textHighlight(drawX + drawBegin, drawTop, drawX + drawEnd, drawTop + 9, true);
                        }
                        
                    }
                    drawTop += 9;
                }
            }
            
            if (this.isHovered()) {
                graphics.requestCursor(CursorTypes.IBEAM);
            }
            
            if (this.preeditOverlay != null) {
                this.preeditOverlay.updateInputPosition(cursorX, cursorY);
                graphics.setPreeditOverlay(this.preeditOverlay);
            }
        }
    }
}
