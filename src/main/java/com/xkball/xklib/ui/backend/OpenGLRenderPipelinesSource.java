package com.xkball.xklib.ui.backend;

import com.xkball.xklib.api.render.IRenderPipelineSource;
import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.ui.backend.gl.pipeline.RenderPipelines;

public class OpenGLRenderPipelinesSource implements IRenderPipelineSource {
    
    @Override
    public IRenderPipeline getGui() {
        return RenderPipelines.GUI;
    }
    
    @Override
    public IRenderPipeline getGuiTextured() {
        return RenderPipelines.GUI_TEXTURED;
    }
}
