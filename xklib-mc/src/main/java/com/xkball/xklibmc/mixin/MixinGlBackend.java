package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.opengl.GlBackend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GlBackend.class)
public class MixinGlBackend {
    
//    @Inject(method = "setWindowHints", at = @At("RETURN"))
//    public void onCreateWindowTint(CallbackInfo ci){
//        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
//        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
//    }
    
    @ModifyConstant(method = "setWindowHints",constant = @Constant(intValue = 3, ordinal = 0))
    public int modifyMajorVersion(int constant){
        return 4;
    }
    
    @ModifyConstant(method = "setWindowHints",constant = @Constant(intValue = 3, ordinal = 1))
    public int modifyMinorVersion(int constant){
        return 6;
    }
}