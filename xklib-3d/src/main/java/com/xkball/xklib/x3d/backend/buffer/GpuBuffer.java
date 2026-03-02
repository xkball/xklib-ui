package com.xkball.xklib.x3d.backend.buffer;

import com.xkball.xklib.x3d.api.render.IGpuBuffer;

import java.nio.ByteBuffer;

public abstract class GpuBuffer implements AutoCloseable, IGpuBuffer {
    private final int usage;
    private final long size;

    public GpuBuffer(int usage, long size) {
        this.size = size;
        this.usage = usage;
    }

    @Override
    public long size() {
        return this.size;
    }


    @Override
    public int usage() {
        return this.usage;
    }

    @Override
    public abstract boolean isClosed();

    @Override
    public abstract void close();

    @Override
    public GpuBufferSlice slice(long offset, long length) {
        if (offset >= 0L && length >= 0L && offset + length <= this.size) {
            return new GpuBufferSlice(this, offset, length);
        } else {
            throw new IllegalArgumentException(
                "Offset of " + offset + " and length " + length + " would put new slice outside buffer's range (of 0," + length + ")"
            );
        }
    }
    
    public interface MappedView extends AutoCloseable {
        ByteBuffer data();

        @Override
        void close();
    }


}