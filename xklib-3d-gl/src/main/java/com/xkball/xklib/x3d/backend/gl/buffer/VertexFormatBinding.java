package com.xkball.xklib.x3d.backend.gl.buffer;

import com.xkball.xklib.x3d.backend.gl.GLStateManager;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import com.xkball.xklib.x3d.backend.vertex.VertexFormatElement;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL11C;

import java.util.HashMap;
import java.util.Map;

public class VertexFormatBinding {

    private static final ThreadLocal<Map<VertexFormat, VertexFormatBinding>> CACHE = ThreadLocal.withInitial(HashMap::new);

    private final int vao;
    private final VertexFormat format;

    private VertexFormatBinding(VertexFormat format) {
        this.format = format;
        this.vao = ARBDirectStateAccess.glCreateVertexArrays();

        for (int i = 0; i < format.getElements().size(); i++) {
            VertexFormatElement element = format.getElements().get(i);
            int offset = format.getOffset(element);
            int glType = getGLType(element.type());
            boolean normalized = element.type() == VertexFormatElement.Type.UBYTE || element.type() == VertexFormatElement.Type.BYTE;

            ARBDirectStateAccess.glEnableVertexArrayAttrib(vao, i);
            ARBDirectStateAccess.glVertexArrayAttribFormat(vao, i, element.count(), glType, normalized, offset);
            ARBDirectStateAccess.glVertexArrayAttribBinding(vao, i, 0);
        }
    }

    public static VertexFormatBinding getFor(VertexFormat format) {
        return CACHE.get().computeIfAbsent(format, VertexFormatBinding::new);
    }

    public void bind(GLGpuBuffer vbo, GLGpuBuffer ibo) {
        ARBDirectStateAccess.glVertexArrayVertexBuffer(vao, 0, vbo.handle(), 0, format.getVertexSize());
        ARBDirectStateAccess.glVertexArrayElementBuffer(vao, ibo.handle());
        GLStateManager.INSTANCE.get().bindVertexArray(vao);
    }

    public void bind(GLGpuBuffer vbo) {
        ARBDirectStateAccess.glVertexArrayVertexBuffer(vao, 0, vbo.handle(), 0, format.getVertexSize());
        GLStateManager.INSTANCE.get().bindVertexArray(vao);
    }

    public int getVao() {
        return vao;
    }

    private static int getGLType(VertexFormatElement.Type type) {
        return switch (type) {
            case FLOAT -> GL11C.GL_FLOAT;
            case UBYTE -> GL11C.GL_UNSIGNED_BYTE;
            case BYTE -> GL11C.GL_BYTE;
            case USHORT -> GL11C.GL_UNSIGNED_SHORT;
            case SHORT -> GL11C.GL_SHORT;
            case UINT -> GL11C.GL_UNSIGNED_INT;
            case INT -> GL11C.GL_INT;
        };
    }
}
