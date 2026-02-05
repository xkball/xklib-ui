package com.xkball.xklib.ui.backend.gl.font;

import com.ibm.icu.text.BreakIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Font {
    
    private final FontAtlas atlas;
    private final int fontSize;
    
    public Font(int fontSize) {
        this.fontSize = fontSize;
        var fontData = loadSystemFont();
        this.atlas = new FontAtlas(fontData, fontSize);
    }
    
    public Font() {
        this(32);
    }
    
    private byte[] loadSystemFont() {
        String os = System.getProperty("os.name").toLowerCase();
        Path fontPath;
        
        if (os.contains("win")) {
            fontPath = Paths.get(System.getenv("WINDIR"), "Fonts", "宋体.ttc");
            if (!Files.exists(fontPath)) {
                fontPath = Paths.get(System.getenv("WINDIR"), "Fonts", "simhei.ttf");
            }
            if (!Files.exists(fontPath)) {
                fontPath = Paths.get(System.getenv("WINDIR"), "Fonts", "msyh.ttc");
            }
            if (!Files.exists(fontPath)) {
                fontPath = Paths.get(System.getenv("WINDIR"), "Fonts", "simsun.ttc");
            }
        } else if (os.contains("mac")) {
            fontPath = Paths.get("/System/Library/Fonts/Helvetica.ttc");
            if (!Files.exists(fontPath)) {
                fontPath = Paths.get("/System/Library/Fonts/PingFang.ttc");
            }
        } else {
            fontPath = Paths.get("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf");
            if (!Files.exists(fontPath)) {
                fontPath = Paths.get("/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf");
            }
        }
        
        if (!Files.exists(fontPath)) {
            throw new RuntimeException("Could not find system font");
        }
        
        try {
            return Files.readAllBytes(fontPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font: " + fontPath, e);
        }
    }
    
    public int width(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        int width = 0;
        int prevCodepoint = -1;
        
        BreakIterator iterator = BreakIterator.getCharacterInstance();
        iterator.setText(text);
        
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String grapheme = text.substring(start, end);
            int codepoint = grapheme.codePointAt(0);
            
            if (prevCodepoint >= 0) {
                width += atlas.getKerning(prevCodepoint, codepoint);
            }
            
            GlyphInfo glyph = atlas.getGlyph(codepoint);
            width += glyph.advance();
            
            prevCodepoint = codepoint;
        }
        
        return width;
    }
    
    public FontAtlas getAtlas() {
        return atlas;
    }
    
    public int getFontSize() {
        return fontSize;
    }
    
    public void destroy() {
        atlas.destroy();
    }
}
