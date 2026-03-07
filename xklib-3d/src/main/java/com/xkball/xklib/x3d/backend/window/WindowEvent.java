package com.xkball.xklib.x3d.backend.window;

import com.xkball.xklib.x3d.api.event.IEvent;
import com.xkball.xklib.x3d.api.render.IWindow;

public class WindowEvent implements IEvent {
    
    public final IWindow window;
    
    public WindowEvent(IWindow window) {
        this.window = window;;
    }
    
    public static class Init extends WindowEvent {
        public Init(IWindow window) {
            super(window);
        }
    }
    
    public static class Resize extends WindowEvent {
        public Resize(IWindow window) {
            super(window);
        }
    }
    
}
