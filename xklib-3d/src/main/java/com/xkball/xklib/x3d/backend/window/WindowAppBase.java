package com.xkball.xklib.x3d.backend.window;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.utils.FPSLimiter;
import com.xkball.xklib.x3d.api.render.IWindow;
import com.xkball.xklib.x3d.api.resource.IWindowFactory;

import java.util.ServiceLoader;

public class WindowAppBase implements Runnable, AutoCloseable {
    
    protected IWindow window;
    protected boolean isClosed = false;
    public void init(){
    
    }
    
    public void render(){
    
    }
    
    @Override
    public void close() throws Exception {
        this.isClosed = true;
        window.close();
    }
    
    public boolean isClosed(){
        return isClosed;
    }
    
    @Override
    public void run() {
        var windowFactoryLoader = ServiceLoader.load(IWindowFactory.class);
        this.window = windowFactoryLoader.findFirst().orElseThrow().createNewWindow();
        this.window.init();
        window.setVsync(false);
        var fpsLimit = new FPSLimiter(240);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        var profiler = XKLib.RENDER_CONTEXT.get().getProfiler();
        this.init();
        while (!this.isClosed && !window.shouldClose()) {
            profiler.push("frame");
            this.render();
            window.swapBuffer();
            profiler.push("wait");
            fpsLimit.tickFrame();
            profiler.pop();
            profiler.pop();
            profiler.endTick();
        }
    }
    
}
