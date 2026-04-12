#version 330 core

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec2 uv2;
layout (location = 3) in vec4 aColor;
layout (location = 4) in float aRadius;

out vec2 vTexCoord;
out vec4 vColor;
out float radius;
out vec2 rectSize;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(aPos, 1.0);
    vTexCoord = aTexCoord;
    vColor = aColor;
    radius = aRadius;
    rectSize = uv2;
}
