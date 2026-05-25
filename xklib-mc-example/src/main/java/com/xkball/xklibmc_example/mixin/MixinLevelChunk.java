package com.xkball.xklibmc_example.mixin;

import com.xkball.xklibmc_example.XKLibMCExample;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class MixinLevelChunk {

    @Inject(method = "markUnsaved", at = @At("RETURN"))
    public void onMarkDirty(CallbackInfo ci){
        XKLibMCExample.MARK_DIRTY_CALLBACK.accept((LevelChunk)(Object) this);
    }
}
