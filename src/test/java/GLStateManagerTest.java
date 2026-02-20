import com.xkball.xklib.ui.backend.window.Window;
import com.xkball.xklib.ui.backend.gl.GLStateManager;
import com.xkball.xklib.ui.backend.gl.buffer.VAOBuffer;
import com.xkball.xklib.ui.backend.gl.buffer.VBOBuffer;
import com.xkball.xklib.ui.backend.gl.buffer.IBOBuffer;
import com.xkball.xklib.ui.backend.gl.pipeline.BlendFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

class GLStateManagerTest {
    
    private Window window;
    
    @BeforeEach
    void setUp() {
        window = new Window(64, 64, "glstate-test");
        window.init();
        GLStateManager.reset();
    }
    
    @AfterEach
    void tearDown() {
        if (window != null) {
            window.destroy();
        }
    }
    
    @Test
    void testBindFramebuffer() {
        int fbo = glGenFramebuffers();
        try {
            GLStateManager.bindFramebuffer(GL_FRAMEBUFFER, fbo);
            assertEquals(fbo, glGetInteger(GL_FRAMEBUFFER_BINDING));
            assertEquals(fbo, GLStateManager.getBoundFramebuffer());
            
            GLStateManager.bindFramebuffer(GL_FRAMEBUFFER, fbo);
            assertEquals(fbo, glGetInteger(GL_FRAMEBUFFER_BINDING));
            
            GLStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
            assertEquals(0, glGetInteger(GL_FRAMEBUFFER_BINDING));
        } finally {
            glDeleteFramebuffers(fbo);
        }
    }
    
    @Test
    void testBindVertexArray() {
        VAOBuffer vao = new VAOBuffer();
        try {
            GLStateManager.bindVertexArray(vao.getId());
            assertEquals(vao.getId(), glGetInteger(GL_VERTEX_ARRAY_BINDING));
            assertEquals(vao.getId(), GLStateManager.getBoundVertexArray());
            
            GLStateManager.bindVertexArray(vao.getId());
            assertEquals(vao.getId(), glGetInteger(GL_VERTEX_ARRAY_BINDING));
            
            GLStateManager.bindVertexArray(0);
            assertEquals(0, glGetInteger(GL_VERTEX_ARRAY_BINDING));
        } finally {
            vao.destroy();
        }
    }
    
    @Test
    void testBindBuffer() {
        VBOBuffer vbo = new VBOBuffer();
        IBOBuffer ibo = new IBOBuffer();
        
        try {
            GLStateManager.bindBuffer(GL_ARRAY_BUFFER, vbo.getId());
            assertEquals(vbo.getId(), glGetInteger(GL_ARRAY_BUFFER_BINDING));
            assertEquals(vbo.getId(), GLStateManager.getBoundArrayBuffer());
            
            GLStateManager.bindBuffer(GL_ARRAY_BUFFER, vbo.getId());
            assertEquals(vbo.getId(), glGetInteger(GL_ARRAY_BUFFER_BINDING));
            
            GLStateManager.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo.getId());
            assertEquals(ibo.getId(), glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING));
            assertEquals(ibo.getId(), GLStateManager.getBoundElementArrayBuffer());
            
            GLStateManager.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo.getId());
            assertEquals(ibo.getId(), glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING));
            
            GLStateManager.bindBuffer(GL_ARRAY_BUFFER, 0);
            assertEquals(0, glGetInteger(GL_ARRAY_BUFFER_BINDING));
            
            GLStateManager.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            assertEquals(0, glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING));
        } finally {
            vbo.destroy();
            ibo.destroy();
        }
    }
    
    @Test
    void testActiveTexture() {
        GLStateManager.activeTexture(GL_TEXTURE0);
        assertEquals(GL_TEXTURE0, glGetInteger(GL_ACTIVE_TEXTURE));
        
        GLStateManager.activeTexture(GL_TEXTURE1);
        assertEquals(GL_TEXTURE1, glGetInteger(GL_ACTIVE_TEXTURE));
        
        GLStateManager.activeTexture(GL_TEXTURE1);
        assertEquals(GL_TEXTURE1, glGetInteger(GL_ACTIVE_TEXTURE));
        
        GLStateManager.activeTexture(GL_TEXTURE0);
        assertEquals(GL_TEXTURE0, glGetInteger(GL_ACTIVE_TEXTURE));
    }
    
    @Test
    void testBindTexture() {
        int tex1 = glGenTextures();
        int tex2 = glGenTextures();
        
        try {
            GLStateManager.activeTexture(GL_TEXTURE0);
            GLStateManager.bindTexture(GL_TEXTURE_2D, tex1);
            
            int[] bound = new int[1];
            glGetIntegerv(GL_TEXTURE_BINDING_2D, bound);
            assertEquals(tex1, bound[0]);
            
            GLStateManager.bindTexture(GL_TEXTURE_2D, tex1);
            glGetIntegerv(GL_TEXTURE_BINDING_2D, bound);
            assertEquals(tex1, bound[0]);
            
            GLStateManager.activeTexture(GL_TEXTURE1);
            GLStateManager.bindTexture(GL_TEXTURE_2D, tex2);
            glGetIntegerv(GL_TEXTURE_BINDING_2D, bound);
            assertEquals(tex2, bound[0]);
            
            GLStateManager.activeTexture(GL_TEXTURE0);
            glGetIntegerv(GL_TEXTURE_BINDING_2D, bound);
            assertEquals(tex1, bound[0]);
        } finally {
            glDeleteTextures(tex1);
            glDeleteTextures(tex2);
        }
    }
    
    @Test
    void testBlendState() {
        assertFalse(GLStateManager.isBlendEnabled());
        
        GLStateManager.enableBlend();
        assertTrue(GLStateManager.isBlendEnabled());
        assertTrue(glGetBoolean(GL_BLEND));
        
        GLStateManager.enableBlend();
        assertTrue(GLStateManager.isBlendEnabled());
        
        GLStateManager.disableBlend();
        assertFalse(GLStateManager.isBlendEnabled());
        assertFalse(glGetBoolean(GL_BLEND));
        
        GLStateManager.disableBlend();
        assertFalse(GLStateManager.isBlendEnabled());
    }
    
    @Test
    void testBlendFunc() {
        GLStateManager.enableBlend();
        
        GLStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        assertEquals(GL_SRC_ALPHA, glGetInteger(GL_BLEND_SRC_RGB));
        assertEquals(GL_ONE_MINUS_SRC_ALPHA, glGetInteger(GL_BLEND_DST_RGB));
        assertEquals(GL_SRC_ALPHA, glGetInteger(GL_BLEND_SRC_ALPHA));
        assertEquals(GL_ONE_MINUS_SRC_ALPHA, glGetInteger(GL_BLEND_DST_ALPHA));
        
        GLStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        assertEquals(GL_SRC_ALPHA, glGetInteger(GL_BLEND_SRC_RGB));
    }
    
    @Test
    void testBlendFuncSeparate() {
        GLStateManager.enableBlend();
        
        GLStateManager.blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        assertEquals(GL_SRC_ALPHA, glGetInteger(GL_BLEND_SRC_RGB));
        assertEquals(GL_ONE_MINUS_SRC_ALPHA, glGetInteger(GL_BLEND_DST_RGB));
        assertEquals(GL_ONE, glGetInteger(GL_BLEND_SRC_ALPHA));
        assertEquals(GL_ZERO, glGetInteger(GL_BLEND_DST_ALPHA));
    }
    
    @Test
    void testSetBlendFunction() {
        GLStateManager.enableBlend();
        
        GLStateManager.setBlendFunction(BlendFunction.TRANSLUCENT);
        assertEquals(GL_SRC_ALPHA, glGetInteger(GL_BLEND_SRC_RGB));
        assertEquals(GL_ONE_MINUS_SRC_ALPHA, glGetInteger(GL_BLEND_DST_RGB));
        assertEquals(GL_ONE, glGetInteger(GL_BLEND_SRC_ALPHA));
        assertEquals(GL_ONE_MINUS_SRC_ALPHA, glGetInteger(GL_BLEND_DST_ALPHA));
        
        GLStateManager.setBlendFunction(BlendFunction.ADDITIVE);
        assertEquals(GL_ONE, glGetInteger(GL_BLEND_SRC_RGB));
        assertEquals(GL_ONE, glGetInteger(GL_BLEND_DST_RGB));
    }
    
    @Test
    void testDepthTest() {
        assertFalse(GLStateManager.isDepthTestEnabled());
        
        GLStateManager.enableDepthTest();
        assertTrue(GLStateManager.isDepthTestEnabled());
        assertTrue(glGetBoolean(GL_DEPTH_TEST));
        
        GLStateManager.enableDepthTest();
        assertTrue(GLStateManager.isDepthTestEnabled());
        
        GLStateManager.disableDepthTest();
        assertFalse(GLStateManager.isDepthTestEnabled());
        assertFalse(glGetBoolean(GL_DEPTH_TEST));
    }
    
    @Test
    void testDepthFunc() {
        GLStateManager.enableDepthTest();
        
        GLStateManager.depthFunc(GL_LEQUAL);
        assertEquals(GL_LEQUAL, glGetInteger(GL_DEPTH_FUNC));
        
        GLStateManager.depthFunc(GL_LEQUAL);
        assertEquals(GL_LEQUAL, glGetInteger(GL_DEPTH_FUNC));
        
        GLStateManager.depthFunc(GL_ALWAYS);
        assertEquals(GL_ALWAYS, glGetInteger(GL_DEPTH_FUNC));
    }
    
    @Test
    void testDepthMask() {
        GLStateManager.depthMask(false);
        assertFalse(glGetBoolean(GL_DEPTH_WRITEMASK));
        
        GLStateManager.depthMask(false);
        assertFalse(glGetBoolean(GL_DEPTH_WRITEMASK));
        
        GLStateManager.depthMask(true);
        assertTrue(glGetBoolean(GL_DEPTH_WRITEMASK));
    }
    
    @Test
    void testCullFace() {
        assertFalse(GLStateManager.isCullFaceEnabled());
        
        GLStateManager.enableCullFace();
        assertTrue(GLStateManager.isCullFaceEnabled());
        assertTrue(glGetBoolean(GL_CULL_FACE));
        
        GLStateManager.disableCullFace();
        assertFalse(GLStateManager.isCullFaceEnabled());
        assertFalse(glGetBoolean(GL_CULL_FACE));
    }
    
    @Test
    void testCullFaceMode() {
        GLStateManager.enableCullFace();
        
        GLStateManager.cullFace(GL_FRONT);
        assertEquals(GL_FRONT, glGetInteger(GL_CULL_FACE_MODE));
        
        GLStateManager.cullFace(GL_FRONT);
        assertEquals(GL_FRONT, glGetInteger(GL_CULL_FACE_MODE));
        
        GLStateManager.cullFace(GL_BACK);
        assertEquals(GL_BACK, glGetInteger(GL_CULL_FACE_MODE));
    }
    
    @Test
    void testScissor() {
        assertFalse(GLStateManager.isScissorEnabled());
        
        GLStateManager.enableScissor();
        assertTrue(GLStateManager.isScissorEnabled());
        assertTrue(glGetBoolean(GL_SCISSOR_TEST));
        
        GLStateManager.disableScissor();
        assertFalse(GLStateManager.isScissorEnabled());
        assertFalse(glGetBoolean(GL_SCISSOR_TEST));
    }
    
    @Test
    void testScissorBox() {
        GLStateManager.enableScissor();
        
        GLStateManager.scissor(10, 20, 100, 200);
        int[] box = new int[4];
        glGetIntegerv(GL_SCISSOR_BOX, box);
        assertEquals(10, box[0]);
        assertEquals(20, box[1]);
        assertEquals(100, box[2]);
        assertEquals(200, box[3]);
        
        GLStateManager.scissor(10, 20, 100, 200);
        glGetIntegerv(GL_SCISSOR_BOX, box);
        assertEquals(10, box[0]);
        
        GLStateManager.scissor(5, 10, 50, 100);
        glGetIntegerv(GL_SCISSOR_BOX, box);
        assertEquals(5, box[0]);
        assertEquals(10, box[1]);
        assertEquals(50, box[2]);
        assertEquals(100, box[3]);
    }
    
    @Test
    void testReset() {
        GLStateManager.enableBlend();
        GLStateManager.enableDepthTest();
        GLStateManager.enableCullFace();
        GLStateManager.enableScissor();
        
        GLStateManager.reset();
        
        assertFalse(GLStateManager.isBlendEnabled());
        assertFalse(GLStateManager.isDepthTestEnabled());
        assertFalse(GLStateManager.isCullFaceEnabled());
        assertFalse(GLStateManager.isScissorEnabled());
        assertEquals(0, GLStateManager.getBoundFramebuffer());
        assertEquals(0, GLStateManager.getBoundVertexArray());
        assertEquals(0, GLStateManager.getBoundArrayBuffer());
        assertEquals(0, GLStateManager.getBoundElementArrayBuffer());
        assertEquals(0, GLStateManager.getBoundProgram());
    }
}
