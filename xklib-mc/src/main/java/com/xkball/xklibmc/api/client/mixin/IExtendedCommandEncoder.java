package com.xkball.xklibmc.api.client.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlRenderPass;

public interface IExtendedCommandEncoder {

    void xklib$multiDrawElementsIndirect(GlRenderPass renderPass, GpuBuffer command, int drawCount);
}
