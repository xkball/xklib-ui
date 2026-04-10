#version 430

layout(std140) uniform ScreenSize{
    vec2 screenSize;
};

layout(std140) uniform Projection {
    mat4 ProjMat;
};

in vec3 Position;
in vec4 Color;
in vec3 Normal;


out vec4 vColor;

void main() {
    gl_Position = ProjMat * vec4(Position, 1.0);
    gl_Position.x += (2.0/screenSize.x) * gl_Position.w * Normal.z * Normal.x * sign(ProjMat[0][0]);
    gl_Position.y += (2.0/screenSize.y) * gl_Position.w * Normal.z * Normal.y * sign(ProjMat[1][1]);
    vColor = Color;
}