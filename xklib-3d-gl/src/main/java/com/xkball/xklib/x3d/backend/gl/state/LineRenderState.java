package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;

public record LineRenderState(
        Matrix3x2fc pose,
        float x0,
        float y0,
        float x1,
        float y1,
        int col1,
        int col2,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements IGuiElementRenderState{
    
    public LineRenderState(
            Matrix3x2fc pose,
            float x0,
            float y0,
            float x1,
            float y1,
            int col1,
            int col2,
            @Nullable ScreenRectangle scissorArea
    ){
        this(pose, x0, y0, x1, y1, col1, col2, scissorArea, IGuiElementRenderState.getBounds(x0,y0,x1,y1,pose,scissorArea));
    }
    
    @Override
    public void buildVertices(BufferBuilder vertexConsumer, float zOffset) {
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset).setUv(0,0).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset).setUv(1,0).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset).setUv(0,0).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset).setUv(0,0).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset).setUv(1,0).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset).setUv(1,0).setColor(this.col2());
    }
    
    @Override
    public IRenderPipeline pipeline() {
        return IRenderPipelineSource.getInstance().getLine();
    }
    
    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.EMPTY;
    }
    
    @Nullable
    @Override
    public ScreenRectangle scissorArea() {
        return scissorArea;
    }
}
