package com.xkball.xklib.x3d.backend.gl.buffer;

import com.xkball.xklib.x3d.backend.buffer.GpuBuffer;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL15C;

import java.nio.ByteBuffer;

public class GLGpuBuffer extends GpuBuffer {

    private final int id;
    private boolean closed;

    public GLGpuBuffer(int usage, long size, int glStorageFlags) {
        super(usage, size);
        this.id = ARBDirectStateAccess.glCreateBuffers();
        ARBDirectStateAccess.glNamedBufferStorage(this.id, size, glStorageFlags);
        this.closed = false;
    }

    public GLGpuBuffer(int usage, ByteBuffer data, int glStorageFlags) {
        super(usage, data.remaining());
        this.id = ARBDirectStateAccess.glCreateBuffers();
        ARBDirectStateAccess.glNamedBufferStorage(this.id, data, glStorageFlags);
        this.closed = false;
    }

    public int handle() {
        if (closed) {
            throw new IllegalStateException("Buffer has been closed");
        }
        return id;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (!closed) {
            GL15C.glDeleteBuffers(id);
            closed = true;
        }
    }
}
