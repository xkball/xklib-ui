package com.xkball.xklibmc.mixin;

import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IFont;
import com.xkball.xklibmc.x3d.backend.b3d.gui.ComponentConverter;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Font.class)
public abstract class MixinFont implements IFont {
    
    @Shadow
    @Final
    public int lineHeight;
    
    @Shadow
    public abstract int width(FormattedCharSequence text);
    
    @Override
    public int width(IComponent component) {
        return this.width(ComponentConverter.toComponent(component).getVisualOrderText());
    }
    
    @Override
    public int lineHeight() {
        return lineHeight;
    }
}
