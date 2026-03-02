package com.xkball.xklib.x3d.api.render;

import com.xkball.xklib.utils.Lazy;

import java.util.ServiceLoader;

public interface IRenderPipelineSource {
    
    Lazy<IRenderPipelineSource> INSTANCE = Lazy.of(() -> {
        var loader = ServiceLoader.load(IRenderPipelineSource.class);
        return loader.findFirst().orElseThrow();
    });
    
    static IRenderPipelineSource getInstance(){
        return INSTANCE.get();
    }
    
    IRenderPipeline getGui();
    
    IRenderPipeline getGuiTextured();
    
    IRenderPipeline getGuiRoundedRect();
}
