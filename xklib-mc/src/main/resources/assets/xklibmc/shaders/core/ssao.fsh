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
};

layout(std140) uniform SSAOData {
    vec4 sample_[64];
    mat4 trans[16];
};

vec3 toNDC(float depth){
    vec2 ndcXY = texCoord * 2.0 - 1.0;
    float ndcZ0 = depth * 2.0 - 1.0;
    return vec3(ndcXY, ndcZ0);
}

void main(){

    float depth0 = texture(input0, texCoord).r;
    if (depth0 >= 1.0 - 1e-6) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }

    vec4 ndc = vec4(toNDC(depth0),1.0);

    vec4 viewH0 = invProjMat * ndc;
    vec3 viewPos = viewH0.xyz / viewH0.w;
    fragColor = vec4(vec3(viewPos.x),1.0);

    ivec2 pix = ivec2(gl_FragCoord.xy);
    int idx = (pix.x & 3) + ((pix.y & 3) << 2);
    float occ = 0;
    float wsum = 0;
    float baiz = 0.1;
//        float ndcZs = sd * 2.0 - 1.0;
//        vec4 viewHs = invProjMat * vec4(uv * 2.0 - 1.0, ndcZs, 1.0);
//        float viewZs = (viewHs.z / viewHs.w);
//        float dz = abs(viewPos.z - viewZs);
//        float rangeW = 1.0 - smoothstep(0.0, 1.0, dz);
//        wsum += rangeW;
//        float blocked = step(viewZs, samplePos.z - bias);
//        occ += blocked * rangeW;
////    ao = pow(clamp(ao, 0.0, 1.0), 1.6);
    for (int i = 0; i < 64; i++) {
        vec3 dir = (trans[idx] * sample_[i]).xyz;
        vec3 samplePos = viewPos + dir * 0.6 + baiz;
        vec4 clip = ProjMat_ * vec4(samplePos, 1.0);
        if (clip.w <= 0.000001) {
            continue;
        }
        vec3 ndc_ = clip.xyz / clip.w;
        vec2 uv = ndc_.xy * 0.5 + 0.5;
        if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
            continue;
        }

        float depth1 = texture(input0, uv).r;
        if (depth1 >= 1.0) {
            continue;
        }

        float dz = abs(depth0 - depth1);
        float rangeW = 1.0 - smoothstep(0.0, 1.0, dz);

        wsum += rangeW;
        occ += (dz > 0 ? 0 : 1) * rangeW;
    }

    float ao = 1.0;
    if (wsum > 0.0) {
        ao = clamp(1 - (occ / wsum) ,0.0,1.0);
    }
    fragColor = vec4(ao, ao, ao, 1.0);
}