package com.xkball.xklib.x3d.backend.gl.font;

import com.ibm.icu.text.BreakIterator;
import com.xkball.xklib.XKLib;
import com.xkball.xklib.x3d.backend.gl.pipeline.RenderPipeline;
import com.xkball.xklib.x3d.backend.gl.pipeline.RenderPipelines;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import com.xkball.xklib.x3d.backend.vertex.VertexFormats;
import org.joml.Matrix4f;

@Deprecated
public class FontRenderer {
    
    private final RenderPipeline pipeline;
    private final Matrix4f projectionMatrix;
    
    public FontRenderer() {
        this.pipeline = RenderPipelines.FONT.get();
        this.projectionMatrix = new Matrix4f();
    }
    
    public void drawString(Font font, String text, float x, float y, int color) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        var window = XKLib.RENDER_CONTEXT.get().getWindow();
        projectionMatrix.setOrtho(0.0F, window.getWidth(), window.getHeight(), 0.0F,-10, 1000,true);
        
        FontAtlas atlas = font.getAtlas();
        
        BufferBuilder builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_TEX_COLOR);
        
        float currentX = x;
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
                float x0 = currentX + glyph.bearingX();
                float y0 = y + glyph.bearingY();
                float x1 = x0 + glyph.width();
                float y1 = y0 + glyph.height();
                
                float u0 = glyph.u0();
                float v0 = glyph.v0();
                float u1 = glyph.u1();
                float v1 = glyph.v1();
                
                builder.addVertex(x0, y0, 0).setUv(u0, v0).setColor(color);
                builder.addVertex(x1, y0, 0).setUv(u1, v0).setColor(color);
                builder.addVertex(x1, y1, 0).setUv(u1, v1).setColor(color);
                
                builder.addVertex(x0, y0, 0).setUv(u0, v0).setColor(color);
                builder.addVertex(x1, y1, 0).setUv(u1, v1).setColor(color);
                builder.addVertex(x0, y1, 0).setUv(u0, v1).setColor(color);
            }
            
            currentX += glyph.advance();
            prevCodepoint = codepoint;
        }
        
        pipeline.bindSampler(0, () -> atlas);
        
        pipeline.getShader().getUniform("uModelViewProjection").set(projectionMatrix);
        pipeline.draw(builder);
    }
    
    public void destroy() {
        pipeline.destroy();
    }
}
