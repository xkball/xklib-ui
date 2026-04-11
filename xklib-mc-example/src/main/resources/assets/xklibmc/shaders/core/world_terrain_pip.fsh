#version 460 core
#moj_import <minecraft:dynamictransforms.glsl>

layout(early_fragment_tests) in;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}
