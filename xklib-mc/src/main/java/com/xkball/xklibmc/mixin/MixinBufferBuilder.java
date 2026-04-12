package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.xkball.xklibmc.api.client.mixin.IExtendedBufferBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.LongConsumer;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements IExtendedBufferBuilder {
    @Shadow
    private long vertexPointer;
    
    @Shadow
    @Final
    private VertexFormat format;
    
    @Shadow
    protected abstract long beginElement(VertexFormatElement element);
    
    @Override
    public BufferBuilder setUnsafe(VertexFormatElement element, LongConsumer ptr) {
        if (vertexPointer == -1L) {
            throw new IllegalStateException("Must call addVertex first");
        }
        
        if (!format.contains(element)) {
//            LOGGER.warn("VertexFormat does not contain element {}, skipping", element);
            return (BufferBuilder)(Object) this;
        }
        this.beginElement(element);
        int offset = format.getOffset(element);
        ptr.accept(vertexPointer + offset);
        
        return (BufferBuilder)(Object) this;
    }
}
