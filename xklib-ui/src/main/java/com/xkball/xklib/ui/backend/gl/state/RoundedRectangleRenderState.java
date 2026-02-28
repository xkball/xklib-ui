package com.xkball.xklib.ui.backend.gl.state;

import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormatElement;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;
import org.lwjgl.system.MemoryUtil;

public record RoundedRectangleRenderState(
    IRenderPipeline pipeline,
    TextureSetup textureSetup,
    Matrix3x2fc pose,
    int x0,
    int y0,
    int x1,
    int y1,
    int col1,
    int col2,
    int radius,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    public RoundedRectangleRenderState(
        IRenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2fc pose,
        int x0,
        int y0,
        int x1,
        int y1,
        int col1,
        int col2,
        int radius,
        @Nullable ScreenRectangle scissorArea
    ) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2,
             clampRadius(radius, x0, y0, x1, y1),
             scissorArea, getBounds(x0, y0, x1, y1, pose, scissorArea));
    }
    
    private static int clampRadius(int radius, int x0, int y0, int x1, int y1) {
        int width = Math.abs(x1 - x0);
        int height = Math.abs(y1 - y0);
        int maxRadius = Math.min(width, height) / 2;
        return Math.min(radius, maxRadius);
    }

    @Override
    public void buildVertices(BufferBuilder vertexConsumer, float zOffset) {
        float width = this.x1() - this.x0();
        float height = this.y1() - this.y0();
        float r = (float) this.radius();
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), zOffset)
                .setUv(0, 0).setUv2(width,height).setColor(this.col1())
                .setUnsafe(VertexFormatElement.EXTRA_FLOAT,ptr -> MemoryUtil.memPutFloat(ptr, r));
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), zOffset)
                .setUv(0, height).setUv2(width,height).setColor(this.col2())
                .setUnsafe(VertexFormatElement.EXTRA_FLOAT,ptr -> MemoryUtil.memPutFloat(ptr, r));
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), zOffset)
                .setUv(width, height).setUv2(width,height).setColor(this.col2())
                .setUnsafe(VertexFormatElement.EXTRA_FLOAT,ptr -> MemoryUtil.memPutFloat(ptr, r));
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), zOffset)
                .setUv(width, 0).setUv2(width,height).setColor(this.col1())
                .setUnsafe(VertexFormatElement.EXTRA_FLOAT,ptr -> MemoryUtil.memPutFloat(ptr, r));
    }

    private static @Nullable ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}
