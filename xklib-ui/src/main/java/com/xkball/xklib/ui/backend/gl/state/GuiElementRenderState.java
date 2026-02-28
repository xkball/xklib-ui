package com.xkball.xklib.ui.backend.gl.state;

import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;

public interface GuiElementRenderState extends ScreenArea {
    void buildVertices(final BufferBuilder vertexConsumer, float zOffset);

    IRenderPipeline pipeline();

    TextureSetup textureSetup();

    @Nullable
    ScreenRectangle scissorArea();
}