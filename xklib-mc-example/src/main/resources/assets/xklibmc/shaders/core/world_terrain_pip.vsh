#version 460 core
//#extension  GL_ARB_shader_draw_parameters : require
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

struct PosColor{
    vec3 pos_ssbo;
    int color_ssbo;
};

layout(std140, binding = 0) buffer ABlock {
    PosColor posColor[];
};

in vec3 Position;
in vec4 Color;
in vec3 Normal;

out vec4 vertexColor;
out vec3 worldPos;
out vec3 pNormal;

void main() {
//   PosColor pc = posColor[ cmd[gl_DrawID].offset ];
    PosColor pc = posColor[gl_BaseInstance + gl_InstanceID];
    vec3 worldPos = Position + pc.pos_ssbo;
    gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);

    uint c = uint(pc.color_ssbo);
    float a = float((c >> 24u) & 255u) / 255.0;
    float r = float((c >> 16u) & 255u) / 255.0;
    float g = float((c >> 8u) & 255u) / 255.0;
    float b = float(c & 255u) / 255.0;
    vertexColor = Color * vec4(r, g, b, a);
    worldPos = pc.pos_ssbo;
    pNormal = Normal;
}
