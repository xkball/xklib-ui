package com.xkball.xklib.x3d.backend.gl.font;

import com.ibm.icu.text.BreakIterator;
import com.xkball.xklib.ui.render.ComponentStyle;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IFont;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Font implements IFont {

    private final List<FontAtlas> atlases = new ArrayList<>();
    private final Map<Integer, GlyphInfo> glyphCache = new HashMap<>();
    private final byte[] fontData;
    private final int fontSize;
    private static final float OVER_SAMPLE_SCALE = 2f;

    public Font(int fontSize) {
        this.fontSize = fontSize;
        this.fontData = loadSystemFont();
        atlases.add(new FontAtlas(fontData, fontSize, OVER_SAMPLE_SCALE));
    }

    public Font() {
        this(16);
    }

    @Override
    public int lineHeight() {
        return this.fontSize;
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

    public GlyphInfo getGlyph(int codepoint) {
        return glyphCache.computeIfAbsent(codepoint, cp -> {
            FontAtlas last = atlases.get(atlases.size() - 1);
            GlyphInfo glyph = last.tryRasterize(cp, atlases.size() - 1);
            if (glyph == null) {
                FontAtlas newAtlas = new FontAtlas(fontData, fontSize, OVER_SAMPLE_SCALE);
                atlases.add(newAtlas);
                glyph = newAtlas.tryRasterize(cp, atlases.size() - 1);
                if (glyph == null) {
                    glyph = new GlyphInfo(cp, atlases.size() - 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                }
            }
            return glyph;
        });
    }

    public FontAtlas getAtlas(int index) {
        return atlases.get(index);
    }

    public int getAtlasCount() {
        return atlases.size();
    }

    public int getKerning(int codepoint1, int codepoint2) {
        return atlases.get(0).getKerning(codepoint1, codepoint2);
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
                width += getKerning(prevCodepoint, codepoint);
            }

            GlyphInfo glyph = getGlyph(codepoint);
            width += (int) glyph.advance();

            prevCodepoint = codepoint;
        }

        return width;
    }
    
    @Override
    public int width(IComponent component) {
        AtomicInteger l = new AtomicInteger();
        component.visitStyled((c, t, _) ->
                l.addAndGet(c.extraWidth() + this.width(t)), ComponentStyle.EMPTY);
        return l.get();
    }
    
    public int getFontSize() {
        return fontSize;
    }

    public void destroy() {
        for (FontAtlas atlas : atlases) {
            atlas.destroy();
        }
    }
}
