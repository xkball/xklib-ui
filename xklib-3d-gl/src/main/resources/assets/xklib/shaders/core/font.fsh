#version 330 core

const float EdgeThreshold = 0.5f;
in vec2 vTexCoord;
in vec4 vColor;
out vec4 FragColor;

uniform sampler2D uTexture;

void main() {
    float distance = texture(uTexture, vTexCoord).r;
    float afwidth = fwidth(distance) * 0.5;
    float alpha = smoothstep(EdgeThreshold - afwidth, EdgeThreshold + afwidth, distance);
    if(alpha <= 0.01) discard;
    FragColor = vec4(vColor.rgb, vColor.a * alpha);

}
