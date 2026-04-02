package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.logging.LogUtils;
import com.xkball.xklib.x3d.api.render.IRenderContext;
import com.xkball.xklib.x3d.api.render.IWindow;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Window.class)
public abstract class MixinWindow implements IWindow {
    
    @Final
    @Shadow
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @Shadow
    public abstract void updateVsync(boolean enableVsync);
    
    @Shadow
    @Final
    private long handle;
    
    @Override
    public long getHandle() {
        return this.handle;
    }
    
    @Override
    public IRenderContext init() {
        LOGGER.warn("not supported operation: xklib#init");
        return null;
    }
    
    @Override
    public void swapBuffer() {
        LOGGER.warn("not supported operation: xklib#swapBuffer");
    }
    
    
    @Override
    public void setVsync(boolean enabled) {
        this.updateVsync(enabled);
    }
}
