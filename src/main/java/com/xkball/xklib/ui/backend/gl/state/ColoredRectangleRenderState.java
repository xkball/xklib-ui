package com.xkball.xklib.ui.backend.gl.state;

import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.ui.backend.gl.pipeline.RenderPipeline;
import com.xkball.xklib.ui.backend.gl.texture.AbstractTexture;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.navigation.ScreenRectangle;
import com.xkball.xklib.utils.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;

import java.util.function.Supplier;

public record ColoredRectangleRenderState(
    IRenderPipeline pipeline,
    Supplier<Pair<Integer, AbstractTexture>> textureSetup,
    Matrix3x2fc pose,
    int x0,
    int y0,
    int x1,
    int y1,
    int col1,
    int col2,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    public ColoredRectangleRenderState(
        IRenderPipeline pipeline,
        Supplier<Pair<Integer, AbstractTexture>> textureSetup,
        Matrix3x2fc pose,
        int x0,
        int y0,
        int x1,
        int y1,
        int col1,
        int col2,
        @Nullable ScreenRectangle scissorArea
    ) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, getBounds(x0, y0, x1, y1, pose, scissorArea));
    }

    @Override
    public void buildVertices(BufferBuilder vertexConsumer) {
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1()).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0()).setColor(this.col1());
    }

    private static @Nullable ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}