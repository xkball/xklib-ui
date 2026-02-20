import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.window.Window;
import com.xkball.xklib.ui.backend.gl.buffer.VAOBuffer;
import com.xkball.xklib.ui.backend.gl.buffer.VBOBuffer;
import com.xkball.xklib.ui.backend.gl.shader.ShaderProgram;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormat;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormats;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.opengl.GL11.*;

class RectangleRenderTest {

    @Test
    void testRenderRectangle() {
        Window window = new Window(640, 480, "rectangle-test");
        window.init();
        
        try {
            ShaderProgram shader = new ShaderProgram(
                    ResourceLocation.of("shaders/core/pos_color.vsh"),
                    ResourceLocation.of("shaders/core/pos_color.fsh")
            );
            
            BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_COLOR);
            builder.addVertex(-0.5f, -0.5f, 0.0f).setColor(255, 0, 0, 255);
            builder.addVertex(0.5f, -0.5f, 0.0f).setColor(0, 255, 0, 255);
            builder.addVertex(0.5f, 0.5f, 0.0f).setColor(0, 0, 255, 255);
            
            builder.addVertex(-0.5f, -0.5f, 0.0f).setColor(255, 0, 0, 255);
            builder.addVertex(0.5f, 0.5f, 0.0f).setColor(0, 0, 255, 255);
            builder.addVertex(-0.5f, 0.5f, 0.0f).setColor(255, 255, 0, 255);
            
            ByteBuffer buffer = builder.build();
            assertNotNull(buffer);
            assertEquals(6 * VertexFormats.POSITION_COLOR.getVertexSize(), buffer.capacity());
            
            VAOBuffer vao = new VAOBuffer();
            VBOBuffer vbo = new VBOBuffer();
            
            vao.bind();
            vao.setupVertexAttributes(VertexFormats.POSITION_COLOR);
            vbo.bind();
            vbo.upload(buffer);
            
            builder.free();
            
            try {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                
                shader.bind();
                vao.bind();
                glDrawArrays(GL_TRIANGLES, 0, 6);
                shader.unbind();
                VAOBuffer.unbind();
                
                int error = glGetError();
                assertEquals(GL_NO_ERROR, error, "OpenGL error occurred during rendering");
                
            } finally {
                shader.destroy();
                vao.destroy();
                vbo.destroy();
            }
        } finally {
            window.destroy();
        }
    }
}
