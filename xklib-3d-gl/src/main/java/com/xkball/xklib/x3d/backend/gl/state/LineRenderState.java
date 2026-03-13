package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

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
    
    private static final Matrix3x2fc ROTATE90 = new Matrix3x2f().rotate((float) Math.toRadians(90));
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
        var dir = new Vector2f(x1-x0,y1-y0);
        dir = ROTATE90.transformDirection(dir).normalize();
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset).setNormal(dir.x,dir.y,0).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset).setNormal(dir.x,dir.y,1).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset).setNormal(dir.x,dir.y,0).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset).setNormal(dir.x,dir.y,0).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset).setNormal(dir.x,dir.y,1).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset).setNormal(dir.x,dir.y,1).setColor(this.col2());
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
