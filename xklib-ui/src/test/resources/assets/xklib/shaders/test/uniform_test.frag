#version 330 core

uniform vec4 uColor;
uniform float uAlpha;

out vec4 FragColor;

void main() {
    FragColor = vec4(uColor.rgb, uColor.a * uAlpha);
}
