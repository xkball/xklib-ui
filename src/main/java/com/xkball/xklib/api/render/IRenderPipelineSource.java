package com.xkball.xklib.api.render;

import com.xkball.xklib.XKLibWorkaround;

public interface IRenderPipelineSource {
    
    static IRenderPipelineSource getInstance(){
        return XKLibWorkaround.renderPipelineSource;
    }
    
    IRenderPipeline getGui();
    
    IRenderPipeline getGuiTextured();
    
    IRenderPipeline getGuiRoundedRect();
    
}
