package com.xkball.xklib.ui.backend;

import com.xkball.xklib.ui.backend.gl.GLStateManager;
import com.xkball.xklib.ui.backend.gl.buffer.FrameBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);
    private static final int MAX_VERSION_MAJOR = 4;
    private static final int MAX_VERSION_MINOR = 6;

    private int width;
    private int height;
    private int x;
    private int y;
    private boolean iconified;
    private boolean minimized;
    private final String title;
    private long handle;
    private FrameBuffer framebuffer;

    public Window() {
        this(1280, 720, "xklib");
    }

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void init() {
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
        GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferResize);
        GLFW.glfwSetWindowPosCallback(this.handle, this::onMove);
        GLFW.glfwSetWindowSizeCallback(this.handle, this::onResize);
        GLFW.glfwSetWindowFocusCallback(this.handle, this::onFocus);
        GLFW.glfwSetCursorEnterCallback(this.handle, this::onEnter);
        GLFW.glfwSetWindowIconifyCallback(this.handle, this::onIconify);
    }
    
    public FrameBuffer getFramebuffer(){
            return framebuffer;
    }

    public void tickAndFlip() {
        framebuffer.blitToDefault();
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public long getHandle() {
        return handle;
    }

    public void destroy() {
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
    
    private void onFramebufferResize(long window, int width, int height) {
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
    }
    
    private void onMove(long window, int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    private void onResize(long window, int width, int height) {
//        this.width = width;
//        this.height = height;
    }
    
    private void onFocus(long window, boolean hasFocus) {
        if (window == this.handle) {
        
        }
    }
    
    private void onEnter(long window, boolean cursorEntered) {
        if (cursorEntered) {
        
        }
    }
    
    private void onIconify(long window, boolean iconified) {
        this.iconified = iconified;
    }
    
    public int getX() {
        return x;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getY() {
        return y;
    }
}
