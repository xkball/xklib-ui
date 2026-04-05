package com.xkball.xklibmc.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklibmc.annotation.NonnullByDefault;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@NonnullByDefault
public class XKLibMultiLineEditBox extends MultiLineEditBox {
    
    public XKLibMultiLineEditBox(Font font, int x, int y, int width, int height, Component placeholder, Component narration, int textColor, boolean textShadow, int cursorColor, boolean showBackground, boolean showDecorations) {
        super(font, x, y, width, height, placeholder, narration, textColor, textShadow, cursorColor, showBackground, showDecorations);
    }
    
    public XKLibMultiLineEditBox(){
        super(Minecraft.getInstance().font,
                0,0,0,0,
                Component.empty(), Component.empty(),
                -2039584,true,-3092272,true,true);
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
}
