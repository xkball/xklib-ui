package com.xkball.xklibmc.x3d.backend.b3d.gui.element;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public class XKLibGuiTextRenderState extends GuiTextRenderState {
    
    public final float x_;
    public final float y_;
    public XKLibGuiTextRenderState(Font font, FormattedCharSequence text, Matrix3x2fc pose, float x, float y, int color, int backgroundColor, boolean dropShadow, boolean includeEmpty, @Nullable ScreenRectangle scissor) {
        super(font, text, pose, (int) x, (int) y, color, backgroundColor, dropShadow, includeEmpty, scissor);
        this.x_ = x;
        this.y_ = y;
    }
    
    public Font.PreparedText ensurePrepared() {
        if (this.preparedText == null) {
            this.preparedText = this.font.prepareText(this.text, this.x_, this.y_, this.color, this.dropShadow, this.includeEmpty, this.backgroundColor);
            ScreenRectangle bounds = this.preparedText.bounds();
            if (bounds != null) {
                bounds = bounds.transformMaxBounds(this.pose);
                this.bounds = this.scissor != null ? this.scissor.intersection(bounds) : bounds;
            }
        }
        
        return this.preparedText;
    }
}
