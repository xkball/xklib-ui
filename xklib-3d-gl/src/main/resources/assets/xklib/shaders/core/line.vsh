#version 430

uniform vec2 screenSize;
uniform mat4 projMatrix;

in vec3 Position;
in vec3 Normal;
in vec4 Color;

out vec4 vColor;

void main() {
//    vec4 pos = vec4(Position,1.0);
//    pos.x += 1.0 * Normal.z * Normal.x;
//    pos.y += 1.0 * Normal.z * Normal.y;
//    gl_Position = projMatrix * pos;
//    vColor = Color;
    gl_Position = projMatrix * vec4(Position, 1.0);
    vColor = Color;
    gl_Position.x += (2.0/screenSize.x) * gl_Position.w * Normal.z * Normal.x * sign(projMatrix[0][0]);
    gl_Position.y += (2.0/screenSize.y) * gl_Position.w * Normal.z * Normal.y * sign(projMatrix[1][1]);
}