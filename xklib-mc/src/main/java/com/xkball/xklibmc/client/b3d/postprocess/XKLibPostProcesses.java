package com.xkball.xklibmc.client.b3d.postprocess;

import com.xkball.xklibmc.api.client.b3d.SamplerCacheCache;
import com.xkball.xklibmc.x3d.backend.b3d.pipeline.B3dRenderPipelines;

public class XKLibPostProcesses {
    
    public static final PostProcess BLUR = PostProcess.builder()
            .withTexture("input",false,SamplerCacheCache.LINEAR_CLAMP)
            .applyOnce(B3dRenderPipelines.BLUR,"swap", PostProcess::drawcall)
            .swapBack()
            .build("blur");
    
    public static final PostProcess SSAO = PostProcess.builder()
            .withTexture("input",true,SamplerCacheCache.LINEAR_CLAMP)
            .applyOnce(B3dRenderPipelines.SSAO,"swap", PostProcess::drawcall)
            .withTexture("swap",false,SamplerCacheCache.LINEAR_CLAMP)
            .applyOnce(B3dRenderPipelines.BLUR,"output", PostProcess::drawcall)
            .build("ssao");
}

