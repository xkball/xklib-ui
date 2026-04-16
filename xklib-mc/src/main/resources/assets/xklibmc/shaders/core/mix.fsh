#version 330 core

uniform sampler2D input0;
uniform sampler2D input1;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float p1 = texture(input0, texCoord).r;
    vec4 p2 = texture(input1, texCoord);
    fragColor = vec4(p2.rgb * p1, p2.a);
}