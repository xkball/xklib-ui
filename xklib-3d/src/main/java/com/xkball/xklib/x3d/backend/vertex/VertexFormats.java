package com.xkball.xklib.x3d.backend.vertex;

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
    
    public static final VertexFormat POSITION_NORMAL = VertexFormat.builder()
             .add("Position", VertexFormatElement.POSITION)
             .add("Normal", VertexFormatElement.NORMAL)
             .build();
    
    public static final VertexFormat POSITION_NORMAL_COLOR = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("Normal", VertexFormatElement.NORMAL)
            .padding(1)
            .build();
    
    public static final VertexFormat POSITION_TEX_NORMAL_COLOR = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV", VertexFormatElement.UV)
            .add("Normal", VertexFormatElement.NORMAL)
            .add("Color", VertexFormatElement.COLOR)
            .build();
    
    public static final VertexFormat POSITION_TEX_UV2_COLOR_EXTRA = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV", VertexFormatElement.UV)
            .add("UV2", VertexFormatElement.UV2)
            .add("Color", VertexFormatElement.COLOR)
            .add("Extra", VertexFormatElement.EXTRA_FLOAT)
            .build();
    
}
