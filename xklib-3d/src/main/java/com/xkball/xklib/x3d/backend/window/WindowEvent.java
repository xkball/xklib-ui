package com.xkball.xklib.x3d.backend.window;

import com.xkball.xklib.x3d.api.event.IEvent;

public class WindowEvent implements IEvent {
    
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public WindowEvent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public static class Init extends WindowEvent {
        public Init(int x, int y, int width, int height) {
            super(x, y, width, height);
        }
    }
    
    public static class Resize extends WindowEvent {
        public Resize(int x, int y, int width, int height) {
            super(x, y, width, height);
        }
    }
    
}
