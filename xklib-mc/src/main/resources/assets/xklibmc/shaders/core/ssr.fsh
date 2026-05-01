#version 330 core

uniform sampler2D input0; //detph
uniform sampler2D input1; //color

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

vec3 getViewPos(vec2 uv) {
    float depth = texture(input0, uv).r * 2 - 1;
    vec4 ndc = vec4(uv * 2.0 - 1.0, depth, 1.0);
    vec4 vp = invProjMat * ndc;
    return vp.xyz / vp.w;
}


//vec3 getViewNormal(vec3 pos, vec2 uv) {
//    vec2 offset = 1.0 / screenSize;
//
//    vec3 p1 = getViewPos(uv + vec2( offset.x, 0.0)) - pos;
//    vec3 p2 = getViewPos(uv + vec2( 0.0,  offset.y)) - pos;
//    vec3 p3 = getViewPos(uv + vec2(-offset.x, 0.0)) - pos;
//    //vec3 p4 = getViewPos(uv + vec2( 0.0, -offset.y)) - pos;
//
//    vec3 v1 = p1-p2;
//    vec3 v2 = p3-p2;
//
//    return -normalize(cross(v1, v2));
//}

vec3 getNormal(vec3 pos){
    vec3 dx = dFdx(pos);
    vec3 dy = dFdy(pos);
    return normalize(cross(dx, dy));
}

bool projectToUv(vec3 p, out vec2 uv, out float ndcZ) {
    vec4 clip = ProjMat_ * vec4(p, 1.0);
    if (clip.w <= 1e-6) {
        return false;
    }
    vec3 ndc = clip.xyz / clip.w;
    uv = ndc.xy * 0.5 + 0.5;
    ndcZ = ndc.z;
    return uv.x >= 0.0 && uv.x <= 1.0 && uv.y >= 0.0 && uv.y <= 1.0;
}

bool refineHit(vec3 origin, vec3 dir, float tMin, float tMax, out vec2 hitUv) {
    float lo = tMin;
    float hi = tMax;
    vec2 bestUv = vec2(0.0);
    float bestErr = 1e20;
    for (int i = 0; i < 20; i++) {
        float mid = 0.5 * (lo + hi);
        vec2 uv;
        float rayNdcZ;
        if (!projectToUv(origin + dir * mid, uv, rayNdcZ)) {
            return false;
        }
        else {
            float sceneNdcZ = texture(input0, uv).r * 2.0 - 1.0;
            float dz = rayNdcZ - sceneNdcZ;
            float err = abs(dz);
            if (err < bestErr) {
                bestErr = err;
                bestUv = uv;
            }
            if (dz > 0.0) {
                hi = mid;
            } else {
                lo = mid;
            }
        }

    }
    hitUv = bestUv;
    return bestErr < 0.03;
}

bool traceSSR(vec3 origin, vec3 dir, out vec2 hitUv) {
    return refineHit(origin,dir,0,8192,hitUv);
}

void main() {
    vec4 color = texture(input1, texCoord);
    if(color.a > 1.0 - 1e-6){
        fragColor = vec4(color.rgb,1.0);
        return;
    }

    vec3 worldPos = getViewPos(texCoord);
    vec3 worldN = getNormal(worldPos);
    vec3 viewDir = normalize(worldPos + camPos.xyz);
    vec3 reflDir = normalize(reflect(viewDir, worldN));

    vec2 hitUv;
    vec3 ssrColor = vec3(0.0);
    float hitMask = 0.0;
    if (traceSSR(worldPos + worldN * 0.05, reflDir, hitUv)) {
        ssrColor = texture(input1, hitUv).rgb;
        float ssrDepth = texture(input0, hitUv).r;
        if(ssrDepth > 1.0 - 1e-6){
            fragColor = vec4(color.rgb,0.8);
            return;
        }
        hitMask = 1.0;
    }

    float nv = clamp(dot(worldN, normalize(-camPos.xyz - worldPos)), 0.0, 1.0);
    float fresnel = pow(1.0 - nv, 5.0);
    float reflectivity = clamp((1.0 - color.a) * (0.2 + fresnel), 0.0, 1.0) * hitMask;
    vec3 outRgb = mix(color.rgb, ssrColor, reflectivity);
    fragColor = vec4(outRgb, 0.8);
}