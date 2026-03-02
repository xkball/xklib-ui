#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec4 aColor;

out vec2 vTexCoord;
out vec4 vColor;

uniform mat4 projMatrix;

void main() {
    gl_Position = projMatrix * vec4(aPos, 1.0);
    vTexCoord = aTexCoord;
    vColor = aColor;
}
