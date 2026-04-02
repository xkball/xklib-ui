package com.xkball.xklibmc.x3d.backend.b3d;

import com.xkball.xklib.api.IProfiler;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.utils.SimpleProfiler;
import com.xkball.xklib.x3d.api.render.IBufferSource;
import com.xkball.xklib.x3d.api.render.IRenderContext;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.api.render.IWindow;
import com.xkball.xklib.x3d.api.resource.IResourceManager;
import com.xkball.xklib.x3d.api.resource.ITextureManager;
import com.xkball.xklibmc.x3d.backend.b3d.pipeline.B3dPipelineSource;
import com.xkball.xklibmc.x3d.backend.b3d.texture.B3dTextureManager;
import net.minecraft.client.Minecraft;

public class B3dRenderContext implements IRenderContext {
    
    private final ITextureManager textureManager;
    private final IRenderPipelineSource renderPipelineSource;
    private final IProfiler profiler = new SimpleProfiler();
    private final IGUIGraphics guiGraphics;
    
    public B3dRenderContext() {
        var mc = Minecraft.getInstance();
        this.textureManager = new B3dTextureManager(mc.getTextureManager(),mc.getAtlasManager());
        this.renderPipelineSource = new B3dPipelineSource();
        this.guiGraphics = new B3dGuiGraphics(textureManager);
    }
    
    @Override
    public IBufferSource getBufferSource() {
        return null;
    }
    
    @Override
    public IResourceManager getResourceManager() {
        return null;
    }
    
    @Override
    public ITextureManager getTextureManager() {
        return textureManager;
    }
    
    @Override
    public IWindow getWindow() {
        return (IWindow) (Object) Minecraft.getInstance().getWindow();
    }
    
    @Override
    public IRenderPipelineSource getPipelineSource() {
        return this.renderPipelineSource;
    }
    
    @Override
    public IGUIGraphics getGUIGraphics() {
        return guiGraphics;
    }
    
    @Override
    public IProfiler getProfiler() {
        return profiler;
    }
}
