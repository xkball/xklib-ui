package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Std140SizeCalculator.class)
public class MixinSTD140Calculator {
    
    @ModifyConstant(method = "putVec3", constant = @Constant(intValue = 16, ordinal = 1))
    public int fixVec3Align(int constant){
        return 12;
    }

}
