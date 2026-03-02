package com.xkball.xklib.x3d.api.render;

import com.xkball.xklib.x3d.backend.buffer.GpuBufferSlice;

public interface IGpuBuffer {
    int USAGE_MAP_READ = 1;
    int USAGE_MAP_WRITE = 2;
    int USAGE_HINT_CLIENT_STORAGE = 4;
    int USAGE_COPY_DST = 8;
    int USAGE_COPY_SRC = 16;
    int USAGE_VERTEX = 32;
    int USAGE_INDEX = 64;
    int USAGE_UNIFORM = 128;
    int USAGE_UNIFORM_TEXEL_BUFFER = 256;
    
    long size();
    
    int usage();
    
    boolean isClosed();
    
    GpuBufferSlice slice(long offset, long length);
    
    default GpuBufferSlice slice() {
        return new GpuBufferSlice(this, 0L, this.size());
    }
}
