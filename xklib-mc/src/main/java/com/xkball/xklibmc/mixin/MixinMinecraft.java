package com.xkball.xklibmc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    
    @Inject(method = "resizeGui", at = @At("RETURN"))
    public void afterResizeGui(CallbackInfo ci, @Local int guiScale){
        CssLengthUnit.rpxScaleWorkaround = guiScale;
    }
}
