package com.xkball.xklib.x3d.backend.window;

import com.xkball.xklib.x3d.api.render.IWindow;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.*;

public abstract class Window implements IWindow {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);

    protected int width;
    protected int height;
    protected int x;
    protected int y;
    protected boolean iconified;
    protected boolean minimized;
    protected final String title;
    protected long handle;
    

    public Window() {
        this(1280, 720, "xklib");
    }

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    @Override
    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    @Override
    public long getHandle() {
        return handle;
    }
    
    @Override
    public void onMove(long window, int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void onResize(long window, int width, int height) {
//        this.width = width;
//        this.height = height;
    }
    
    @Override
    public void onFocus(long window, boolean hasFocus) {
    }
    
    @Override
    public void onEnter(long window, boolean cursorEntered) {
    }
    
    @Override
    public void onIconify(long window, boolean iconified) {
        this.iconified = iconified;
    }
    
    @Override
    public int getX() {
        return x;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getY() {
        return y;
    }
    
    @Override
    public void setVsync(boolean enabled) {
        GLFW.glfwSwapInterval(enabled ? 1 : 0);
    }
}
