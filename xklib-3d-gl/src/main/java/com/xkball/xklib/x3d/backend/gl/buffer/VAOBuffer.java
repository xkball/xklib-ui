package com.xkball.xklib.x3d.backend.gl.buffer;

import com.xkball.xklib.x3d.backend.gl.GLStateManager;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import com.xkball.xklib.x3d.backend.vertex.VertexFormatElement;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.*;

@Deprecated
public class VAOBuffer implements AutoCloseable {
    private final int id;
    private boolean destroyed;
    
    public VAOBuffer() {
        this.id = glGenVertexArrays();
        this.destroyed = false;
    }
    
    public void bind() {
        if (destroyed) {
            throw new IllegalStateException("VAO has been destroyed");
        }
        GLStateManager.INSTANCE.get().bindVertexArray(id);
    }
    
    public static void unbind() {
        GLStateManager.INSTANCE.get().bindVertexArray(0);
    }
    
    public void setupVertexAttributes(VertexFormat format) {
        bind();
        for (int i = 0; i < format.getElements().size(); i++) {
            VertexFormatElement element = format.getElements().get(i);
            int offset = format.getOffset(element);
            
            glEnableVertexAttribArray(i);
            
            int glType = getGLType(element.type());
            boolean normalized = element.type() == VertexFormatElement.Type.UBYTE || element.type() == VertexFormatElement.Type.BYTE;
            
            glVertexAttribPointer(i, element.count(), glType, normalized, format.getVertexSize(), offset);
        }
    }
    
    private int getGLType(VertexFormatElement.Type type) {
        return switch (type) {
            case FLOAT -> GL_FLOAT;
            case UBYTE -> GL_UNSIGNED_BYTE;
            case BYTE -> GL_BYTE;
            case USHORT -> GL_UNSIGNED_SHORT;
            case SHORT -> GL_SHORT;
            case UINT -> GL_UNSIGNED_INT;
            case INT -> GL_INT;
        };
    }
    
    public int getId() {
        return id;
    }
    
    public void destroy() {
        if (!destroyed) {
            glDeleteVertexArrays(id);
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
