package com.xkball.xklib.ui;

import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.gl.OpenGLWorkaround;
import com.xkball.xklib.ui.backend.gl.buffer.VBOBuffer;
import com.xkball.xklib.ui.backend.gl.pipeline.RenderPipelines;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormat;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormats;

public class XKLibUI {

    public static final String NAME = "xklib";
    
    public static void main(String[] args) {
        Thread.currentThread().setName("xklib-ui-test-main");
        
        OpenGLWorkaround.init();
        
        var texture = OpenGLWorkaround.textureManager.getTexture(ResourceLocation.of("textures/img.png"));
        
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_TEX_COLOR);
        builder.addVertex(-0.5f, -0.5f, 0.0f).setUv(0,1).setColor(255,255,255,255);
        builder.addVertex(0.5f, -0.5f, 0.0f).setUv(1,1).setColor(255,255,255,255);
        builder.addVertex(0.5f, 0.5f, 0.0f).setUv(1,0).setColor(255,255,255,255);

        builder.addVertex(-0.5f, -0.5f, 0.0f).setUv(0,1).setColor(255,255,255,255);
        builder.addVertex(0.5f, 0.5f, 0.0f).setUv(1,0).setColor(255,255,255,255);
        builder.addVertex(-0.5f, 0.5f, 0.0f).setUv(0,0).setColor(255,255,255,255);
        VBOBuffer vbo = builder.buildAndUpload();
        var window = OpenGLWorkaround.window;
        try {
            while (!window.shouldClose()) {
                window.getFramebuffer().bind();
                window.getFramebuffer().clearWhite();
                
                RenderPipelines.POSITION_TEX_COLOR.setSampler(0,() -> texture);
                RenderPipelines.POSITION_TEX_COLOR.draw(vbo);
                OpenGLWorkaround.fontRenderer.drawString(OpenGLWorkaround.font,"114514abcd中文", 100, 100, 0xFF0000FF);
                OpenGLWorkaround.fontRenderer.drawString(OpenGLWorkaround.font,String.valueOf(window.getHeight()), 500, 100, 0xFFFF0000);
                window.tickAndFlip();
            }
        } finally {
            vbo.destroy();
            window.destroy();
        }
    }
}