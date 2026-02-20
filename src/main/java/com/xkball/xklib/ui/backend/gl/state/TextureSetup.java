package com.xkball.xklib.ui.backend.gl.state;

import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.api.render.ITexture;


import java.util.List;

public record TextureSetup(List<TextureBind> binds) {
    
    public static final TextureSetup EMPTY = new TextureSetup(List.of());
    
    public static TextureSetup singleTexture(ITexture texture){
        return new TextureSetup(List.of(new TextureBind(0, texture)));
    }
    public void apply(IRenderPipeline pipeline){
        for(TextureBind bind : binds){
            pipeline.bindSampler(bind.textureUnit(), bind::texture);
        }
    }
    
    public record TextureBind(int textureUnit, ITexture texture){
    
    }
}
