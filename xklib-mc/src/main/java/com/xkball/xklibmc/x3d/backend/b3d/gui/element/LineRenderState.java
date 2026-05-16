package com.xkball.xklibmc.x3d.backend.b3d.gui.element;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xkball.xklibmc.api.client.mixin.IExtendedBufferBuilder;
import com.xkball.xklibmc.x3d.backend.b3d.pipeline.B3dRenderPipelines;
import com.xkball.xklibmc.x3d.backend.b3d.vertex.B3dVertexFormats;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

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
) implements GuiElementRenderState {

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
        this(pose, x0, y0, x1, y1, col1, col2, scissorArea,getBounds(x0,y0,x1,y1,pose,scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer) {
        if (vertexConsumer instanceof IExtendedBufferBuilder bufferBuilder) {
            Vector2f p0w = this.pose().transformPosition(this.x0(), this.y0(), new Vector2f());
            float p0x = p0w.x();
            float p0y = p0w.y();

            Vector2f p1w = this.pose().transformPosition(this.x1(), this.y1(), new Vector2f());
            float p1x = p1w.x();
            float p1y = p1w.y();

            vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setColor(this.col1());
            bufferBuilder.setUnsafe(B3dVertexFormats.P0, ptr -> { MemoryUtil.memPutFloat(ptr, p0x); MemoryUtil.memPutFloat(ptr + 4, p0y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.P1, ptr -> { MemoryUtil.memPutFloat(ptr, p1x); MemoryUtil.memPutFloat(ptr + 4, p1y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.CORNER, ptr -> { MemoryUtil.memPutFloat(ptr, -1.0f); MemoryUtil.memPutFloat(ptr + 4, 0.0f); });

            vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setColor(this.col2());
            bufferBuilder.setUnsafe(B3dVertexFormats.P0, ptr -> { MemoryUtil.memPutFloat(ptr, p0x); MemoryUtil.memPutFloat(ptr + 4, p0y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.P1, ptr -> { MemoryUtil.memPutFloat(ptr, p1x); MemoryUtil.memPutFloat(ptr + 4, p1y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.CORNER, ptr -> { MemoryUtil.memPutFloat(ptr, 1.0f); MemoryUtil.memPutFloat(ptr + 4, 0.0f); });

            vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col2());
            bufferBuilder.setUnsafe(B3dVertexFormats.P0, ptr -> { MemoryUtil.memPutFloat(ptr, p0x); MemoryUtil.memPutFloat(ptr + 4, p0y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.P1, ptr -> { MemoryUtil.memPutFloat(ptr, p1x); MemoryUtil.memPutFloat(ptr + 4, p1y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.CORNER, ptr -> { MemoryUtil.memPutFloat(ptr, -1.0f); MemoryUtil.memPutFloat(ptr + 4, 1.0f); });

            vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col2());
            bufferBuilder.setUnsafe(B3dVertexFormats.P0, ptr -> { MemoryUtil.memPutFloat(ptr, p0x); MemoryUtil.memPutFloat(ptr + 4, p0y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.P1, ptr -> { MemoryUtil.memPutFloat(ptr, p1x); MemoryUtil.memPutFloat(ptr + 4, p1y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.CORNER, ptr -> { MemoryUtil.memPutFloat(ptr, -1.0f); MemoryUtil.memPutFloat(ptr + 4, 1.0f); });

            vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col1());
            bufferBuilder.setUnsafe(B3dVertexFormats.P0, ptr -> { MemoryUtil.memPutFloat(ptr, p0x); MemoryUtil.memPutFloat(ptr + 4, p0y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.P1, ptr -> { MemoryUtil.memPutFloat(ptr, p1x); MemoryUtil.memPutFloat(ptr + 4, p1y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.CORNER, ptr -> { MemoryUtil.memPutFloat(ptr, 1.0f); MemoryUtil.memPutFloat(ptr + 4, 1.0f); });

            vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setColor(this.col2());
            bufferBuilder.setUnsafe(B3dVertexFormats.P0, ptr -> { MemoryUtil.memPutFloat(ptr, p0x); MemoryUtil.memPutFloat(ptr + 4, p0y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.P1, ptr -> { MemoryUtil.memPutFloat(ptr, p1x); MemoryUtil.memPutFloat(ptr + 4, p1y); MemoryUtil.memPutFloat(ptr + 8, 0.0f); });
            bufferBuilder.setUnsafe(B3dVertexFormats.CORNER, ptr -> { MemoryUtil.memPutFloat(ptr, 1.0f); MemoryUtil.memPutFloat(ptr + 4, 0.0f); });
        }
    }

    @Override
    public @NonNull RenderPipeline pipeline() {
        return B3dRenderPipelines.LINE;
    }

    @Override
    public @NonNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Nullable
    @Override
    public ScreenRectangle scissorArea() {
        return scissorArea;
    }

    private static ScreenRectangle getBounds(float x0, float y0, float x1, float y1, Matrix3x2fc pose, ScreenRectangle scissorArea) {
        ScreenRectangle bounds = new ScreenRectangle((int) x0, (int) y0, (int) (x1 - x0), (int) (y1 - y0)).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}
