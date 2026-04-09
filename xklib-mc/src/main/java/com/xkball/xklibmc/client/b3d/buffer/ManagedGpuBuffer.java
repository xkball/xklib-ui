package com.xkball.xklibmc.client.b3d.buffer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;

public class ManagedGpuBuffer {
    
    public static final int BASE_SIZE = 4;
    public final int chunkSize;
    public GpuBuffer gpuBuffer;
    
    public ManagedGpuBuffer(int chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    public record Chunk(int id, GpuBufferSlice slice) {
    
    }
}
