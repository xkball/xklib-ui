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


class VertexFormatTest {
    private static Window window;

    @BeforeAll
    static void setup() {
        window = new Window(64, 64, "vertex-format-test");
        window.init();
    }

    @AfterAll
    static void cleanup() {
        if (window != null) {
            window.destroy();
        }
    }

    @Test
    void vertexFormatHasCorrectSize() {
        VertexFormat format = VertexFormats.POSITION_COLOR;
        
        // Position: 3 floats = 12 bytes
        // Color: 4 unsigned bytes = 4 bytes
        // Total: 16 bytes
        assertEquals(16, format.getVertexSize());
    }

    @Test
    void vertexFormatCreatesFormatVAO() {
        VertexFormat format = VertexFormats.POSITION_COLOR;
        
        VAOBuffer vao = format.getFormatVertexArrayBuffer();
        assertNotNull(vao);
        assertFalse(vao.isDestroyed());
        
        // Should return the same instance on subsequent calls
        VAOBuffer vao2 = format.getFormatVertexArrayBuffer();
        assertSame(vao, vao2);
    }

    @Test
    void vertexFormatUploadsImmediateVertexBuffer() {
        VertexFormat format = VertexFormats.POSITION_COLOR;
        
        // Create test vertex data
        ByteBuffer vertexData = MemoryUtil.memAlloc(3 * format.getVertexSize());
        try {
            // Vertex 1: position (0, 0.5, 0), color (255, 0, 0, 255)
            vertexData.putFloat(0.0f).putFloat(0.5f).putFloat(0.0f);
            vertexData.put((byte) 255).put((byte) 0).put((byte) 0).put((byte) 255);
            
            // Vertex 2: position (-0.5, -0.5, 0), color (0, 255, 0, 255)
            vertexData.putFloat(-0.5f).putFloat(-0.5f).putFloat(0.0f);
            vertexData.put((byte) 0).put((byte) 255).put((byte) 0).put((byte) 255);
            
            // Vertex 3: position (0.5, -0.5, 0), color (0, 0, 255, 255)
            vertexData.putFloat(0.5f).putFloat(-0.5f).putFloat(0.0f);
            vertexData.put((byte) 0).put((byte) 0).put((byte) 255).put((byte) 255);
            
            vertexData.flip();
            
            format.uploadImmediateVertexBuffer(vertexData);
            
            VBOBuffer vbo = format.getImmediateDrawVertexBuffer();
            assertNotNull(vbo);
            assertFalse(vbo.isDestroyed());
            assertEquals(3 * format.getVertexSize(), vbo.getSize());
        } finally {
            MemoryUtil.memFree(vertexData);
        }
    }

    @Test
    void vertexFormatUploadsImmediateIndexBuffer() {
        VertexFormat format = VertexFormats.POSITION_COLOR;
        
        // Create test index data
        ByteBuffer indexData = MemoryUtil.memAlloc(3 * Integer.BYTES);
        try {
            indexData.putInt(0).putInt(1).putInt(2);
            indexData.flip();
            
            format.uploadImmediateIndexBuffer(indexData);
            
            IBOBuffer ibo = format.getImmediateDrawIndexBuffer();
            assertNotNull(ibo);
            assertFalse(ibo.isDestroyed());
            assertEquals(3 * Integer.BYTES, ibo.getSize());
        } finally {
            MemoryUtil.memFree(indexData);
        }
    }

    @Test
    void vertexFormatReplacesImmediateBufferData() {
        VertexFormat format = VertexFormats.POSITION_COLOR;
        
        ByteBuffer vertexData1 = MemoryUtil.memAlloc(format.getVertexSize());
        try {
            vertexData1.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
            vertexData1.put((byte) 255).put((byte) 255).put((byte) 255).put((byte) 255);
            vertexData1.flip();
            format.uploadImmediateVertexBuffer(vertexData1);
        } finally {
            MemoryUtil.memFree(vertexData1);
        }
        
        VBOBuffer vbo1 = format.getImmediateDrawVertexBuffer();
        assertNotNull(vbo1);
        assertEquals(format.getVertexSize(), vbo1.getSize());
        
        ByteBuffer vertexData2 = MemoryUtil.memAlloc(2 * format.getVertexSize());
        try {
            vertexData2.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
            vertexData2.put((byte) 255).put((byte) 0).put((byte) 0).put((byte) 255);
            vertexData2.putFloat(1.0f).putFloat(0.0f).putFloat(0.0f);
            vertexData2.put((byte) 0).put((byte) 255).put((byte) 0).put((byte) 255);
            vertexData2.flip();
            format.uploadImmediateVertexBuffer(vertexData2);
        } finally {
            MemoryUtil.memFree(vertexData2);
        }
        
        VBOBuffer vbo2 = format.getImmediateDrawVertexBuffer();
        
        assertSame(vbo1, vbo2);
        assertEquals(2 * format.getVertexSize(), vbo2.getSize());
        
        ByteBuffer vertexData3 = MemoryUtil.memAlloc(format.getVertexSize());
        try {
            vertexData3.putFloat(0.5f).putFloat(0.5f).putFloat(0.0f);
            vertexData3.put((byte) 0).put((byte) 0).put((byte) 255).put((byte) 255);
            vertexData3.flip();
            format.uploadImmediateVertexBuffer(vertexData3);
        } finally {
            MemoryUtil.memFree(vertexData3);
        }
        
        assertEquals(format.getVertexSize(), vbo2.getSize());
    }

    @Test
    void vertexFormatBuilderWorks() {
        VertexFormat format = VertexFormat.builder()
                .add("Position", com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.POSITION)
                .add("Color", com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.COLOR)
                .build();
        
        assertNotNull(format);
        assertEquals(2, format.getElements().size());
        assertEquals(16, format.getVertexSize());
    }

    @Test
    void vertexFormatBuilderWithPadding() {
        VertexFormat format = VertexFormat.builder()
                .add("Position", com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.POSITION)
                .padding(4)  // Add 4 bytes padding
                .add("Color", com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.COLOR)
                .build();
        
        assertNotNull(format);
        assertEquals(2, format.getElements().size());
        assertEquals(20, format.getVertexSize()); // 12 + 4 padding + 4 = 20
    }

    @Test
    void vertexFormatContainsElement() {
        VertexFormat format = VertexFormats.POSITION_COLOR;
        
        assertTrue(format.contains(com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.POSITION));
        assertTrue(format.contains(com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.COLOR));
        assertFalse(format.contains(com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.UV));
    }

    @Test
    void vertexFormatGetElementOffset() {
        VertexFormat format = VertexFormats.POSITION_COLOR;
        
        // Position should be at offset 0
        assertEquals(0, format.getOffset(com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.POSITION));
        
        // Color should be after position (3 floats = 12 bytes)
        assertEquals(12, format.getOffset(com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement.COLOR));
    }

    @Test
    void vertexFormatHasCorrectProperties() {
        VertexFormat format = VertexFormats.POSITION_COLOR;
        
        assertTrue(format.hasPosition());
        assertTrue(format.hasColor());
        assertFalse(format.hasNormal());
        assertFalse(format.hasUV(0));
    }
}
