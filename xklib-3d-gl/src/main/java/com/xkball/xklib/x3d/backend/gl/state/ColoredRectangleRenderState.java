package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;

public record ColoredRectangleRenderState(
    IRenderPipeline pipeline,
    TextureSetup textureSetup,
    Matrix3x2fc pose,
    float x0,
    float y0,
    float x1,
    float y1,
    int col1,
    int col2,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements IGuiElementRenderState {
    public ColoredRectangleRenderState(
        IRenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2fc pose,
        float x0,
        float y0,
        float x1,
        float y1,
        int col1,
        int col2,
        @Nullable ScreenRectangle scissorArea
    ) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, getBounds(x0, y0, x1, y1, pose, scissorArea));
    }

    @Override
    public void buildVertices(BufferBuilder vertexConsumer, float zOffset) {
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), zOffset).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), zOffset).setColor(this.col1());
    }

    private static @Nullable ScreenRectangle getBounds(float x0, float y0, float x1, float y1, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle bounds = new ScreenRectangle((int) x0, (int) y0, (int) (x1 - x0), (int) (y1 - y0)).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}