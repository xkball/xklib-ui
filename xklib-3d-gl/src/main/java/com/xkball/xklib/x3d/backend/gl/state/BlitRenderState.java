package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record BlitRenderState(
    IRenderPipeline pipeline,
    TextureSetup textureSetup,
    Matrix3x2f pose,
    float x0,
    float y0,
    float x1,
    float y1,
    float u0,
    float u1,
    float v0,
    float v1,
    int color,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements IGuiElementRenderState {
    public BlitRenderState(
        IRenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        float x0,
        float y0,
        float x1,
        float y1,
        float u0,
        float u1,
        float v0,
        float v1,
        int color,
        @Nullable ScreenRectangle scissorArea
    ) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, u0, u1, v0, v1, color, scissorArea, IGuiElementRenderState.getBounds(x0, y0, x1, y1, pose, scissorArea));
    }

    @Override
    public void buildVertices(BufferBuilder vertexConsumer, float zOffset) {
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset).setUv(this.u0(), this.v0()).setColor(this.color());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), zOffset).setUv(this.u0(), this.v1()).setColor(this.color());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset).setUv(this.u1(), this.v1()).setColor(this.color());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), zOffset).setUv(this.u1(), this.v0()).setColor(this.color());
    }
    
}