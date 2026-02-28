package com.xkball.xklib.ui.backend.window;

import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.gl.OpenGLWorkaround;
import com.xkball.xklib.ui.backend.gl.buffer.VBOBuffer;
import com.xkball.xklib.ui.backend.gl.pipeline.RenderPipelines;
import com.xkball.xklib.ui.backend.gl.texture.AbstractTexture;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormat;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormats;

public class DrawTest1 extends WindowAppBase{
    
    VBOBuffer vbo;
    AbstractTexture texture;
    @Override
    public void init() {
        super.init();
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_TEX_COLOR);
        builder.addVertex(-0.5f, -0.5f, 0.0f).setUv(0,1).setColor(255,255,255,255);
        builder.addVertex(0.5f, -0.5f, 0.0f).setUv(1,1).setColor(255,255,255,255);
        builder.addVertex(0.5f, 0.5f, 0.0f).setUv(1,0).setColor(255,255,255,255);
        
        builder.addVertex(-0.5f, -0.5f, 0.0f).setUv(0,1).setColor(255,255,255,255);
        builder.addVertex(0.5f, 0.5f, 0.0f).setUv(1,0).setColor(255,255,255,255);
        builder.addVertex(-0.5f, 0.5f, 0.0f).setUv(0,0).setColor(255,255,255,255);
        this.vbo = builder.buildAndUpload();
        this.texture = OpenGLWorkaround.textureManager.getTexture(ResourceLocation.of("textures/img.png"));
    }
    
    @Override
    public void render() {
        super.render();
        RenderPipelines.POSITION_TEX_COLOR.bindSampler(0,() -> texture);
        RenderPipelines.POSITION_TEX_COLOR.draw(vbo);
        OpenGLWorkaround.fontRenderer.drawString(OpenGLWorkaround.font,"114514abcd中文", 100, 100, 0xFF0000FF);
        OpenGLWorkaround.fontRenderer.drawString(OpenGLWorkaround.font,String.valueOf(window.getHeight()), 500, 100, 0xFFFF0000);
    }
    
    @Override
    public void close() {
        super.close();
        vbo.destroy();
    }
}
