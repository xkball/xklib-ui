package com.xkball.xklibmc.x3d.backend.b3d.gui.element;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.api.client.mixin.IExtendedBufferBuilder;
import com.xkball.xklibmc.x3d.backend.b3d.pipeline.B3dRenderPipelines;
import com.xkball.xklibmc.x3d.backend.b3d.vertex.B3dVertexFormats;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

@NonNullByDefault
public record RoundedRectangleRenderState(
    TextureSetup textureSetup,
    Matrix3x2fc pose,
    float x0,
    float y0,
    float x1,
    float y1,
    int col1,
    int col2,
    float radius,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    public RoundedRectangleRenderState(
        TextureSetup textureSetup,
        Matrix3x2fc pose,
        float x0,
        float y0,
        float x1,
        float y1,
        int col1,
        int col2,
        float radius,
        @Nullable ScreenRectangle scissorArea
    ) {
        this(textureSetup, pose, x0, y0, x1, y1, col1, col2,
             clampRadius(radius, x0, y0, x1, y1),
             scissorArea, getBounds(x0, y0, x1, y1, pose, scissorArea));
    }
    
    @Override
    public RenderPipeline pipeline() {
        return B3dRenderPipelines.ROUNDED_RECT;
    }
    
    private static float clampRadius(float radius, float x0, float y0, float x1, float y1) {
        float width = Math.abs(x1 - x0);
        float height = Math.abs(y1 - y0);
        float maxRadius = Math.min(width, height) / 2;
        return Math.min(radius, maxRadius);
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer) {
        float w = this.x1() - this.x0();
        float h = this.y1() - this.y0();
        var r = this.radius();
        if(vertexConsumer instanceof IExtendedBufferBuilder bufferBuilder){
            vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setUv(0, 0).setColor(this.col1());
            bufferBuilder.setUnsafe(B3dVertexFormats.EXTRA_FLOAT,ptr -> MemoryUtil.memPutFloat(ptr,r));
            bufferBuilder.setUnsafe(B3dVertexFormats.EXTRA_UV,ptr -> {
                MemoryUtil.memPutFloat(ptr,w);
                MemoryUtil.memPutFloat(ptr + 4,h);
            });
            vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1()).setUv(0, h).setColor(this.col2());
            bufferBuilder.setUnsafe(B3dVertexFormats.EXTRA_FLOAT,ptr -> MemoryUtil.memPutFloat(ptr,r));
            bufferBuilder.setUnsafe(B3dVertexFormats.EXTRA_UV,ptr -> {
                MemoryUtil.memPutFloat(ptr,w);
                MemoryUtil.memPutFloat(ptr + 4,h);
            });
            vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setUv(w, h).setColor(this.col2());
            bufferBuilder.setUnsafe(B3dVertexFormats.EXTRA_FLOAT,ptr -> MemoryUtil.memPutFloat(ptr,r));
            bufferBuilder.setUnsafe(B3dVertexFormats.EXTRA_UV,ptr -> {
                MemoryUtil.memPutFloat(ptr,w);
                MemoryUtil.memPutFloat(ptr + 4,h);
            });
            vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0()).setUv(w, 0).setColor(this.col1());
            bufferBuilder.setUnsafe(B3dVertexFormats.EXTRA_FLOAT,ptr -> MemoryUtil.memPutFloat(ptr,r));
            bufferBuilder.setUnsafe(B3dVertexFormats.EXTRA_UV,ptr -> {
                MemoryUtil.memPutFloat(ptr,w);
                MemoryUtil.memPutFloat(ptr + 4,h);
            });
            
        }

    }
    
    @Nullable
    private static ScreenRectangle getBounds(float x0, float y0, float x1, float y1, Matrix3x2fc pose,@Nullable ScreenRectangle scissorArea) {
        ScreenRectangle bounds = new ScreenRectangle((int) x0, (int) y0, (int) (x1 - x0), (int) (y1 - y0)).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}