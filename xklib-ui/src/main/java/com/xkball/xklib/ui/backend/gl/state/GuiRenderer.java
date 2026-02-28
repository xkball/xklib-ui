package com.xkball.xklib.ui.backend.gl.state;

import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.ui.backend.gl.GLStateManager;
import com.xkball.xklib.ui.backend.gl.OpenGLGUIGraphics;
import com.xkball.xklib.ui.backend.gl.OpenGLWorkaround;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;

public class GuiRenderer {
    
    private final GuiRenderState state = new GuiRenderState();
    private final OpenGLGUIGraphics guiGraphics = new OpenGLGUIGraphics(state);
    private final Matrix4f projectionMatrix = new Matrix4f();
    
    public IGUIGraphics getGuiGraphics() {
        return guiGraphics;
    }
    
    public void draw() {
        var window = OpenGLWorkaround.window;
        //state.setDebug(true);
        window.getFramebuffer().clearDepthStencil();
        projectionMatrix.setOrtho(0.0F, window.getWidth(), window.getHeight(), 0.0F, 1, 10000, true);
        var z = 2f;
        var layerDepth = 9995/state.layers().size();
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
                    GLStateManager.scissor(scissor.left(), window.getHeight() - scissor.top() - scissor.height(), scissor.width(), scissor.height());
                    GLStateManager.enableScissor();
                }
                pipeline.draw(entry.getValue());
                if(draw.scissorArea != null) {
                    GLStateManager.disableScissor();
                }
            }
            window.getFramebuffer().clearDepthStencil();
        }
        this.state.clear();
        window.getFramebuffer().clearDepthStencil();
    }
    
    public record Draw(IRenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea){
        
        public static Draw of(GuiElementRenderState state){
            return new Draw(state.pipeline(),state.textureSetup(),state.scissorArea());
        }
        
        
    }
}
