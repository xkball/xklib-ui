package com.xkball.xklib.x3d.backend.gl.window;

import com.google.auto.service.AutoService;
import com.xkball.xklib.x3d.api.render.IWindow;
import com.xkball.xklib.x3d.api.resource.IWindowFactory;

@AutoService(IWindowFactory.class)
public class OpenGLWindowFactory implements IWindowFactory {
    @Override
    public IWindow createNewWindow() {
        return new OpenGLWindow();
    }
}
