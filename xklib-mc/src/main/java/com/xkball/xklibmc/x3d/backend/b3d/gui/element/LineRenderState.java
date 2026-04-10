package com.xkball.xklibmc.x3d.backend.b3d.gui.element;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xkball.xklibmc.x3d.backend.b3d.pipeline.B3dRenderPipelines;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
    
    public static final Matrix3x2fc ROTATE90 = new Matrix3x2f().rotate((float) Math.toRadians(90));
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
        var dir = new Vector2f(x1-x0,y1-y0);
        dir = ROTATE90.transformDirection(dir).normalize();
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setNormal(dir.x,dir.y,0).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setNormal(dir.x,dir.y,1).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setNormal(dir.x,dir.y,0).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setNormal(dir.x,dir.y,0).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setNormal(dir.x,dir.y,1).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setNormal(dir.x,dir.y,1).setColor(this.col2());
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