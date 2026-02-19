package com.xkball.xklib.ui.backend.gl.state;

import com.xkball.xklib.ui.backend.gl.pipeline.RenderPipelines;
import com.xkball.xklib.ui.navigation.ScreenRectangle;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuiRenderState {
    
    private boolean debug = false;
    private final List<Layer> layers = new ArrayList<>();
    private Layer current;
    
    public GuiRenderState(){
        this.clear();
    }
    
    public void setDebug(boolean b){
        this.debug = b;
    }
    
    public void clear(){
        this.layers.clear();
        this.current = new Layer(null);
        this.layers.add(current);
    }
    public void submitGuiElement(GuiElementRenderState blitState) {
        this.current.submitGuiElement(blitState);
        this.sumbitDebugRectangleIfEnabled(blitState.bounds());
    }
    
    private void sumbitDebugRectangleIfEnabled(@Nullable ScreenRectangle bounds) {
        if (this.debug && bounds != null) {
            this.up();
            this.current
                    .submitGuiElement(
                            new ColoredRectangleRenderState(
                                    RenderPipelines.GUI, () -> null, new Matrix3x2f(), 0, 0, 10000, 10000, 2000962815, 2000962815, bounds
                            )
                    );
        }
    }
    
    private void up(){
        if(this.current.up == null){
            this.current.up = new Layer(current);
        }
        this.current = this.current.up;
    }
    
    private void down(){
        if(this.current.parent != null){
            this.current = this.current.parent;
        }
    }
    
    public static class Layer{
        
        public final @Nullable Layer parent;
        public @Nullable Layer up;
        public final List<GuiElementRenderState> elements = new ArrayList<>();
        
        public Layer(@Nullable Layer parent) {
            this.parent = parent;
        }
        
        public void submitGuiElement(GuiElementRenderState blitState) {
            this.elements.add(blitState);
        }
    }
}
