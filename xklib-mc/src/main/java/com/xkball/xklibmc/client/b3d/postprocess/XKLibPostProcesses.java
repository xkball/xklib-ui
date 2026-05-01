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
            .regRenderTarget("swap2",false)
            .withTexture("input",true,SamplerCacheCache.LINEAR_CLAMP)
            .applyOnce(B3dRenderPipelines.SSAO,"swap", PostProcess::drawcall)
            .withTexture("swap",false,SamplerCacheCache.LINEAR_CLAMP)
            .applyOnce(B3dRenderPipelines.BLUR,"swap2", PostProcess::drawcall)
            .withTexture("swap2", false, SamplerCacheCache.LINEAR_CLAMP)
            .withTexture("input", false, SamplerCacheCache.LINEAR_CLAMP)
            .applyOnce(B3dRenderPipelines.MIX, "swap", PostProcess::drawcall)
            .swapBack()
            .build("ssao");
    
    public static final PostProcess LINEAR_DEPTH = PostProcess.builder()
            .withTexture("input",true,SamplerCacheCache.LINEAR_CLAMP)
            .applyOnce(B3dRenderPipelines.LINEAR_DEPTH,"swap", PostProcess::drawcall)
            .swapBack()
            .build("linear_depth");
    
    public static final PostProcess SSR = PostProcess.builder()
            .withTexture("input",true,SamplerCacheCache.LINEAR_CLAMP)
            .withTexture("input",false,SamplerCacheCache.LINEAR_CLAMP)
            .applyOnce(B3dRenderPipelines.SSR,"swap", PostProcess::drawcall)
            .swapBack()
            .build("ssr");
}

