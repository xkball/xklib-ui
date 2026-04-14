#version 150

uniform sampler2D input0;

layout(std140) uniform ScreenSize{
    vec2 screenSize;
};

in vec2 texCoord;
out vec4 fragColor;

float gaussianPdf(in float x, in float sigma) {
    return 0.39894 * exp(-0.5 * x * x/(sigma * sigma))/sigma;
}
const float Radius = 1.0;
float weight0 = gaussianPdf(0.0,Radius);
float weight1 = gaussianPdf(1.0,Radius);
float weightSum = weight0 + weight1 * 8.0;
//float weight2 = gaussianPdf(2.0);

void main(){
    vec2 invSize = 1.0 / screenSize;
    vec3 diffuseSum = texture(input0, texCoord).rgb * weight0;
    diffuseSum += texture(input0, texCoord + vec2( 1.0, 0.0) * invSize).rgb * weight1;
    diffuseSum += texture(input0, texCoord + vec2( 1.0, 1.0) * invSize).rgb * weight1;
    diffuseSum += texture(input0, texCoord + vec2( 0.0, 1.0) * invSize).rgb * weight1;
    diffuseSum += texture(input0, texCoord + vec2(-1.0, 1.0) * invSize).rgb * weight1;
    diffuseSum += texture(input0, texCoord + vec2(-1.0, 0.0) * invSize).rgb * weight1;
    diffuseSum += texture(input0, texCoord + vec2(-1.0,-1.0) * invSize).rgb * weight1;
    diffuseSum += texture(input0, texCoord + vec2( 0.0,-1.0) * invSize).rgb * weight1;
    diffuseSum += texture(input0, texCoord + vec2( 1.0,-1.0) * invSize).rgb * weight1;
    fragColor = vec4(diffuseSum/weightSum, 1.0);
}