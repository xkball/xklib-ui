#version 330 core

in vec2 vTexCoord;
in vec4 vColor;
in float radius;
in vec2 rectSize;
out vec4 FragColor;

float roundedBoxSDF(vec2 centerPos, vec2 halfSize, float r) {
    vec2 d = abs(centerPos) - halfSize + r;
    return length(max(d, 0.0)) + min(max(d.x, d.y), 0.0) - r;
}

void main() {
    vec2 pixelPos = vTexCoord;
    vec2 halfSize = rectSize * 0.5;
    vec2 centerPos = pixelPos - halfSize;

    float dist = roundedBoxSDF(centerPos, halfSize, radius);

    float smoothWidth = 1.0;
    float alpha = 1.0 - smoothstep(-smoothWidth, smoothWidth, dist);

    if (alpha <= 0.0) {
        discard;
    }

    FragColor = vec4(vColor.rgb, vColor.a * alpha);
}
