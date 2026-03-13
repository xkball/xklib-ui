package com.xkball.xklib.x3d.backend.gl.font;

public record GlyphInfo(
    int codepoint,
    float width,
    float height,
    float bearingX,
    float bearingY,
    float advance,
    float u0,
    float v0,
    float u1,
    float v1
) {
}
