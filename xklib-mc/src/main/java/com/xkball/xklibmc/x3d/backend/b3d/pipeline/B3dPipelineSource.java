package com.xkball.xklibmc.x3d.backend.b3d.pipeline;

import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import net.minecraft.client.renderer.RenderPipelines;

public class B3dPipelineSource implements IRenderPipelineSource {
    @Override
    public IRenderPipeline getGui() {
        return (IRenderPipeline) RenderPipelines.GUI;
    }
    
    @Override
    public IRenderPipeline getGuiTextured() {
        return (IRenderPipeline) RenderPipelines.GUI_TEXTURED;
    }
    
    @Override
    public IRenderPipeline getGuiRoundedRect() {
        return (IRenderPipeline) B3dRenderPipelines.ROUNDED_RECT;
    }
    
    @Override
    public IRenderPipeline getLine() {
        return (IRenderPipeline) B3dRenderPipelines.LINE;
    }
}
