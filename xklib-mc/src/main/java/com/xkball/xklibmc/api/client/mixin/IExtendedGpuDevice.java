package com.xkball.xklibmc.api.client.mixin;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import org.jspecify.annotations.Nullable;

public interface IExtendedGpuDevice {
    
    default GpuTexture xklib$createSparseTexture(
            @Nullable String label, @GpuTexture.Usage final int usage, TextureFormat format, int width, int height, int depthOrLayers
    ){
        throw new UnsupportedOperationException();
    }

    static IExtendedGpuDevice cast(Object obj) {
        return (IExtendedGpuDevice)obj;
    }
}
