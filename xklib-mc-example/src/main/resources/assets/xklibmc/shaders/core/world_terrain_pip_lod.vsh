#version 460 core
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

uniform sampler2D colorTexture;
uniform sampler2D heightTexture;

const float textureSize_ = 16384;
const float textureUnit_ = 1 / textureSize_;
in vec3 inPos;

out vec4 vertexColor;
out vec3 worldPos;
out vec3 pNormal;

float getHeight(vec2 uv){
    vec4 data = texture(heightTexture, uv);
    int result = 0;
    result |= int(data.r * 255);
    result |= int(data.g * 255) >> 8;
    result |= int(data.b * 255) >> 16;
    result |= int(data.a * 255) >> 24;
    return float(result);
}

void main() {
    vec2 offset = unpackHalf2x16(gl_BaseInstance);
    int px = ((gl_BaseInstance >> 24) & 0xff);
    int pz =  (gl_BaseInstance        & 0xff);
    worldPos = inPos + vec3(px * 512, 0, pz * 512);
    vec2 uv = worldPos.xz / textureSize_;
    float h00 = getHeight(uv);
    float h10 = getHeight(uv + vec2(textureUnit_, 0));
    float h01 = getHeight(uv + vec2(0, textureUnit_));
    pNormal = -normalize(cross(vec3(1,h10-h00,0),vec3(0,h01-h00,1)));
    worldPos.y = h00;
    vertexColor = texture(colorTexture, uv);
    gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);
}
