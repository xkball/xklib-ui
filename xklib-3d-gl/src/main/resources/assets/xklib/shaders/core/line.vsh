#version 430

uniform vec2 screenSize;
uniform mat4 projMatrix;

in vec3 Position;
in vec2 uv;
in vec4 Color;

out vec4 vColor;

void main() {
    gl_Position = projMatrix * vec4(Position, 1.0);
    vColor = Color;
    gl_Position.x += (4.0/screenSize.x) * gl_Position.w * uv.x;
    gl_Position.y += (4.0/screenSize.y) * gl_Position.w * uv.x;
}