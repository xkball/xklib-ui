package com.xkball.xklib.x3d.backend.gl.buffer;

import com.xkball.xklib.x3d.backend.gl.GLStateManager;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30C.*;

public class FrameBuffer {

    private int fbo;
    private int colorTex;
    private int depthRbo;
    private int width;
    private int height;

    public FrameBuffer(int width, int height) {
        create(width, height);
    }

    public void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }
        destroy();
        create(width, height);
    }

    private void create(int width, int height) {
        this.width = width;
        this.height = height;

        fbo = GL45.glCreateFramebuffers();
        colorTex = createTexture(width, height);
        depthRbo = createDepthStencil(width, height);

        GL45.glNamedFramebufferTexture(fbo, GL_COLOR_ATTACHMENT0, colorTex, 0);
        GL45.glNamedFramebufferRenderbuffer(fbo, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthRbo);

        int status = GL45.glCheckNamedFramebufferStatus(fbo, GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            destroy();
            throw new IllegalStateException("Framebuffer incomplete: 0x" + Integer.toHexString(status));
        }
    }

    public void clearWhite() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer white = stack.floats(1.0f, 1.0f, 1.0f, 1.0f);
            GL45.glClearNamedFramebufferfv(fbo, GL_COLOR, 0, white);
            GL45.glClearNamedFramebufferfi(fbo, GL_DEPTH_STENCIL, 0, 1.0f, 0);
        }
    }
    
    public void clearDepthStencil() {
        GL45.glClearNamedFramebufferfi(fbo, GL_DEPTH_STENCIL, 0, 1.0f, 0);
    }

    public void blitToDefault() {
        GL45.glBlitNamedFramebuffer(fbo, 0,
                0, 0, width, height,
                0, 0, width, height,
                GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }

    private int createTexture(int width, int height) {
        int tex = GL45.glCreateTextures(GL_TEXTURE_2D);
        GL45.glTextureStorage2D(tex, 1, GL_RGBA8, width, height);
        GL45.glTextureParameteri(tex, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        GL45.glTextureParameteri(tex, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        GL45.glTextureParameteri(tex, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GL45.glTextureParameteri(tex, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        return tex;
    }

    private int createDepthStencil(int width, int height) {
        int rbo = GL45.glCreateRenderbuffers();
        GL45.glNamedRenderbufferStorage(rbo, GL_DEPTH24_STENCIL8, width, height);
        
        return rbo;
    }
    
    public void bind(){
        GLStateManager.bindFramebuffer(GL45.GL_FRAMEBUFFER, fbo);
    }

    public void destroy() {
        if (fbo != 0) {
            GL30C.glDeleteFramebuffers(fbo);
            fbo = 0;
        }
        if (colorTex != 0) {
            GL30C.glDeleteTextures(colorTex);
            colorTex = 0;
        }
        if (depthRbo != 0) {
            GL30C.glDeleteRenderbuffers(depthRbo);
            depthRbo = 0;
        }
    }
}
