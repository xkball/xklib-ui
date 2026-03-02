#version 330 core

in vec2 vTexCoord;
in vec4 vColor;
out vec4 FragColor;

uniform sampler2D uTexture;

void main() {
    float alpha = texture(uTexture, vTexCoord).r;
    if(alpha <= 0.1) discard;
    FragColor = vec4(vColor.rgb, vColor.a * alpha * 2);

}
