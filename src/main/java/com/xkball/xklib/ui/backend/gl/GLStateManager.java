package com.xkball.xklib.ui.backend.gl;

import com.xkball.xklib.ui.backend.gl.pipeline.BlendFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GLStateManager {
    
    private static int boundFBO = 0;
    private static int boundVAO = 0;
    private static int boundVBO = 0;
    private static int boundIBO = 0;
    private static int boundShaderProgram = 0;
    
    private static final Int2IntMap boundTextures = new Int2IntLinkedOpenHashMap();
    private static int activeTextureUnit = GL_TEXTURE0;
    
    private static boolean blendEnabled = false;
    private static int blendSrcRGB = GL_ONE;
    private static int blendDstRGB = GL_ZERO;
    private static int blendSrcAlpha = GL_ONE;
    private static int blendDstAlpha = GL_ZERO;
    
    private static boolean depthTestEnabled = false;
    private static int depthFunc = GL_LESS;
    private static boolean depthMask = true;
    
    private static boolean cullFaceEnabled = false;
    private static int cullFaceMode = GL_BACK;
    
    private static boolean scissorEnabled = false;
    private static int scissorX = 0;
    private static int scissorY = 0;
    private static int scissorWidth = 0;
    private static int scissorHeight = 0;
    
    public static void bindFramebuffer(int target, int fbo) {
        if (target == GL_FRAMEBUFFER && boundFBO == fbo) {
            return;
        }
        boundFBO = fbo;
        glBindFramebuffer(target, fbo);
    }
    
    public static void bindVertexArray(int vao) {
        if (boundVAO == vao) {
            return;
        }
        boundVAO = vao;
        glBindVertexArray(vao);
    }
    
    public static void bindBuffer(int target, int buffer) {
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
    
    public static void useProgram(int program) {
        if (boundShaderProgram == program) {
            return;
        }
        boundShaderProgram = program;
        glUseProgram(program);
    }
    
    public static void activeTexture(int textureUnit) {
        if (activeTextureUnit == textureUnit) {
            return;
        }
        activeTextureUnit = textureUnit;
        glActiveTexture(textureUnit);
    }
    
    public static void bindTexture(int target, int texture) {
        Integer current = boundTextures.get(activeTextureUnit);
        if (current != null && current == texture) {
            return;
        }
        boundTextures.put(activeTextureUnit, texture);
        glBindTexture(target, texture);
    }
    
    public static void enableBlend() {
        if (blendEnabled) {
            return;
        }
        blendEnabled = true;
        glEnable(GL_BLEND);
    }
    
    public static void disableBlend() {
        if (!blendEnabled) {
            return;
        }
        blendEnabled = false;
        glDisable(GL_BLEND);
    }
    
    public static void blendFunc(int srcFactor, int dstFactor) {
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
    
    public static void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
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
    
    public static void setBlendFunction(BlendFunction blendFunction) {
        blendFuncSeparate(
            BlendFunction.toGl(blendFunction.sourceColor()),
            BlendFunction.toGl(blendFunction.destColor()),
            BlendFunction.toGl(blendFunction.sourceAlpha()),
            BlendFunction.toGl(blendFunction.destAlpha())
        );
    }
    
    public static void enableDepthTest() {
        if (depthTestEnabled) {
            return;
        }
        depthTestEnabled = true;
        glEnable(GL_DEPTH_TEST);
    }
    
    public static void disableDepthTest() {
        if (!depthTestEnabled) {
            return;
        }
        depthTestEnabled = false;
        glDisable(GL_DEPTH_TEST);
    }
    
    public static void depthFunc(int func) {
        if (depthFunc == func) {
            return;
        }
        depthFunc = func;
        glDepthFunc(func);
    }
    
    public static void depthMask(boolean flag) {
        if (depthMask == flag) {
            return;
        }
        depthMask = flag;
        glDepthMask(flag);
    }
    
    public static void enableCullFace() {
        if (cullFaceEnabled) {
            return;
        }
        cullFaceEnabled = true;
        glEnable(GL_CULL_FACE);
    }
    
    public static void disableCullFace() {
        if (!cullFaceEnabled) {
            return;
        }
        cullFaceEnabled = false;
        glDisable(GL_CULL_FACE);
    }
    
    public static void cullFace(int mode) {
        if (cullFaceMode == mode) {
            return;
        }
        cullFaceMode = mode;
        glCullFace(mode);
    }
    
    public static void enableScissor() {
        if (scissorEnabled) {
            return;
        }
        scissorEnabled = true;
        glEnable(GL_SCISSOR_TEST);
    }
    
    public static void disableScissor() {
        if (!scissorEnabled) {
            return;
        }
        scissorEnabled = false;
        glDisable(GL_SCISSOR_TEST);
    }
    
    public static void scissor(int x, int y, int width, int height) {
        if (scissorX == x && scissorY == y && scissorWidth == width && scissorHeight == height) {
            return;
        }
        scissorX = x;
        scissorY = y;
        scissorWidth = width;
        scissorHeight = height;
        glScissor(x, y, width, height);
    }
    
    public static void reset() {
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
    
    public static int getBoundFramebuffer() {
        return boundFBO;
    }
    
    public static int getBoundVertexArray() {
        return boundVAO;
    }
    
    public static int getBoundArrayBuffer() {
        return boundVBO;
    }
    
    public static int getBoundElementArrayBuffer() {
        return boundIBO;
    }
    
    public static int getBoundProgram() {
        return boundShaderProgram;
    }
    
    public static boolean isBlendEnabled() {
        return blendEnabled;
    }
    
    public static boolean isDepthTestEnabled() {
        return depthTestEnabled;
    }
    
    public static boolean isCullFaceEnabled() {
        return cullFaceEnabled;
    }
    
    public static boolean isScissorEnabled() {
        return scissorEnabled;
    }
}
