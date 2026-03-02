package com.xkball.xklib.x3d.backend.gl.window;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.x3d.api.render.IRenderContext;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.api.render.IWindow;
import com.xkball.xklib.x3d.api.resource.IResourceManager;
import com.xkball.xklib.x3d.api.resource.ITextureManager;
import com.xkball.xklib.x3d.backend.gl.GLStateManager;
import com.xkball.xklib.x3d.backend.gl.buffer.FrameBuffer;
import com.xkball.xklib.x3d.backend.window.Window;
import com.xkball.xklib.x3d.backend.window.WindowEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

public class OpenGLWindow extends Window {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenGLWindow.class);
    private static final int MAX_VERSION_MAJOR = 4;
    private static final int MAX_VERSION_MINOR = 6;
    
    
    protected FrameBuffer framebuffer;
    @Override
    public IRenderContext init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }
        
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        
        handle = createWindowWithHighestVersion(width, height, title);
        if (handle == MemoryUtil.NULL) {
            glfwTerminate();
            throw new IllegalStateException("Failed to create GLFW window with OpenGL 4.x");
        }
        
        glfwMakeContextCurrent(handle);
        GLCapabilities caps = GL.createCapabilities();
        if (!caps.OpenGL45) {
            glfwDestroyWindow(handle);
            glfwTerminate();
            throw new IllegalStateException("OpenGL 4.5 is required");
        }
        
        glfwSwapInterval(1);
        
        framebuffer = new FrameBuffer(width,height);
        org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFrameBufferResize);
        GLFW.glfwSetWindowPosCallback(this.handle, this::onMove);
        GLFW.glfwSetWindowSizeCallback(this.handle, this::onResize);
        GLFW.glfwSetWindowFocusCallback(this.handle, this::onFocus);
        GLFW.glfwSetCursorEnterCallback(this.handle, this::onEnter);
        GLFW.glfwSetWindowIconifyCallback(this.handle, this::onIconify);
        XKLib.EVENT_BUS.call(new WindowEvent.Init(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
        
        return new OpenGLRenderContext(this);
    }
    
    public FrameBuffer getFramebuffer(){
        return framebuffer;
    }
    
    private long createWindowWithHighestVersion(int width, int height, String title) {
        for (int minor = MAX_VERSION_MINOR; minor >= 0; minor--) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, MAX_VERSION_MAJOR);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, minor);
            long window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
            if (window != MemoryUtil.NULL) {
                LOGGER.info("Creating Window with OpenGL {}.{}", MAX_VERSION_MAJOR, minor);
                return window;
            }
        }
        return MemoryUtil.NULL;
    }
    
    
    @Override
    public void swapBuffer() {
        framebuffer.blitToDefault();
        glfwSwapBuffers(handle);
        glfwPollEvents();
        framebuffer.bind();
        framebuffer.clearWhite();
    }
    
    @Override
    public void onFrameBufferResize(long window, int width, int height) {
        if(window != this.handle){
            return;
        }
        this.width = width;
        this.height = height;
        if (this.framebuffer != null && width > 0 && height > 0) {
            this.framebuffer.resize(width, height);
            this.minimized = false;
        }
        else {
            this.minimized = true;
        }
        GLStateManager.bindFramebuffer(GL45.GL_FRAMEBUFFER, 0);
        GL45.glViewport(0,0,width,height);
        XKLib.EVENT_BUS.call(new WindowEvent.Resize(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
    }
    
    @Override
    public void close() {
        if (framebuffer != null) {
            framebuffer.destroy();
            framebuffer = null;
        }
        if (handle != MemoryUtil.NULL) {
            glfwDestroyWindow(handle);
            handle = MemoryUtil.NULL;
        }
        glfwTerminate();
    }
}
