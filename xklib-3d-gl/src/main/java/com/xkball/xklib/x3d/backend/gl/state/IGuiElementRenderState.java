package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;

public interface IGuiElementRenderState extends IScreenArea {
    void buildVertices(final BufferBuilder vertexConsumer, float zOffset);

    IRenderPipeline pipeline();

    TextureSetup textureSetup();

    @Nullable
    ScreenRectangle scissorArea();
}