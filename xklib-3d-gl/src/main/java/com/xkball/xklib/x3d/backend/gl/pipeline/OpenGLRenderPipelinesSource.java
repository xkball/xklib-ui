package com.xkball.xklib.x3d.backend.gl.pipeline;

import com.google.auto.service.AutoService;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;

@AutoService(IRenderPipelineSource.class)
public class OpenGLRenderPipelinesSource implements IRenderPipelineSource {
    
    @Override
    public IRenderPipeline getGui() {
        return RenderPipelines.GUI;
    }
    
    @Override
    public IRenderPipeline getGuiTextured() {
        return RenderPipelines.GUI_TEXTURED;
    }
    
    @Override
    public IRenderPipeline getGuiRoundedRect() {
        return RenderPipelines.GUI_ROUNDED_RECT;
    }
}
