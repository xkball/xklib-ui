import com.xkball.xklib.ui.backend.window.Window;
import com.xkball.xklib.ui.backend.gl.buffer.VBOBuffer;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormat;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormats;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class BufferBuilderTest {
    private static Window window;

    @BeforeAll
    static void setup() {
        window = new Window(64, 64, "buffer-builder-test");
        window.init();
    }

    @AfterAll
    static void cleanup() {
        if (window != null) {
            window.destroy();
        }
    }

    @Test
    void testBuilderBasicCreation() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        assertNotNull(builder);
        builder.free();
    }

    @Test
    void testAddVertexAndColor() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        try {
            builder.addVertex(0.0f, 1.0f, 0.0f).setColor(255, 0, 0, 255);
            builder.addVertex(1.0f, 0.0f, 0.0f).setColor(0, 255, 0, 255);
            builder.addVertex(0.0f, 0.0f, 1.0f).setColor(0, 0, 255, 255);
            
            ByteBuffer buffer = builder.build();
            assertNotNull(buffer);
            assertEquals(3 * VertexFormats.POSITION_COLOR.getVertexSize(), buffer.capacity());
        } finally {
            builder.free();
        }
    }

    @Test
    void testColorFromInt() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        try {
            int color = 0xFF00FF00;
            builder.addVertex(0.0f, 0.0f, 0.0f).setColor(color);
            
            ByteBuffer buffer = builder.build();
            assertNotNull(buffer);
        } finally {
            builder.free();
        }
    }

    @Test
    void testBuildAndUpload() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(255, 0, 0, 255);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(0, 255, 0, 255);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(0, 0, 255, 255);
        
        VBOBuffer vbo = builder.buildAndUpload();
        assertNotNull(vbo);
        assertEquals(3 * VertexFormats.POSITION_COLOR.getVertexSize(), vbo.getSize());
        vbo.destroy();
    }

    @Test
    void testMultipleVertices() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        try {
            for (int i = 0; i < 10; i++) {
                builder.addVertex(i, i * 2, i * 3).setColor(i * 10, i * 20, i * 30, 255);
            }
            
            ByteBuffer buffer = builder.build();
            assertEquals(10 * VertexFormats.POSITION_COLOR.getVertexSize(), buffer.capacity());
        } finally {
            builder.free();
        }
    }

    @Test
    void testSkipNonExistentElement() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        try {
            builder.addVertex(0.0f, 0.0f, 0.0f);
            builder.setUv(0.5f, 0.5f);
            builder.setColor(255, 255, 255, 255);
            
            ByteBuffer buffer = builder.build();
            assertNotNull(buffer);
        } finally {
            builder.free();
        }
    }

    @Test
    void testThrowsOnSetBeforeAddVertex() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        try {
            assertThrows(IllegalStateException.class, () -> builder.setColor(255, 0, 0, 255));
        } finally {
            builder.free();
        }
    }

    @Test
    void testThrowsOnDoubleuild() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        try {
            builder.addVertex(0.0f, 0.0f, 0.0f).setColor(255, 255, 255, 255);
            builder.build();
            assertThrows(IllegalStateException.class, builder::build);
        } finally {
            builder.free();
        }
    }

    @Test
    void testVertexWithAllElements() {
        VertexFormat format = VertexFormat.builder()
                .add("Position", VertexFormatElement.POSITION)
                .add("Color", VertexFormatElement.COLOR)
                .add("UV", VertexFormatElement.UV)
                .add("Normal", VertexFormatElement.NORMAL)
                .build();
        
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, format);
        try {
            builder.addVertex(1.0f, 2.0f, 3.0f)
                    .setColor(255, 128, 64, 255)
                    .setUv(0.5f, 0.5f)
                    .setNormal(0.0f, 1.0f, 0.0f);
            
            ByteBuffer buffer = builder.build();
            assertNotNull(buffer);
            assertEquals(format.getVertexSize(), buffer.capacity());
        } finally {
            builder.free();
        }
    }

    @Test
    void testMemoryReallocation() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        try {
            for (int i = 0; i < 300; i++) {
                builder.addVertex(i, i, i).setColor(255, 255, 255, 255);
            }
            
            ByteBuffer buffer = builder.build();
            assertEquals(300 * VertexFormats.POSITION_COLOR.getVertexSize(), buffer.capacity());
        } finally {
            builder.free();
        }
    }

    @Test
    void testUnsafeSet() {
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
        try {
            builder.addVertex(0.0f, 0.0f, 0.0f);
            builder.setUnsafe(VertexFormatElement.COLOR, ptr -> {
                MemoryUtil.memPutByte(ptr, (byte) 255);
                MemoryUtil.memPutByte(ptr + 1, (byte) 128);
                MemoryUtil.memPutByte(ptr + 2, (byte) 64);
                MemoryUtil.memPutByte(ptr + 3, (byte) 255);
            });
            
            ByteBuffer buffer = builder.build();
            assertNotNull(buffer);
        } finally {
            builder.free();
        }
    }
}
