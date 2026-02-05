package com.xkball.xklib.ui.backend.gl.vertex;

public class VertexFormats {
    
    public static final VertexFormat POSITION_COLOR = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .build();
    
    public static final VertexFormat POSITION_TEX_COLOR = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV", VertexFormatElement.UV)
            .add("Color", VertexFormatElement.COLOR)
            .build();
    
}
