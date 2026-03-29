package com.xkball.xklib.x3d.backend.gl.state;

import com.ibm.icu.text.BreakIterator;
import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.render.ComponentStyle;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IconComponent;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.backend.gl.font.Font;
import com.xkball.xklib.x3d.backend.gl.font.GlyphInfo;
import com.xkball.xklib.x3d.backend.gl.pipeline.RenderPipelines;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TextRenderState {

    private TextRenderState() {
    }

    public static List<IGuiElementRenderState> buildStates(
        IRenderPipeline fontPipeline,
        Font font,
        IComponent component,
        float x,
        float y,
        int defaultColor,
        boolean drawShadow,
        Matrix3x2fc pose,
        @Nullable ScreenRectangle scissorArea
    ) {
        if (component == null) {
            return List.of();
        }

        float baseY = y + font.getFontSize() - 2;

        List<SegmentInfo> segments = collectSegments(font, component, x, defaultColor);
        if (segments.isEmpty()) {
            return List.of();
        }

        Map<Integer, List<GlyphBatchRenderState.GlyphQuad>> shadowQuads = new LinkedHashMap<>();
        Map<Integer, List<GlyphBatchRenderState.GlyphQuad>> normalQuads = new LinkedHashMap<>();
        List<float[]> lines = new ArrayList<>();
        List<IGuiElementRenderState> result = new ArrayList<>();

        for (SegmentInfo seg : segments) {
            if (seg.isIcon()) {
                float iconWidth = font.getFontSize();
                float iconX = seg.startX;
                var textureManger = XKLib.RENDER_CONTEXT.get().getTextureManager();
                var sprite = textureManger.getSprite(seg.getIcon().icon());
                if (sprite != null) {
                    result.add(new BlitRenderState(
                        IRenderPipelineSource.getInstance().getGuiTextured(),
                        TextureSetup.singleTexture(sprite.texture()),
                        new Matrix3x2f(pose),
                        iconX, y, iconX + iconWidth, y + font.getFontSize(),
                        sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
                        0xffffffff,
                        scissorArea
                    ));
                }
            } else {
                if (drawShadow) {
                    collectGlyphs(font, seg.text, seg.startX + 1, baseY + 1, mulColor(seg.color, 0.25f), shadowQuads);
                }
                collectGlyphs(font, seg.text, seg.startX, baseY, seg.color, normalQuads);

                if (seg.style.strikethrough()) {
                    float strikeY = y + font.getFontSize() * 0.5f;
                    lines.add(new float[]{seg.startX, strikeY, seg.endX, strikeY + 1f, seg.color});
                }
                if (seg.style.baseline()) {
                    float sy = y + font.getFontSize() - 2;
                    lines.add(new float[]{seg.startX, sy, seg.endX, sy + 1f, seg.color});
                }
            }
        }

        for (var entry : shadowQuads.entrySet()) {
            result.add(new GlyphBatchRenderState(
                fontPipeline,
                TextureSetup.singleTexture(font.getAtlas(entry.getKey())),
                new Matrix3x2f(pose),
                entry.getValue(),
                -0.01f,
                scissorArea
            ));
        }

        for (var entry : normalQuads.entrySet()) {
            result.add(new GlyphBatchRenderState(
                fontPipeline,
                TextureSetup.singleTexture(font.getAtlas(entry.getKey())),
                new Matrix3x2f(pose),
                entry.getValue(),
                0f,
                scissorArea
            ));
        }

        for (float[] st : lines) {
            result.add(new ColoredRectangleRenderState(
                RenderPipelines.GUI.get(),
                TextureSetup.EMPTY,
                new Matrix3x2f(pose),
                st[0], st[1], st[2], st[3],
                (int) st[4], (int) st[4],
                scissorArea
            ));
        }

        return result;
    }

    private static List<SegmentInfo> collectSegments(Font font, IComponent component, float startX, int defaultColor) {
        List<SegmentInfo> segments = new ArrayList<>();
        float[] xTracker = {startX};

        collectSegmentsRecursive(font, component, xTracker, defaultColor, segments);
        return segments;
    }

    private static void collectSegmentsRecursive(Font font, IComponent component, float[] xTracker, int defaultColor, List<SegmentInfo> segments) {
        component.visitStyled((ic,text, style) -> {
            if (ic instanceof IconComponent icon) {
                float iconWidth = font.getFontSize();
                segments.add(new SegmentInfo(icon, xTracker[0], iconWidth, -1, ComponentStyle.EMPTY));
                xTracker[0] += iconWidth;
            }
            if (text == null || text.isEmpty()) {
                return;
            }
            int segColor = style.color() != null ? style.color() : defaultColor;
            float segStart = xTracker[0];
            float segEnd = segStart + font.width(text);
            segments.add(new SegmentInfo(text, segStart, segEnd, segColor, style));
            xTracker[0] = segEnd;
        }, ComponentStyle.EMPTY);
    }

    private static void collectGlyphs(
        Font font,
        String text,
        float startX,
        float startY,
        int color,
        Map<Integer, List<GlyphBatchRenderState.GlyphQuad>> atlasQuads
    ) {
        float currentX = startX;
        int prevCodepoint = -1;

        BreakIterator iterator = BreakIterator.getCharacterInstance();
        iterator.setText(text);

        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String grapheme = text.substring(start, end);
            int codepoint = grapheme.codePointAt(0);

            if (prevCodepoint >= 0) {
                currentX += font.getKerning(prevCodepoint, codepoint);
            }

            GlyphInfo glyph = font.getGlyph(codepoint);

            if (glyph.width() > 0 && glyph.height() > 0) {
                float x0 = currentX + glyph.bearingX();
                float y0 = startY + glyph.bearingY();
                float x1 = x0 + glyph.width();
                float y1 = y0 + glyph.height();

                atlasQuads.computeIfAbsent(glyph.atlasIndex(), _ -> new ArrayList<>())
                    .add(new GlyphBatchRenderState.GlyphQuad(x0, y0, x1, y1, glyph.u0(), glyph.v0(), glyph.u1(), glyph.v1(), color));
            }

            currentX += glyph.advance();
            prevCodepoint = codepoint;
        }
    }

    private static int mulColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static class SegmentInfo {
        private final String text;
        private final IconComponent icon;
        private final float startX;
        private final float endX;
        private final int color;
        private final ComponentStyle style;

        SegmentInfo(String text, float startX, float endX, int color, ComponentStyle style) {
            this.text = text;
            this.icon = null;
            this.startX = startX;
            this.endX = endX;
            this.color = color;
            this.style = style;
        }

        SegmentInfo(IconComponent icon, float startX, float width, int color, ComponentStyle style) {
            this.text = null;
            this.icon = icon;
            this.startX = startX;
            this.endX = startX + width;
            this.color = color;
            this.style = style;
        }

        boolean isIcon() {
            return icon != null;
        }

        IconComponent getIcon() {
            return icon;
        }
    }
}
