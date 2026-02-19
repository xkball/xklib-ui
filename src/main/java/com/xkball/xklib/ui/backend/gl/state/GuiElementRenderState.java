package com.xkball.xklib.ui.backend.gl.state;

import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.ui.backend.gl.texture.AbstractTexture;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.navigation.ScreenRectangle;
import com.xkball.xklib.utils.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface GuiElementRenderState extends ScreenArea {
    void buildVertices(final BufferBuilder vertexConsumer);

    IRenderPipeline pipeline();

    Supplier<Pair<Integer, AbstractTexture>> textureSetup();

    @Nullable
    ScreenRectangle scissorArea();
}