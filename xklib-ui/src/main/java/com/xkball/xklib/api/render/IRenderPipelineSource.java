package com.xkball.xklib.api.render;

import com.xkball.xklib.XKLib;

public interface IRenderPipelineSource {
    
    static IRenderPipelineSource getInstance(){
        return XKLib.renderPipelineSource;
    }
    
    IRenderPipeline getGui();
    
    IRenderPipeline getGuiTextured();
    
    IRenderPipeline getGuiRoundedRect();
    
}
