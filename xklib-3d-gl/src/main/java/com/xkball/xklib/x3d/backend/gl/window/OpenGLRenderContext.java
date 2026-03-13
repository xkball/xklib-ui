package com.xkball.xklib.x3d.backend.gl.window;

import com.xkball.xklib.api.IProfiler;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.utils.SimpleProfiler;
import com.xkball.xklib.x3d.api.render.IBufferSource;
import com.xkball.xklib.x3d.api.render.IRenderContext;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.api.render.IWindow;
import com.xkball.xklib.x3d.api.resource.IResourceManager;
import com.xkball.xklib.x3d.api.resource.ITextureManager;
import com.xkball.xklib.x3d.backend.gl.OpenGLGUIGraphics;
import com.xkball.xklib.x3d.backend.gl.font.Font;
import com.xkball.xklib.x3d.backend.gl.state.GuiRenderer;

public class OpenGLRenderContext implements IRenderContext {
    
    private final OpenGLWindow window;
    private final GuiRenderer guiRenderer = new GuiRenderer();
    private final Font font = new Font();
    private final OpenGLGUIGraphics guiGraphics = new OpenGLGUIGraphics(guiRenderer.state, font, _ -> guiRenderer.draw());
    private final IProfiler profiler = new SimpleProfiler();
    
    public OpenGLRenderContext(OpenGLWindow window) {
        this.window = window;
    }
    
    @Override
    public IBufferSource getBufferSource() {
        return IBufferSource.getInstance();
    }
    
    @Override
    public IResourceManager getResourceManager() {
        return IResourceManager.getInstance();
    }
    
    @Override
    public ITextureManager getTextureManager() {
        return ITextureManager.getInstance();
    }
    
    @Override
    public IWindow getWindow() {
        return this.window;
    }
    
    @Override
    public IRenderPipelineSource getPipelineSource() {
        return IRenderPipelineSource.getInstance();
    }
    
    @Override
    public IGUIGraphics getGUIGraphics() {
        return this.guiGraphics;
    }
    
    @Override
    public IProfiler getProfiler() {
        return this.profiler;
    }
}
