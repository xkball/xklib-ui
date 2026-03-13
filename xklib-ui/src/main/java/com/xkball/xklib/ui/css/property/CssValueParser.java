package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.resource.ResourceLocation;
import dev.vfyjxf.taffy.style.TaffyDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class CssValueParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CssValueParser.class);

    private static final Map<String, Integer> NAMED_COLORS = namedColors();

    private CssValueParser() {
    }

    public static Optional<Integer> parseColor(String raw) {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        Integer named = NAMED_COLORS.get(value);
        if (named != null) {
            return Optional.of(named);
        }
        if (value.startsWith("#")) {
            return parseHex(value.substring(1));
        }
        if (value.startsWith("0x")) {
            return parseHex(value.substring(2));
        }
        if (value.startsWith("rgb(")) {
            return parseRgb(value);
        }
        if (value.startsWith("rgba(")) {
            return parseRgba(value);
        }
        return Optional.empty();
    }

    public static Optional<Float> parseLengthPx(String raw) {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.startsWith("min(") && value.endsWith(")")) {
            return parseMinOrMax(value, true);
        }
        if (value.startsWith("max(") && value.endsWith(")")) {
            return parseMinOrMax(value, false);
        }
        if (value.endsWith("px")) {
            return parseFloat(value.substring(0, value.length() - 2));
        }
        if (value.endsWith("em")) {
            return parseFloat(value.substring(0, value.length() - 2)).map(v -> v * 16f);
        }
        if (value.endsWith("%")) {
            return parseFloat(value.substring(0, value.length() - 1)).map(v -> v / 100f);
        }
        return Optional.empty();
    }

    public static Optional<TaffyDimension> parseDimension(String raw) {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if ("auto".equals(value)) {
            return Optional.of(TaffyDimension.AUTO);
        }
        if ("content".equals(value)) {
            return Optional.of(TaffyDimension.CONTENT);
        }
        if ("max-content".equals(value)) {
            return Optional.of(TaffyDimension.MAX_CONTENT);
        }
        if ("min-content".equals(value)) {
            return Optional.of(TaffyDimension.MIN_CONTENT);
        }
        if (value.startsWith("min(") || value.startsWith("max(")) {
            return parseLengthPx(value).map(TaffyDimension::length);
        }
        if (value.endsWith("%")) {
            return parseFloat(value.substring(0, value.length() - 1)).map(v -> TaffyDimension.percent(v / 100f));
        }
        if (value.endsWith("px")) {
            return parseFloat(value.substring(0, value.length() - 2)).map(TaffyDimension::length);
        }
        if (value.endsWith("em")) {
            return parseFloat(value.substring(0, value.length() - 2)).map(v -> TaffyDimension.length(v * 16f));
        }
        return Optional.empty();
    }

    public static Optional<ResourceLocation> parseResourceLocationFromRl(String raw) {
        String value = raw.trim();
        if (!value.startsWith("rl(") || !value.endsWith(")")) {
            return Optional.empty();
        }
        String inner = unquote(value.substring(3, value.length() - 1).trim());
        int split = inner.indexOf(':');
        if (split > 0) {
            return Optional.of(new ResourceLocation(inner.substring(0, split), inner.substring(split + 1)));
        }
        return Optional.of(ResourceLocation.of(inner));
    }

    public static boolean looksLikeGridFunction(String raw) {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        return value.startsWith("repeat(") || value.startsWith("minmax(");
    }

    private static Optional<Integer> parseHex(String hexRaw) {
        String hex = hexRaw.trim();
        try {
            if (hex.length() == 3) {
                int r = Integer.parseInt(hex.substring(0, 1) + hex.substring(0, 1), 16);
                int g = Integer.parseInt(hex.substring(1, 2) + hex.substring(1, 2), 16);
                int b = Integer.parseInt(hex.substring(2, 3) + hex.substring(2, 3), 16);
                return Optional.of((0xFF << 24) | (r << 16) | (g << 8) | b);
            }
            if (hex.length() == 4) {
                int a = Integer.parseInt(hex.substring(0, 1) + hex.substring(0, 1), 16);
                int r = Integer.parseInt(hex.substring(1, 2) + hex.substring(1, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 3) + hex.substring(2, 3), 16);
                int b = Integer.parseInt(hex.substring(3, 4) + hex.substring(3, 4), 16);
                return Optional.of((a << 24) | (r << 16) | (g << 8) | b);
            }
            if (hex.length() == 6) {
                long rgb = Long.parseLong(hex, 16);
                return Optional.of((int) (0xFF000000L | rgb));
            }
            if (hex.length() == 8) {
                long argb = Long.parseLong(hex, 16);
                return Optional.of((int) argb);
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("invalid hex color: {}", hexRaw, e);
        }
        return Optional.empty();
    }

    private static Optional<Integer> parseRgb(String rgb) {
        String body = rgb.substring(4, rgb.length() - 1);
        List<String> parts = splitArgs(body);
        if (parts.size() != 3) {
            return Optional.empty();
        }
        Optional<Integer> r = parseChannel(parts.get(0));
        Optional<Integer> g = parseChannel(parts.get(1));
        Optional<Integer> b = parseChannel(parts.get(2));
        if (r.isEmpty() || g.isEmpty() || b.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of((0xFF << 24) | (r.get() << 16) | (g.get() << 8) | b.get());
    }

    private static Optional<Integer> parseRgba(String rgba) {
        String body = rgba.substring(5, rgba.length() - 1);
        List<String> parts = splitArgs(body);
        if (parts.size() != 4) {
            return Optional.empty();
        }
        Optional<Integer> r = parseChannel(parts.get(0));
        Optional<Integer> g = parseChannel(parts.get(1));
        Optional<Integer> b = parseChannel(parts.get(2));
        Optional<Integer> a = parseAlpha(parts.get(3));
        if (r.isEmpty() || g.isEmpty() || b.isEmpty() || a.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of((a.get() << 24) | (r.get() << 16) | (g.get() << 8) | b.get());
    }

    private static Optional<Integer> parseChannel(String part) {
        return parseFloat(part).map(v -> Math.clamp(Math.round(v), 0, 255));
    }

    private static Optional<Integer> parseAlpha(String part) {
        String value = part.trim();
        Optional<Float> parsed = parseFloat(value);
        if (parsed.isEmpty()) {
            return Optional.empty();
        }
        float number = parsed.get();
        if (number <= 1f) {
            return Optional.of(Math.clamp(Math.round(number * 255f), 0, 255));
        }
        return Optional.of(Math.clamp(Math.round(number), 0, 255));
    }

    private static Optional<Float> parseMinOrMax(String raw, boolean min) {
        String body = raw.substring(raw.indexOf('(') + 1, raw.length() - 1);
        List<String> parts = splitArgs(body);
        if (parts.isEmpty()) {
            return Optional.empty();
        }
        Optional<Float> selected = Optional.empty();
        for (String part : parts) {
            Optional<Float> value = parseLengthPx(part);
            if (value.isEmpty()) {
                return Optional.empty();
            }
            if (selected.isEmpty()) {
                selected = value;
                continue;
            }
            float current = selected.get();
            float next = value.get();
            selected = Optional.of(min ? Math.min(current, next) : Math.max(current, next));
        }
        return selected;
    }

    private static Optional<Float> parseFloat(String number) {
        try {
            return Optional.of(Float.parseFloat(number.trim()));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    private static List<String> splitArgs(String source) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            }
            if (c == ',' && depth == 0) {
                result.add(current.toString().trim());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        if (!current.isEmpty()) {
            result.add(current.toString().trim());
        }
        return result;
    }

    private static String unquote(String source) {
        if ((source.startsWith("\"") && source.endsWith("\"")) || (source.startsWith("'") && source.endsWith("'"))) {
            return source.substring(1, source.length() - 1);
        }
        return source;
    }

    private static Map<String, Integer> namedColors() {
        Map<String, Integer> colors = new HashMap<>();
        colors.put("black", 0xFF000000);
        colors.put("silver", 0xFFC0C0C0);
        colors.put("gray", 0xFF808080);
        colors.put("white", 0xFFFFFFFF);
        colors.put("maroon", 0xFF800000);
        colors.put("red", 0xFFFF0000);
        colors.put("purple", 0xFF800080);
        colors.put("fuchsia", 0xFFFF00FF);
        colors.put("green", 0xFF008000);
        colors.put("lime", 0xFF00FF00);
        colors.put("olive", 0xFF808000);
        colors.put("yellow", 0xFFFFFF00);
        colors.put("navy", 0xFF000080);
        colors.put("blue", 0xFF0000FF);
        colors.put("teal", 0xFF008080);
        colors.put("aqua", 0xFF00FFFF);
        return colors;
    }
}

