package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.systems.GpuDeviceBackend;
import com.xkball.xklibmc.api.client.mixin.IExtendedGpuDevice;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GpuDeviceBackend.class)
public interface MixinGpuDeviceBackend extends IExtendedGpuDevice {
}
