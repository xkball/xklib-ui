#version 330 core

uniform sampler2D theDepth;

in vec2 texCoord;
out vec4 fragColor;

layout(std140) uniform ScreenSize{
    vec2 screenSize;
};

layout(std140) uniform Projection {
    mat4 ProjMat;
};

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};


layout(std140) uniform Samples {
    vec3 sample_[64];
};

layout(std140) uniform Rotate {
    mat4 trans[16];
};


void main(){

}