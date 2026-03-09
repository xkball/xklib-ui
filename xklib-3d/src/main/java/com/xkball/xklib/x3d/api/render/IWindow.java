package com.xkball.xklib.x3d.api.render;

public interface IWindow extends AutoCloseable{
    
    long getHandle();
    
    IRenderContext init();
    
    void swapBuffer();
    
    boolean shouldClose();
    
    void onFrameBufferResize(long window, int width, int height);
    
    void onMove(long window, int x, int y);
    
    void onResize(long window, int width, int height);
    
    void onFocus(long window, boolean hasFocus);
    
    void onEnter(long window, boolean cursorEntered);
    
    void onIconify(long window, boolean iconified);
    
    int getX();
    
    int getY();
    
    int getWidth();
    
    int getHeight();
    
    void setVsync(boolean enabled);

}
