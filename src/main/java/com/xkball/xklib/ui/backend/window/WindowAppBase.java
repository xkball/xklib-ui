package com.xkball.xklib.ui.backend.window;

import com.xkball.xklib.ui.backend.gl.OpenGLWorkaround;

public class WindowAppBase implements Runnable, AutoCloseable {
    
    protected Window window;
    
    public void init(){
    
    }
    
    public void render(){
    
    }
    
    @Override
    public void close(){
        window.destroy();
    }
    
    @Override
    public void run() {
        OpenGLWorkaround.init();
        this.window = OpenGLWorkaround.window;
        this.init();
        try {
            while (!window.shouldClose()) {
                window.getFramebuffer().bind();
                window.getFramebuffer().clearWhite();
                this.render();
                window.tickAndFlip();
            }
        } finally {
            this.close();
        }
    }
    
    
}
