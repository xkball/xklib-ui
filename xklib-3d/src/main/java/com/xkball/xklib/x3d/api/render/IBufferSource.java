package com.xkball.xklib.x3d.api.render;

import com.xkball.xklib.utils.Lazy;
import com.xkball.xklib.x3d.backend.buffer.GpuBuffer;
import com.xkball.xklib.x3d.backend.buffer.GpuBufferSlice;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public interface IBufferSource {
    
    Lazy<IBufferSource> INSTANCE = Lazy.ofSPI(IBufferSource.class);
    
    static IBufferSource getInstance() {
        return INSTANCE.get();
    }
    
    GpuBuffer createBuffer(@Nullable Supplier<String> label, int usage, long size);
    
    GpuBuffer createBuffer(@Nullable Supplier<String> label, int usage, ByteBuffer data);
    
    void writeToBuffer(GpuBufferSlice destination, ByteBuffer data);
    
    GpuBuffer.MappedView mapBuffer(GpuBufferSlice buffer, boolean read, boolean write);
    
    void copyToBuffer(GpuBufferSlice source, GpuBufferSlice target);
    
}
