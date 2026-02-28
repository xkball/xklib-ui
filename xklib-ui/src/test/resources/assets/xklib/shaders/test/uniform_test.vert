#version 330 core

layout (location = 0) in vec3 aPos;

uniform mat4 uModelViewProjection;
uniform vec3 uOffset;

void main() {
    gl_Position = uModelViewProjection * vec4(aPos + uOffset, 1.0);
}
