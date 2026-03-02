package com.xkball.xklib.x3d.backend.gl.buffer;

import com.google.auto.service.AutoService;
import com.xkball.xklib.x3d.api.render.IBufferSource;
import com.xkball.xklib.x3d.api.render.IGpuBuffer;
import com.xkball.xklib.x3d.backend.buffer.GpuBuffer;
import com.xkball.xklib.x3d.backend.buffer.GpuBufferSlice;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL44C;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

@AutoService(IBufferSource.class)
public class GLBufferSource implements IBufferSource {

    @Override
    public GpuBuffer createBuffer(@Nullable Supplier<String> label, int usage, long size) {
        int flags = toGLStorageFlags(usage);
        return new GLGpuBuffer(usage, size, flags);
    }

    @Override
    public GpuBuffer createBuffer(@Nullable Supplier<String> label, int usage, ByteBuffer data) {
        int flags = toGLStorageFlags(usage);
        return new GLGpuBuffer(usage, data, flags);
    }

    @Override
    public void writeToBuffer(GpuBufferSlice destination, ByteBuffer data) {
        GLGpuBuffer glBuffer = asGLBuffer(destination.buffer());
        ARBDirectStateAccess.glNamedBufferSubData(glBuffer.handle(), destination.offset(), data);
    }

    @Override
    public GpuBuffer.MappedView mapBuffer(GpuBufferSlice buffer, boolean read, boolean write) {
        GLGpuBuffer glBuffer = asGLBuffer(buffer.buffer());
        int access = 0;
        if (read) access |= GL30C.GL_MAP_READ_BIT;
        if (write) access |= GL30C.GL_MAP_WRITE_BIT;
        ByteBuffer mapped = ARBDirectStateAccess.glMapNamedBufferRange(glBuffer.handle(), buffer.offset(), buffer.length(), access);
        if (mapped == null) {
            throw new IllegalStateException("Failed to map buffer");
        }
        int handle = glBuffer.handle();
        return new GpuBuffer.MappedView() {
            @Override
            public ByteBuffer data() {
                return mapped;
            }

            @Override
            public void close() {
                ARBDirectStateAccess.glUnmapNamedBuffer(handle);
            }
        };
    }

    @Override
    public void copyToBuffer(GpuBufferSlice source, GpuBufferSlice target) {
        GLGpuBuffer srcBuffer = asGLBuffer(source.buffer());
        GLGpuBuffer dstBuffer = asGLBuffer(target.buffer());
        long size = Math.min(source.length(), target.length());
        ARBDirectStateAccess.glCopyNamedBufferSubData(srcBuffer.handle(), dstBuffer.handle(), source.offset(), target.offset(), size);
    }

    private static GLGpuBuffer asGLBuffer(IGpuBuffer buffer) {
        if (buffer instanceof GLGpuBuffer glBuf) {
            return glBuf;
        }
        throw new IllegalArgumentException("Expected GLGpuBuffer but got " + buffer.getClass().getName());
    }

    private static int toGLStorageFlags(int usage) {
        int flags = 0;
        if ((usage & IGpuBuffer.USAGE_MAP_READ) != 0) {
            flags |= GL30C.GL_MAP_READ_BIT;
        }
        if ((usage & IGpuBuffer.USAGE_MAP_WRITE) != 0) {
            flags |= GL30C.GL_MAP_WRITE_BIT;
        }
        if ((usage & IGpuBuffer.USAGE_HINT_CLIENT_STORAGE) != 0) {
            flags |= GL44C.GL_CLIENT_STORAGE_BIT;
        }
        if ((usage & IGpuBuffer.USAGE_COPY_DST) != 0) {
            flags |= GL44C.GL_DYNAMIC_STORAGE_BIT;
        }
        return flags;
    }
}
