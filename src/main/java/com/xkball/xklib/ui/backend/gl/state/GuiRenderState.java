package com.xkball.xklib.ui.backend.gl.state;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuiRenderState {
    
    private final List<Layer> layers = new ArrayList<>();
    private Layer current;
    
    public GuiRenderState(){
        this.clear();
    }
    
    public void clear(){
        this.layers.clear();
        this.current = new Layer(null);
        this.layers.add(current);
    }
    
    public void submitGuiElement(GuiElementRenderState blitState) {
        this.current.submitGuiElement(blitState);
    }

//    private void sumbitDebugRectangleIfEnabled(@Nullable ScreenRectangle bounds) {
//        if (this.debug && bounds != null) {
//            this.up();
//            this.current
//                    .submitGuiElement(
//                            new ColoredRectangleRenderState(
//                                    RenderPipelines.GUI, TextureSetup.EMPTY, new Matrix3x2f(), 0, 0, 10000, 10000, 2000962815, 2000962815, bounds
//                            )
//                    );
//            this.down();
//        }
//    }
    
    public void up(){
        if(this.current.up == null){
            this.current.up = new Layer(current);
            this.layers.add(this.current.up);
        }
        this.current = this.current.up;
    }
    
    public void down(){
        if(this.current.parent != null){
            this.current = this.current.parent;
        }
    }
    
    public List<Layer> layers() {
        return this.layers;
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
