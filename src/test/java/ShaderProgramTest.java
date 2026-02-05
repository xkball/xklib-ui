import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.Window;
import com.xkball.xklib.ui.backend.gl.shader.ShaderProgram;
import com.xkball.xklib.ui.backend.gl.shader.Uniform;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShaderProgramTest {

    @Test
    void loadsCompilesAndLinksPositionColorShaders() {
        Window window = new Window(64, 64, "shader-test");
        window.init();
        try {
            ShaderProgram program = new ShaderProgram(
                ResourceLocation.of("shaders/test/pos_color.vert"),
                ResourceLocation.of("shaders/test/pos_color.frag")
            );
            try {
                assertTrue(program.getProgramId() > 0);

                program.bind();
                program.unbind();
            } finally {
                program.destroy();
            }
        } finally {
            window.destroy();
        }
    }

    @Test
    void throwsHelpfulErrorOnCompileFailure() {
        Window window = new Window(64, 64, "shader-test-invalid");
        window.init();
        try {
            IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> new ShaderProgram(
                    ResourceLocation.of("shaders/test/invalid.vert"),
                    ResourceLocation.of("shaders/test/pos_color.frag")
                )
            );
            assertTrue(ex.getMessage().contains("Failed to compile shader"));
        } finally {
            window.destroy();
        }
    }
    
    @Test
    void canCreateAndUseUniforms() {
        Window window = new Window(64, 64, "shader-test-uniform");
        window.init();
        try {
            ShaderProgram program = new ShaderProgram(
                ResourceLocation.of("shaders/test/uniform_test.vert"),
                ResourceLocation.of("shaders/test/uniform_test.frag")
            );
            try {
                Uniform mvpUniform = program.getUniform("uModelViewProjection");
                assertNotNull(mvpUniform);
                assertTrue(mvpUniform.getLocation() >= 0);
                assertEquals(Uniform.UT_MAT4, mvpUniform.getType());
                
                Uniform offsetUniform = program.getUniform("uOffset");
                assertNotNull(offsetUniform);
                assertTrue(offsetUniform.getLocation() >= 0);
                assertEquals(Uniform.UT_FLOAT3, offsetUniform.getType());
                
                Uniform colorUniform = program.getUniform("uColor");
                assertNotNull(colorUniform);
                assertTrue(colorUniform.getLocation() >= 0);
                assertEquals(Uniform.UT_FLOAT4, colorUniform.getType());
                
                Uniform alphaUniform = program.getUniform("uAlpha");
                assertNotNull(alphaUniform);
                assertTrue(alphaUniform.getLocation() >= 0);
                assertEquals(Uniform.UT_FLOAT1, alphaUniform.getType());
                
                Matrix4f mvp = new Matrix4f().identity();
                mvpUniform.set(mvp);
                
                Vector3f offset = new Vector3f(1.0f, 2.0f, 3.0f);
                offsetUniform.set(offset);
                
                Vector4f color = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
                colorUniform.set(color);
                
                alphaUniform.set(0.5f);
                
                program.bind();
                program.uploadUniforms();
                program.unbind();
                
            } finally {
                program.destroy();
            }
        } finally {
            window.destroy();
        }
    }
    
    @Test
    void uniformsGetCleanedUpOnDestroy() {
        Window window = new Window(64, 64, "shader-test-cleanup");
        window.init();
        try {
            ShaderProgram program = new ShaderProgram(
                ResourceLocation.of("shaders/test/uniform_test.vert"),
                ResourceLocation.of("shaders/test/uniform_test.frag")
            );
            
            program.getOrCreateUniform("uColor", Uniform.UT_FLOAT4);
            program.getOrCreateUniform("uAlpha", Uniform.UT_FLOAT1);
            
            program.destroy();
            
            assertNull(program.getUniform("uColor"));
            assertNull(program.getUniform("uAlpha"));
        } finally {
            window.destroy();
        }
    }
}
