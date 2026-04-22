#version 460 core
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

struct ChunkData {
    int x0;
    int z0;
    int heightMap[289];
    int colorMap[289];
};

layout(std430, binding = 0) buffer Chunks {
    ChunkData chunkData[];
};

in vec3 indexs;

out vec4 vertexColor;
out vec3 worldPos;
out vec3 pNormal;

void main() {
    int chunkId = gl_BaseInstance;
    ChunkData chunk = chunkData[chunkId];
    int index = int(indexs.x);
    int index1 = int(indexs.y);
    int index2 = int(indexs.z);
    float h = chunk.heightMap[index];
    vec3 worldPos0 = vec3(chunk.x0 + (index % 17), h, chunk.z0 + index / 17);
    vec3 worldPos1 = vec3(chunk.x0 + (index1 % 17), chunk.heightMap[index1], chunk.z0 + index1 / 17);
    vec3 worldPos2 = vec3(chunk.x0 + (index2 % 17), chunk.heightMap[index2], chunk.z0 + index2 / 17);
    worldPos = worldPos0;
    pNormal = normalize(cross(worldPos1 - worldPos0,worldPos2 - worldPos0));
    uint c = uint(chunk.colorMap[index]);
    float a = float((c >> 24u) & 255u) / 255.0;
    float r = float((c >> 16u) & 255u) / 255.0;
    float g = float((c >> 8u) & 255u) / 255.0;
    float b = float(c & 255u) / 255.0;
    vertexColor = vec4(r, g, b, a);
    gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);
}
