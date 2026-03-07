package com.xkball.xklib.x3d.backend.window;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.x3d.api.render.IWindow;
import com.xkball.xklib.x3d.api.resource.IWindowFactory;

import java.util.ServiceLoader;

public class WindowAppBase implements Runnable, AutoCloseable {
    
    protected IWindow window;
    
    public void init(){
    
    }
    
    public void render(){
    
    }
    
    @Override
    public void close() throws Exception {
        window.close();
    }
    
    @Override
    public void run() {
        var windowFactoryLoader = ServiceLoader.load(IWindowFactory.class);
        this.window = windowFactoryLoader.findFirst().orElseThrow().createNewWindow();
        this.window.init();
        this.init();
        while (!window.shouldClose()) {
            this.render();
            window.swapBuffer();
        }
    }
    
}
