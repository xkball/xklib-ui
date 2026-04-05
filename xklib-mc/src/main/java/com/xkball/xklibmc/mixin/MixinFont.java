package com.xkball.xklibmc.mixin;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IFont;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Font.class)
public abstract class MixinFont implements IFont {
    
    @Shadow
    @Final
    public int lineHeight;
    
    @Override
    public int width(IComponent component) {
        return 0;
    }
    
    @Override
    public int lineHeight() {
        return 16;
    }
}
