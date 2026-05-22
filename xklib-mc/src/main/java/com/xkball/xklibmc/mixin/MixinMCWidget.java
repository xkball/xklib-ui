package com.xkball.xklibmc.mixin;

import com.xkball.xklibmc.ui.XKLibBaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractWidget.class)
public class MixinMCWidget {

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;containsPointInScissor(II)Z"))
    public boolean onExtract(GuiGraphicsExtractor instance, int x, int y){
        return Minecraft.getInstance().screen instanceof XKLibBaseScreen || instance.containsPointInScissor(x, y);
    }
}
