#version 460
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

struct PosColor{
    vec3 pos_ssbo;
    int color_ssbo;
};

layout(std140, binding = 0) buffer ABlock {
    PosColor posColor[];
};

layout(std140, binding = 1) buffer ChunkIndex {
    int offset[];
};

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;

void main() {
    PosColor pc = posColor[ offset[gl_DrawID] + gl_InstanceID];
//    PosColor pc = posColor[gl_InstanceID];
    vec3 worldPos = Position + pc.pos_ssbo;
    gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);

    uint c = uint(pc.color_ssbo);
    float a = float((c >> 24u) & 255u) / 255.0;
    float r = float((c >> 16u) & 255u) / 255.0;
    float g = float((c >> 8u) & 255u) / 255.0;
    float b = float(c & 255u) / 255.0;
    vertexColor = Color * vec4(r, g, b, a);
}
