package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.backend.gl.GLStateManager;
import com.xkball.xklib.x3d.backend.gl.window.OpenGLWindow;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;

public class GuiRenderer {
    
    public final GuiRenderState state = new GuiRenderState();
    private final Matrix4f projectionMatrix = new Matrix4f();
    
    public void draw() {
        var window = (OpenGLWindow) XKLib.RENDER_CONTEXT.get().getWindow();
        //state.setDebug(true);
        window.getFramebuffer().clearDepthStencil();
        projectionMatrix.setOrtho(0.0F, window.getWidth(), window.getHeight(), 0.0F, 1, 10000, true);
        var z = 2f;
        var layerDepth = 9995/state.layers().size();
        boolean lastScissorEnabled = false;
        for (var layer : state.layers()) {
            if (layer.elements.isEmpty()) continue;
            var offset = layerDepth / layer.elements.size();
            var builderMap = new LinkedHashMap<Draw, BufferBuilder>();
            for (var ele : layer.elements) {
                var builder = builderMap.computeIfAbsent(Draw.of(ele), rl -> BufferBuilder.start(ele.pipeline().mode(), ele.pipeline().format()));
                ele.buildVertices(builder, z);
                z += offset;
            }
            for (var entry : builderMap.entrySet()) {
                var draw = entry.getKey();
                var pipeline = draw.pipeline;
                pipeline.shader().safeSetUniform("projMatrix", u -> u.set(projectionMatrix));
                draw.textureSetup().apply(pipeline);
                if(draw.scissorArea != null){
                    var scissor = draw.scissorArea;
                    GLStateManager.INSTANCE.get().scissor(scissor.left(), window.getHeight() - scissor.top() - scissor.height(), scissor.width(), scissor.height());
                    if(!lastScissorEnabled) GLStateManager.INSTANCE.get().enableScissor();
                }
                else if(lastScissorEnabled) GLStateManager.INSTANCE.get().disableScissor();
                pipeline.draw(entry.getValue());
                lastScissorEnabled = draw.scissorArea != null;
            }
            window.getFramebuffer().clearDepthStencil();
        }
        GLStateManager.INSTANCE.get().disableScissor();
        this.state.clear();
        window.getFramebuffer().clearDepthStencil();
    }
    
    public record Draw(IRenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea){
        
        public static Draw of(IGuiElementRenderState state){
            return new Draw(state.pipeline(),state.textureSetup(),state.scissorArea());
        }
        
        
    }
}
