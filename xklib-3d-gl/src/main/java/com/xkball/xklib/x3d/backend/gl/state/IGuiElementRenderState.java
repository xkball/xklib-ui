package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;

public interface IGuiElementRenderState extends IScreenArea {
    void buildVertices(final BufferBuilder vertexConsumer, float zOffset);

    IRenderPipeline pipeline();

    TextureSetup textureSetup();

    @Nullable
    ScreenRectangle scissorArea();
    
    static @Nullable ScreenRectangle getBounds(float x0, float y0, float x1, float y1, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle bounds = new ScreenRectangle((int) x0, (int) y0, (int) (x1 - x0), (int) (y1 - y0)).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}