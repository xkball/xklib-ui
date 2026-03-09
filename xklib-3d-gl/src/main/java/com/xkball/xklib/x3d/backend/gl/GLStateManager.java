package com.xkball.xklib.x3d.backend.gl;

import com.xkball.xklib.x3d.backend.gl.pipeline.BlendFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.*;

public class GLStateManager {
    
    public static final ThreadLocal<GLStateManager> INSTANCE = ThreadLocal.withInitial(GLStateManager::new);
    
    private int boundFBO = 0;
    private int boundVAO = 0;
    private int boundVBO = 0;
    private int boundIBO = 0;
    private int boundShaderProgram = 0;
    
    private final Int2IntMap boundTextures = new Int2IntLinkedOpenHashMap();
    private int activeTextureUnit = GL_TEXTURE0;
    
    private boolean blendEnabled = false;
    private int blendSrcRGB = GL_ONE;
    private int blendDstRGB = GL_ZERO;
    private int blendSrcAlpha = GL_ONE;
    private int blendDstAlpha = GL_ZERO;
    
    private boolean depthTestEnabled = false;
    private int depthFunc = GL_LESS;
    private boolean depthMask = true;
    
    private boolean cullFaceEnabled = false;
    private int cullFaceMode = GL_BACK;
    
    private boolean scissorEnabled = false;
    private int scissorX = 0;
    private int scissorY = 0;
    private int scissorWidth = 0;
    private int scissorHeight = 0;
    
    public void bindFramebuffer(int target, int fbo) {
        if (target == GL_FRAMEBUFFER && boundFBO == fbo) {
            return;
        }
        boundFBO = fbo;
        glBindFramebuffer(target, fbo);
    }
    
    public void bindVertexArray(int vao) {
        if (boundVAO == vao) {
            return;
        }
        boundVAO = vao;
        glBindVertexArray(vao);
    }
    
    public void bindBuffer(int target, int buffer) {
        if (target == GL_ARRAY_BUFFER) {
            if (boundVBO == buffer) {
                return;
            }
            boundVBO = buffer;
        } else if (target == GL_ELEMENT_ARRAY_BUFFER) {
            if (boundIBO == buffer) {
                return;
            }
            boundIBO = buffer;
        }
        glBindBuffer(target, buffer);
    }
    
    public void useProgram(int program) {
        if (boundShaderProgram == program) {
            return;
        }
        boundShaderProgram = program;
        glUseProgram(program);
    }
    
    public void activeTexture(int textureUnit) {
        if (activeTextureUnit == textureUnit) {
            return;
        }
        activeTextureUnit = textureUnit;
        glActiveTexture(textureUnit);
    }
    
    public void bindTexture(int target, int texture) {
        Integer current = boundTextures.get(activeTextureUnit);
        if (current != null && current == texture) {
            return;
        }
        boundTextures.put(activeTextureUnit, texture);
        glBindTexture(target, texture);
    }
    
    public void enableBlend() {
        if (blendEnabled) {
            return;
        }
        blendEnabled = true;
        glEnable(GL_BLEND);
    }
    
    public void disableBlend() {
        if (!blendEnabled) {
            return;
        }
        blendEnabled = false;
        glDisable(GL_BLEND);
    }
    
    public void blendFunc(int srcFactor, int dstFactor) {
        if (blendSrcRGB == srcFactor && blendDstRGB == dstFactor &&
                blendSrcAlpha == srcFactor && blendDstAlpha == dstFactor) {
            return;
        }
        blendSrcRGB = srcFactor;
        blendDstRGB = dstFactor;
        blendSrcAlpha = srcFactor;
        blendDstAlpha = dstFactor;
        glBlendFunc(srcFactor, dstFactor);
    }
    
    public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        if (blendSrcRGB == srcRGB && blendDstRGB == dstRGB &&
                blendSrcAlpha == srcAlpha && blendDstAlpha == dstAlpha) {
            return;
        }
        blendSrcRGB = srcRGB;
        blendDstRGB = dstRGB;
        blendSrcAlpha = srcAlpha;
        blendDstAlpha = dstAlpha;
        glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }
    
    public void setBlendFunction(BlendFunction blendFunction) {
        blendFuncSeparate(
                BlendFunction.toGl(blendFunction.sourceColor()),
                BlendFunction.toGl(blendFunction.destColor()),
                BlendFunction.toGl(blendFunction.sourceAlpha()),
                BlendFunction.toGl(blendFunction.destAlpha())
        );
    }
    
    public void enableDepthTest() {
        if (depthTestEnabled) {
            return;
        }
        depthTestEnabled = true;
        glEnable(GL_DEPTH_TEST);
    }
    
    public void disableDepthTest() {
        if (!depthTestEnabled) {
            return;
        }
        depthTestEnabled = false;
        glDisable(GL_DEPTH_TEST);
    }
    
    public void depthFunc(int func) {
        if (depthFunc == func) {
            return;
        }
        depthFunc = func;
        glDepthFunc(func);
    }
    
    public void depthMask(boolean flag) {
        if (depthMask == flag) {
            return;
        }
        depthMask = flag;
        glDepthMask(flag);
    }
    
    public void enableCullFace() {
        if (cullFaceEnabled) {
            return;
        }
        cullFaceEnabled = true;
        glEnable(GL_CULL_FACE);
    }
    
    public void disableCullFace() {
        if (!cullFaceEnabled) {
            return;
        }
        cullFaceEnabled = false;
        glDisable(GL_CULL_FACE);
    }
    
    public void cullFace(int mode) {
        if (cullFaceMode == mode) {
            return;
        }
        cullFaceMode = mode;
        glCullFace(mode);
    }
    
    public void enableScissor() {
        if (scissorEnabled) {
            return;
        }
        scissorEnabled = true;
        glEnable(GL_SCISSOR_TEST);
    }
    
    public void disableScissor() {
        if (!scissorEnabled) {
            return;
        }
        scissorEnabled = false;
        glDisable(GL_SCISSOR_TEST);
    }
    
    public void scissor(int x, int y, int width, int height) {
        if (scissorX == x && scissorY == y && scissorWidth == width && scissorHeight == height) {
            return;
        }
        scissorX = x;
        scissorY = y;
        scissorWidth = width;
        scissorHeight = height;
        glScissor(x, y, width, height);
    }
    
    public void reset() {
        boundFBO = 0;
        boundVAO = 0;
        boundVBO = 0;
        boundIBO = 0;
        boundShaderProgram = 0;
        boundTextures.clear();
        activeTextureUnit = GL_TEXTURE0;
        blendEnabled = false;
        blendSrcRGB = GL_ONE;
        blendDstRGB = GL_ZERO;
        blendSrcAlpha = GL_ONE;
        blendDstAlpha = GL_ZERO;
        depthTestEnabled = false;
        depthFunc = GL_LESS;
        depthMask = true;
        cullFaceEnabled = false;
        cullFaceMode = GL_BACK;
        scissorEnabled = false;
        scissorX = 0;
        scissorY = 0;
        scissorWidth = 0;
        scissorHeight = 0;
    }
    
    public int getBoundFramebuffer() {
        return boundFBO;
    }
    
    public int getBoundVertexArray() {
        return boundVAO;
    }
    
    public int getBoundArrayBuffer() {
        return boundVBO;
    }
    
    public int getBoundElementArrayBuffer() {
        return boundIBO;
    }
    
    public int getBoundProgram() {
        return boundShaderProgram;
    }
    
    public boolean isBlendEnabled() {
        return blendEnabled;
    }
    
    public boolean isDepthTestEnabled() {
        return depthTestEnabled;
    }
    
    public boolean isCullFaceEnabled() {
        return cullFaceEnabled;
    }
    
    public boolean isScissorEnabled() {
        return scissorEnabled;
    }
}
