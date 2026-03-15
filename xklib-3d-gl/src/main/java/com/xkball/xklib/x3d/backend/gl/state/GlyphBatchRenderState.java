package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;

import java.util.List;

public record GlyphBatchRenderState(
    IRenderPipeline pipeline,
    TextureSetup textureSetup,
    Matrix3x2fc pose,
    List<GlyphQuad> glyphs,
    float zBias,
    @Nullable ScreenRectangle scissorArea
) implements IGuiElementRenderState {

    @Override
    public void buildVertices(BufferBuilder vertexConsumer, float zOffset) {
        float z = zOffset + zBias;
        for (GlyphQuad quad : glyphs) {
            vertexConsumer.addVertexWith2DPose(pose, quad.x0(), quad.y0(), z).setUv(quad.u0(), quad.v0()).setColor(quad.color());
            vertexConsumer.addVertexWith2DPose(pose, quad.x1(), quad.y0(), z).setUv(quad.u1(), quad.v0()).setColor(quad.color());
            vertexConsumer.addVertexWith2DPose(pose, quad.x1(), quad.y1(), z).setUv(quad.u1(), quad.v1()).setColor(quad.color());
            vertexConsumer.addVertexWith2DPose(pose, quad.x0(), quad.y0(), z).setUv(quad.u0(), quad.v0()).setColor(quad.color());
            vertexConsumer.addVertexWith2DPose(pose, quad.x1(), quad.y1(), z).setUv(quad.u1(), quad.v1()).setColor(quad.color());
            vertexConsumer.addVertexWith2DPose(pose, quad.x0(), quad.y1(), z).setUv(quad.u0(), quad.v1()).setColor(quad.color());
        }
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return null;
    }

    public record GlyphQuad(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, int color) {
    }
}

