package com.xkball.xklib.x3d.api.render;

import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.x3d.api.resource.IResourceManager;
import com.xkball.xklib.x3d.api.resource.ITextureManager;

public interface IRenderContext {
    
    IResourceManager getResourceManager();
    
    ITextureManager getTextureManager();
    
    IWindow getWindow();
    
    IRenderPipelineSource getPipelineSource();
    
    IGUIGraphics getGUIGraphics();
    
}
