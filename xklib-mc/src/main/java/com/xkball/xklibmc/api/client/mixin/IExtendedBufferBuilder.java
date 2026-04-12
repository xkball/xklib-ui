package com.xkball.xklibmc.api.client.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import java.util.function.LongConsumer;

public interface IExtendedBufferBuilder {
    BufferBuilder setUnsafe(VertexFormatElement element, LongConsumer ptr);
}
