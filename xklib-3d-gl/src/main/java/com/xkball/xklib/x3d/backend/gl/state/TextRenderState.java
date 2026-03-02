package com.xkball.xklib.x3d.backend.gl.state;

import com.ibm.icu.text.BreakIterator;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.backend.gl.font.Font;
import com.xkball.xklib.x3d.backend.gl.font.FontAtlas;
import com.xkball.xklib.x3d.backend.gl.font.GlyphInfo;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;

public record TextRenderState(
    IRenderPipeline pipeline,
    TextureSetup textureSetup,
    Matrix3x2fc pose,
    Font font,
    String text,
    int x,
    int y,
    int color,
    boolean drawShadow,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements IGuiElementRenderState {
    
    public TextRenderState(
        IRenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2fc pose,
        Font font,
        String text,
        int x,
        int y,
        int color,
        boolean drawShadow,
        @Nullable ScreenRectangle scissorArea
    ) {
        this(pipeline, textureSetup, pose, font, text, x, y, color, drawShadow, scissorArea,
             getBounds(font, text, x, y, pose, scissorArea));
    }

    @Override
    public void buildVertices(BufferBuilder vertexConsumer, float zOffset) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        if (drawShadow) {
            int shadowColor = mulColor(color, 0.25f);
            buildTextVertices(vertexConsumer, zOffset-0.01f, x + 1, y + 1, shadowColor);
        }
        
        buildTextVertices(vertexConsumer, zOffset, x, y, color);
    }
    
    private void buildTextVertices(BufferBuilder vertexConsumer, float zOffset, float startX, float startY, int textColor) {
        FontAtlas atlas = font.getAtlas();
        
        float currentX = startX;
        startY += font.getFontSize() - 2;
        int prevCodepoint = -1;
        
        BreakIterator iterator = BreakIterator.getCharacterInstance();
        iterator.setText(text);
        
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String grapheme = text.substring(start, end);
            int codepoint = grapheme.codePointAt(0);
            
            if (prevCodepoint >= 0) {
                currentX += atlas.getKerning(prevCodepoint, codepoint);
            }
            
            GlyphInfo glyph = atlas.getGlyph(codepoint);
            
            if (glyph.width() > 0 && glyph.height() > 0) {
                float glyphX0 = currentX + glyph.bearingX();
                float glyphY0 = startY + glyph.bearingY();
                float glyphX1 = glyphX0 + glyph.width();
                float glyphY1 = glyphY0 + glyph.height();
                
                float u0 = glyph.u0();
                float v0 = glyph.v0();
                float u1 = glyph.u1();
                float v1 = glyph.v1();
                
                vertexConsumer.addVertexWith2DPose(pose, glyphX0, glyphY0, zOffset).setUv(u0, v0).setColor(textColor);
                vertexConsumer.addVertexWith2DPose(pose, glyphX1, glyphY0, zOffset).setUv(u1, v0).setColor(textColor);
                vertexConsumer.addVertexWith2DPose(pose, glyphX1, glyphY1, zOffset).setUv(u1, v1).setColor(textColor);
                
                vertexConsumer.addVertexWith2DPose(pose, glyphX0, glyphY0, zOffset).setUv(u0, v0).setColor(textColor);
                vertexConsumer.addVertexWith2DPose(pose, glyphX1, glyphY1, zOffset).setUv(u1, v1).setColor(textColor);
                vertexConsumer.addVertexWith2DPose(pose, glyphX0, glyphY1, zOffset).setUv(u0, v1).setColor(textColor);
            }
            
            currentX += glyph.advance();
            prevCodepoint = codepoint;
        }
    }
    
    private static int mulColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int)(((color >> 16) & 0xFF) * factor);
        int g = (int)(((color >> 8) & 0xFF) * factor);
        int b = (int)((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    private static @Nullable ScreenRectangle getBounds(Font font, String text, int x, int y, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        int width = font.width(text);
        int height = font.getFontSize();
        ScreenRectangle bounds = new ScreenRectangle(x, y, width, height).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}
