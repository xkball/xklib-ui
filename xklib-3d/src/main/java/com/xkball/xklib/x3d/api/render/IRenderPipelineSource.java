package com.xkball.xklib.x3d.api.render;

import com.xkball.xklib.utils.Lazy;

public interface IRenderPipelineSource {
    
    Lazy<IRenderPipelineSource> INSTANCE = Lazy.ofSPI(IRenderPipelineSource.class);
    
    static IRenderPipelineSource getInstance(){
        return INSTANCE.get();
    }
    
    IRenderPipeline getGui();
    
    IRenderPipeline getGuiTextured();
    
    IRenderPipeline getGuiRoundedRect();
}
