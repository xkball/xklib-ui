#version 330 core

uniform sampler2D input0;

in vec2 texCoord;
out vec4 fragColor;

layout(std140) uniform ScreenSize {
    vec2 screenSize;
};

layout(std140) uniform InvProjMat {
    mat4 invProjMat;
    mat4 ProjMat_;
    vec4 camDir;
    vec4 camPos;
};

layout(std140) uniform SSAOData {
    vec4 sample_[64];
    mat4 trans[16];
};

const int   KERNEL_SIZE = 64;
const float RADIUS      = 0.2;
const float BIAS        = 0.02;
const float POWER       = 1;


vec3 getViewPos(vec2 uv) {
    float depth = texture(input0, uv).r * 2 - 1;
    vec4 ndc = vec4(uv * 2.0 - 1.0, depth, 1.0);
    vec4 vp = invProjMat * ndc;
    return vp.xyz / vp.w;
}


vec3 getViewNormal(vec3 pos, vec2 uv) {
    vec2 offset = 1.0 / screenSize;

    vec3 p1 = getViewPos(uv + vec2( offset.x, 0.0)) - pos;
    vec3 p2 = getViewPos(uv + vec2( 0.0,  offset.y)) - pos;
    vec3 p3 = getViewPos(uv + vec2(-offset.x, 0.0)) - pos;
    //vec3 p4 = getViewPos(uv + vec2( 0.0, -offset.y)) - pos;

    vec3 v1 = p2-p1;
    vec3 v2 = p2-p3;

    return normalize(cross(v1, v2));
}

float linearDepth(vec3 viewPos) {
    vec3 pos = viewPos + camPos.xyz;
    float theta = dot(normalize(pos),normalize(camDir.xyz));
    float l = length(pos);
    return theta * l;
}

void main() {
    float depth = texture(input0, texCoord).r;

    if (depth >= 1.0 - 1e-6) {
        fragColor = vec4(0,0,0,1.0);
        return;
    }

    vec3 viewPos = getViewPos(texCoord);
    float d0 = linearDepth(viewPos);
    vec3 normal  = getViewNormal(viewPos, texCoord);

    ivec2 scrCoord = ivec2(gl_FragCoord.xy);
    int   rotIdx   = (scrCoord.x % 4) + (scrCoord.y % 4) * 4;
    mat4  rotMat   = trans[rotIdx];

    float occlusion = 0.0;

    for (int i = 0; i < KERNEL_SIZE; ++i) {
        vec3 sampleDir = sample_[i].xyz;
        vec3 rotated = (rotMat * vec4(sampleDir, 0.0)).xyz;
        rotated = normalize(rotated);

        if (dot(rotated, normal) > 0.0)
        rotated = -rotated;

        vec3 samplePos = viewPos + rotated * RADIUS;

        vec4 proj = ProjMat_ * vec4(samplePos, 1.0);
        proj.xyz /= proj.w;
        vec2 sampleUV = proj.xy * 0.5 + 0.5;

        if (sampleUV.x < 0.0 || sampleUV.x > 1.0 ||
        sampleUV.y < 0.0 || sampleUV.y > 1.0) {
            continue;
        }

        vec3 sampleViewPos = getViewPos(sampleUV);
        float sampleDepth = linearDepth(sampleViewPos);

        float rangeCheck = smoothstep(0.0, 1.0, RADIUS / abs(d0 - sampleDepth));

        occlusion += (sampleDepth > d0 + BIAS ? 1 : 0) * rangeCheck;
    }

    occlusion = 1 - (occlusion / float(KERNEL_SIZE));
    //occlusion = pow(occlusion, POWER);
    //if(occlusion > 0.7) occlusion = 1;
    fragColor = vec4(occlusion, occlusion, occlusion, 1.0);
}