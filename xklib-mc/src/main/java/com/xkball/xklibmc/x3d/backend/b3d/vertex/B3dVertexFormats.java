package com.xkball.xklibmc.x3d.backend.b3d.vertex;


import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;


public class B3dVertexFormats {

    public static final VertexFormatElement EXTRA_UV = VertexFormatElement.register(
            VertexFormatElement.findNextId(), 0, VertexFormatElement.Type.FLOAT, false, 2
    );
    
    public static final VertexFormatElement EXTRA_FLOAT = VertexFormatElement.register(
            VertexFormatElement.findNextId(), 0, VertexFormatElement.Type.FLOAT, false, 1
    );
    
    public static final VertexFormat POSITION_TEX_UV2_COLOR_EXTRA = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV", VertexFormatElement.UV)
            .add("UV2", EXTRA_UV)
            .add("Color", VertexFormatElement.COLOR)
            .add("Extra", EXTRA_FLOAT)
            .build();
}
