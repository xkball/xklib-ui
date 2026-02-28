package com.xkball.xklib.ui.backend.gl.font;

public record GlyphInfo(
    int codepoint,
    int x,
    int y,
    int width,
    int height,
    int bearingX,
    int bearingY,
    int advance,
    float u0,
    float v0,
    float u1,
    float v1
) {
}
