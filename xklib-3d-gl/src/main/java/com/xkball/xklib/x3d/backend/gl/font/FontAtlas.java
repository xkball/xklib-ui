package com.xkball.xklib.x3d.backend.gl.font;

import com.xkball.xklib.x3d.backend.gl.texture.AbstractTexture;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL45.*;

public class FontAtlas extends AbstractTexture {
    
    private final Map<Integer, GlyphInfo> glyphs = new HashMap<>();
    private final STBTTFontinfo fontInfo;
    private final ByteBuffer fontDataBuffer;
    private final float scale;
    private final int ascent;
    private final int descent;
    private final int lineGap;
    
    private int currentX = 0;
    private int currentY = 0;
    private int maxRowHeight = 0;
    private float overSampleScale;
    
    public FontAtlas(byte[] fontData, int fontSize, float overSampleScale) {
        this.fontInfo = STBTTFontinfo.create();
        this.overSampleScale = overSampleScale;
        fontDataBuffer = MemoryUtil.memAlloc(fontData.length);
        fontDataBuffer.put(fontData).flip();
        
        int offset = STBTruetype.stbtt_GetFontOffsetForIndex(fontDataBuffer, 0);
        if (offset < 0) {
            MemoryUtil.memFree(fontDataBuffer);
            throw new RuntimeException("Failed to get font offset - invalid font file");
        }
        
        if (!STBTruetype.stbtt_InitFont(fontInfo, fontDataBuffer, offset)) {
            MemoryUtil.memFree(fontDataBuffer);
            throw new RuntimeException("Failed to initialize font");
        }
        
        this.scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, fontSize * overSampleScale);
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer ascentBuf = stack.mallocInt(1);
            IntBuffer descentBuf = stack.mallocInt(1);
            IntBuffer lineGapBuf = stack.mallocInt(1);
            
            STBTruetype.stbtt_GetFontVMetrics(fontInfo, ascentBuf, descentBuf, lineGapBuf);
            
            this.ascent = ascentBuf.get(0);
            this.descent = descentBuf.get(0);
            this.lineGap = lineGapBuf.get(0);
        }
        
        int atlasSize = 1024;
        this.width = atlasSize;
        this.height = atlasSize;
        
        id = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(id, 1, GL_R8, atlasSize, atlasSize);
        glTextureParameteri(id, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTextureParameteri(id, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTextureParameteri(id, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTextureParameteri(id, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        ByteBuffer clearData = MemoryUtil.memAlloc(atlasSize * atlasSize);
        MemoryUtil.memSet(clearData, 0);
        glTextureSubImage2D(id, 0, 0, 0, atlasSize, atlasSize, GL_RED, GL_UNSIGNED_BYTE, clearData);
        MemoryUtil.memFree(clearData);
    }
    
    public GlyphInfo getGlyph(int codepoint) {
        return glyphs.computeIfAbsent(codepoint, this::loadGlyph);
    }
    
    private GlyphInfo loadGlyph(int codepoint) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer advanceBuf = stack.mallocInt(1);
            IntBuffer lsbBuf = stack.mallocInt(1);
            STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, codepoint, advanceBuf, lsbBuf);
            
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer xOffBuf = stack.mallocInt(1);
            IntBuffer yOffBuf = stack.mallocInt(1);
            
            ByteBuffer bitmap = STBTruetype.stbtt_GetCodepointBitmap(
                fontInfo, scale, scale, codepoint, widthBuf, heightBuf, xOffBuf, yOffBuf
            );
            
            if (bitmap == null) {
                int advance = (int)(advanceBuf.get(0) * scale);
                return new GlyphInfo(codepoint, 0, 0, 0, 0, advance, 0, 0, 0, 0);
            }
            
            int glyphWidth = widthBuf.get(0);
            int glyphHeight = heightBuf.get(0);
            int xOff = xOffBuf.get(0);
            int yOff = yOffBuf.get(0);
            
            if (glyphWidth <= 0 || glyphHeight <= 0) {
                STBTruetype.stbtt_FreeBitmap(bitmap);
                int advance = (int)(advanceBuf.get(0) * scale);
                return new GlyphInfo(codepoint, 0, 0, 0, 0, advance, 0, 0, 0, 0);
            }
            
            int padding = 2;
            
            if (currentX + glyphWidth + padding > width) {
                currentX = 0;
                currentY += maxRowHeight + padding;
                maxRowHeight = 0;
            }
            
            if (currentY + glyphHeight + padding > height) {
                STBTruetype.stbtt_FreeBitmap(bitmap);
                throw new RuntimeException("Font atlas is full");
            }
            
            int oldAlignment = glGetInteger(GL_UNPACK_ALIGNMENT);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTextureSubImage2D(id, 0, currentX, currentY, glyphWidth, glyphHeight, GL_RED, GL_UNSIGNED_BYTE, bitmap);
            glPixelStorei(GL_UNPACK_ALIGNMENT, oldAlignment);
            
            STBTruetype.stbtt_FreeBitmap(bitmap);
            
            float u0 = (float)currentX / width;
            float v0 = (float)currentY / height;
            float u1 = (float)(currentX + glyphWidth) / width;
            float v1 = (float)(currentY + glyphHeight) / height;
            
            float advance = advanceBuf.get(0) * scale;
            
            GlyphInfo glyphInfo = new GlyphInfo(
                codepoint,
                glyphWidth / this.overSampleScale,
                glyphHeight / this.overSampleScale,
                xOff / this.overSampleScale,
                yOff / this.overSampleScale,
                advance / this.overSampleScale,
                u0,
                v0,
                u1,
                v1
            );
            
            currentX += glyphWidth + padding;
            maxRowHeight = Math.max(maxRowHeight, glyphHeight);
            
            return glyphInfo;
        }
    }
    
    public int getAscent() {
        return (int)(ascent * scale);
    }
    
    public int getDescent() {
        return (int)(descent * scale);
    }
    
    public int getLineHeight() {
        return (int)((ascent - descent + lineGap) * scale);
    }
    
    public int getKerning(int codepoint1, int codepoint2) {
        int kern = STBTruetype.stbtt_GetCodepointKernAdvance(fontInfo, codepoint1, codepoint2);
        return (int)(kern * scale);
    }
    
    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        glDeleteTextures(id);
        id = 0;
        if (fontDataBuffer != null) {
            MemoryUtil.memFree(fontDataBuffer);
        }
    }
}
