import com.xkball.xklib.ui.backend.window.Window;
import com.xkball.xklib.ui.backend.gl.buffer.IBOBuffer;
import com.xkball.xklib.ui.backend.gl.buffer.VAOBuffer;
import com.xkball.xklib.ui.backend.gl.buffer.VBOBuffer;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormat;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormats;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.GL_VERTEX_ARRAY_BINDING;

class BufferTest {
    private static Window window;

    @BeforeAll
    static void setup() {
        window = new Window(64, 64, "buffer-test");
        window.init();
    }

    @AfterAll
    static void cleanup() {
        if (window != null) {
            window.destroy();
        }
    }

    @Test
    void vboCreatesAndDestroysBuffer() {
        VBOBuffer vbo = new VBOBuffer();
        try {
            assertTrue(vbo.getId() > 0);
            assertFalse(vbo.isDestroyed());
            assertEquals(0, vbo.getSize());
        } finally {
            vbo.destroy();
        }
        assertTrue(vbo.isDestroyed());
    }

    @Test
    void vboBindsAndUnbinds() {
        VBOBuffer vbo = new VBOBuffer();
        try {
            vbo.bind();
            int boundBuffer = glGetInteger(GL_ARRAY_BUFFER_BINDING);
            assertEquals(vbo.getId(), boundBuffer);
            
            VBOBuffer.unbind();
            boundBuffer = glGetInteger(GL_ARRAY_BUFFER_BINDING);
            assertEquals(0, boundBuffer);
        } finally {
            vbo.destroy();
        }
    }

    @Test
    void vboUploadsData() {
        VBOBuffer vbo = new VBOBuffer();
        try {
            // Create test data: triangle vertices (x, y, z)
            ByteBuffer data = MemoryUtil.memAlloc(9 * Float.BYTES);
            try {
                data.putFloat(0.0f).putFloat(0.5f).putFloat(0.0f);
                data.putFloat(-0.5f).putFloat(-0.5f).putFloat(0.0f);
                data.putFloat(0.5f).putFloat(-0.5f).putFloat(0.0f);
                data.flip();
                
                vbo.upload(data);
                assertEquals(9 * Float.BYTES, vbo.getSize());
            } finally {
                MemoryUtil.memFree(data);
            }
        } finally {
            vbo.destroy();
        }
    }

    @Test
    void vboAllocatesStorage() {
        VBOBuffer vbo = new VBOBuffer();
        try {
            vbo.allocate(1024);
            assertEquals(1024, vbo.getSize());
        } finally {
            vbo.destroy();
        }
    }

    @Test
    void vboReplacesDataOnUpload() {
        VBOBuffer vbo = new VBOBuffer();
        try {
            ByteBuffer data1 = MemoryUtil.memAlloc(3 * Float.BYTES);
            try {
                data1.putFloat(1.0f).putFloat(2.0f).putFloat(3.0f);
                data1.flip();
                vbo.upload(data1);
                assertEquals(3 * Float.BYTES, vbo.getSize());
            } finally {
                MemoryUtil.memFree(data1);
            }
            
            ByteBuffer data2 = MemoryUtil.memAlloc(6 * Float.BYTES);
            try {
                data2.putFloat(4.0f).putFloat(5.0f).putFloat(6.0f);
                data2.putFloat(7.0f).putFloat(8.0f).putFloat(9.0f);
                data2.flip();
                vbo.upload(data2);
                assertEquals(6 * Float.BYTES, vbo.getSize());
            } finally {
                MemoryUtil.memFree(data2);
            }
            
            ByteBuffer data3 = MemoryUtil.memAlloc(2 * Float.BYTES);
            try {
                data3.putFloat(10.0f).putFloat(11.0f);
                data3.flip();
                vbo.upload(data3);
                assertEquals(2 * Float.BYTES, vbo.getSize());
            } finally {
                MemoryUtil.memFree(data3);
            }
        } finally {
            vbo.destroy();
        }
    }

    @Test
    void vboThrowsExceptionWhenDestroyedAndUsed() {
        VBOBuffer vbo = new VBOBuffer();
        vbo.destroy();
        
        assertThrows(IllegalStateException.class, vbo::bind);
    }

    @Test
    void iboCreatesAndDestroysBuffer() {
        IBOBuffer ibo = new IBOBuffer();
        try {
            assertTrue(ibo.getId() > 0);
            assertFalse(ibo.isDestroyed());
            assertEquals(0, ibo.getSize());
        } finally {
            ibo.destroy();
        }
        assertTrue(ibo.isDestroyed());
    }

    @Test
    void iboBindsAndUnbinds() {
        IBOBuffer ibo = new IBOBuffer();
        try {
            ibo.bind();
            int boundBuffer = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
            assertEquals(ibo.getId(), boundBuffer);
            
            IBOBuffer.unbind();
            boundBuffer = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
            assertEquals(0, boundBuffer);
        } finally {
            ibo.destroy();
        }
    }

    @Test
    void iboUploadsIndices() {
        IBOBuffer ibo = new IBOBuffer();
        try {
            // Create test indices
            ByteBuffer data = MemoryUtil.memAlloc(3 * Integer.BYTES);
            try {
                data.putInt(0).putInt(1).putInt(2);
                data.flip();
                
                ibo.upload(data);
                assertEquals(3 * Integer.BYTES, ibo.getSize());
            } finally {
                MemoryUtil.memFree(data);
            }
        } finally {
            ibo.destroy();
        }
    }

    @Test
    void iboThrowsExceptionWhenDestroyedAndUsed() {
        IBOBuffer ibo = new IBOBuffer();
        ibo.destroy();
        
        assertThrows(IllegalStateException.class, ibo::bind);
    }

    @Test
    void iboReplacesDataOnUpload() {
        IBOBuffer ibo = new IBOBuffer();
        try {
            ByteBuffer data1 = MemoryUtil.memAlloc(3 * Integer.BYTES);
            try {
                data1.putInt(0).putInt(1).putInt(2);
                data1.flip();
                ibo.upload(data1);
                assertEquals(3 * Integer.BYTES, ibo.getSize());
            } finally {
                MemoryUtil.memFree(data1);
            }
            
            ByteBuffer data2 = MemoryUtil.memAlloc(6 * Integer.BYTES);
            try {
                data2.putInt(0).putInt(1).putInt(2).putInt(3).putInt(4).putInt(5);
                data2.flip();
                ibo.upload(data2);
                assertEquals(6 * Integer.BYTES, ibo.getSize());
            } finally {
                MemoryUtil.memFree(data2);
            }
            
            ByteBuffer data3 = MemoryUtil.memAlloc(2 * Integer.BYTES);
            try {
                data3.putInt(0).putInt(1);
                data3.flip();
                ibo.upload(data3);
                assertEquals(2 * Integer.BYTES, ibo.getSize());
            } finally {
                MemoryUtil.memFree(data3);
            }
        } finally {
            ibo.destroy();
        }
    }

    @Test
    void vaoCreatesAndDestroysBuffer() {
        VAOBuffer vao = new VAOBuffer();
        try {
            assertTrue(vao.getId() > 0);
            assertFalse(vao.isDestroyed());
        } finally {
            vao.destroy();
        }
        assertTrue(vao.isDestroyed());
    }

    @Test
    void vaoBindsAndUnbinds() {
        VAOBuffer vao = new VAOBuffer();
        try {
            vao.bind();
            int boundVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
            assertEquals(vao.getId(), boundVao);
            
            VAOBuffer.unbind();
            boundVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
            assertEquals(0, boundVao);
        } finally {
            vao.destroy();
        }
    }

    @Test
    void vaoSetupVertexAttributes() {
        VAOBuffer vao = new VAOBuffer();
        VBOBuffer vbo = new VBOBuffer();
        try {
            VertexFormat format = VertexFormats.POSITION_COLOR;
            
            // Create and upload test data
            ByteBuffer data = MemoryUtil.memAlloc(3 * format.getVertexSize());
            try {
                // Vertex 1: position (0, 0.5, 0), color (255, 0, 0, 255)
                data.putFloat(0.0f).putFloat(0.5f).putFloat(0.0f);
                data.put((byte) 255).put((byte) 0).put((byte) 0).put((byte) 255);
                
                // Vertex 2: position (-0.5, -0.5, 0), color (0, 255, 0, 255)
                data.putFloat(-0.5f).putFloat(-0.5f).putFloat(0.0f);
                data.put((byte) 0).put((byte) 255).put((byte) 0).put((byte) 255);
                
                // Vertex 3: position (0.5, -0.5, 0), color (0, 0, 255, 255)
                data.putFloat(0.5f).putFloat(-0.5f).putFloat(0.0f);
                data.put((byte) 0).put((byte) 0).put((byte) 255).put((byte) 255);
                
                data.flip();
                vbo.upload(data);
            } finally {
                MemoryUtil.memFree(data);
            }
            
            // Setup vertex attributes
            vao.setupVertexAttributes(format);
            vbo.bind();
            // Verify VAO is bound
            int boundVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
            assertEquals(vao.getId(), boundVao);
        } finally {
            vao.destroy();
            vbo.destroy();
        }
    }

    @Test
    void vaoBindsIndexBuffer() {
        VAOBuffer vao = new VAOBuffer();
        IBOBuffer ibo = new IBOBuffer();
        try {
            ByteBuffer data = MemoryUtil.memAlloc(3 * Integer.BYTES);
            try {
                data.putInt(0).putInt(1).putInt(2);
                data.flip();
                ibo.upload(data);
            } finally {
                MemoryUtil.memFree(data);
            }
            
            vao.bind();
            ibo.bind();
            
            // Verify both VAO and IBO are bound
            int boundVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
            assertEquals(vao.getId(), boundVao);
            
            int boundIbo = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
            assertEquals(ibo.getId(), boundIbo);
        } finally {
            vao.destroy();
            ibo.destroy();
        }
    }

    @Test
    void vaoThrowsExceptionWhenDestroyedAndUsed() {
        VAOBuffer vao = new VAOBuffer();
        vao.destroy();
        
        assertThrows(IllegalStateException.class, vao::bind);
    }

    @Test
    void buffersWorkWithAutoCloseable() {
        try (VBOBuffer vbo = new VBOBuffer();
             IBOBuffer ibo = new IBOBuffer();
             VAOBuffer vao = new VAOBuffer()) {
            
            assertFalse(vbo.isDestroyed());
            assertFalse(ibo.isDestroyed());
            assertFalse(vao.isDestroyed());
        }
    }
}


