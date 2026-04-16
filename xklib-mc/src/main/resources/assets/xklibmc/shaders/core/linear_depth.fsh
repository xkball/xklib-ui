#version 330 core

uniform sampler2D input0;

in vec2 texCoord;
out vec4 fragColor;

layout(std140) uniform InvProjMat {
    mat4 invProjMat;
    mat4 ProjMat_;
    vec4 camDir;
    vec4 camPos;
};


vec3 getViewPos(vec2 uv) {
    float depth = texture(input0, uv).r *2-1;
    vec4 ndc = vec4(uv * 2.0 - 1.0, depth, 1.0);
    vec4 vp = invProjMat * ndc;
    return vp.xyz / vp.w;
}

float linearDepth(vec2 uv){
    vec3 viewPos = getViewPos(texCoord);
    vec3 pos = viewPos + camPos.xyz;
    float theta = dot(normalize(pos),normalize(camDir.xyz));
    float l = length(pos);
    return cos(theta) * l;
}

void main() {
    float depth = texture(input0, texCoord).r;

    if (depth >= 1.0 - 1e-6) {
        fragColor = vec4(0,0,0,1.0);
        return;
    }

    vec3 viewPos = getViewPos(texCoord);
    vec3 pos = viewPos + camPos.xyz;
    float theta = dot(normalize(pos),normalize(camDir.xyz));
    float l = length(pos);
    float d = theta * l / 1000;
    //fragColor = vec4(theta, theta, theta, 1.0);
    fragColor = vec4(d,d,d, 1.0);
}