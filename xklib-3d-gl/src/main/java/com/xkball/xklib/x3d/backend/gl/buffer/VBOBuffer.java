package com.xkball.xklib.x3d.backend.gl.buffer;

import com.xkball.xklib.x3d.backend.gl.GLStateManager;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15C.*;

@Deprecated
public class VBOBuffer implements AutoCloseable {
    private final int id;
    private final int usage;
    private int size;
    private boolean destroyed;
    
    public VBOBuffer(int usage) {
        this.id = glGenBuffers();
        this.usage = usage;
        this.size = 0;
        this.destroyed = false;
    }
    
    public VBOBuffer() {
        this(GL_DYNAMIC_DRAW);
    }
    
    public void bind() {
        if (destroyed) {
            throw new IllegalStateException("VBO has been destroyed");
        }
        GLStateManager.bindBuffer(GL_ARRAY_BUFFER, id);
    }
    
    public static void unbind() {
        GLStateManager.bindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    public void upload(ByteBuffer data) {
        if (destroyed) {
            throw new IllegalStateException("VBO has been destroyed");
        }
        bind();
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        size = data.remaining();
    }
    
    public void upload(long offset, ByteBuffer data) {
        if (destroyed) {
            throw new IllegalStateException("VBO has been destroyed");
        }
        bind();
        glBufferSubData(GL_ARRAY_BUFFER, offset, data);
    }
    
    public void allocate(int size) {
        if (destroyed) {
            throw new IllegalStateException("VBO has been destroyed");
        }
        bind();
        glBufferData(GL_ARRAY_BUFFER, size, usage);
        this.size = size;
    }
    
    public int getId() {
        return id;
    }
    
    public int getSize() {
        return size;
    }
    
    public void destroy() {
        if (!destroyed) {
            glDeleteBuffers(id);
            destroyed = true;
        }
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void close() {
        destroy();
    }
}
