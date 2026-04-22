package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.antlr.css.css3Parser;

import java.util.Locale;

public class ColorParser implements IPropertyFactory<Integer> {

    @Override
    public Integer parse(css3Parser.ExprContext expr) {
        var text = expr.getText().trim().toLowerCase(Locale.ROOT);
        if (text.startsWith("rgb(")) {
            return parseRgb(text);
        }
        if (text.startsWith("argb(")) {
            return parseArgb(text);
        }
        if (text.startsWith("#")) {
            return parseHash(text.substring(1));
        }
        if (text.startsWith("0x")) {
            return (int) Long.parseLong(text.substring(2), 16);
        }
        return Integer.decode(text);
    }

    private static int parseRgb(String text) {
        int left = text.indexOf('(');
        int right = text.lastIndexOf(')');
        if (left < 0 || right != text.length() - 1) {
            throw new IllegalArgumentException("Invalid color: " + text);
        }
        String inner = text.substring(left + 1, right).trim();
        String[] parts = splitArgs(inner);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid color: " + text);
        }
        int r = parseChannel(parts[0], text);
        int g = parseChannel(parts[1], text);
        int b = parseChannel(parts[2], text);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    private static int parseArgb(String text) {
        int left = text.indexOf('(');
        int right = text.lastIndexOf(')');
        if (left < 0 || right != text.length() - 1) {
            throw new IllegalArgumentException("Invalid color: " + text);
        }
        String inner = text.substring(left + 1, right).trim();
        String[] parts = splitArgs(inner);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid color: " + text);
        }
        int a = parseChannel(parts[0], text);
        int r = parseChannel(parts[1], text);
        int g = parseChannel(parts[2], text);
        int b = parseChannel(parts[3], text);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static String[] splitArgs(String inner) {
        if (inner.isEmpty()) {
            return new String[0];
        }
        String[] raw = inner.split(",");
        for (int i = 0; i < raw.length; i++) {
            raw[i] = raw[i].trim();
        }
        return raw;
    }

    private static int parseChannel(String value, String full) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Invalid color: " + full);
        }
        int v;
        try {
            if (value.startsWith("0x")) {
                v = (int) Long.parseLong(value.substring(2), 16);
            } else {
                v = Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid color: " + full, e);
        }
        if (v < 0 || v > 255) {
            throw new IllegalArgumentException("Invalid color: " + full);
        }
        return v;
    }

    private static int parseHash(String text) {
        if (text.length() == 3) {
            var r = text.substring(0, 1);
            var g = text.substring(1, 2);
            var b = text.substring(2, 3);
            return (int) Long.parseLong("ff" + r + r + g + g + b + b, 16);
        }
        if (text.length() == 4) {
            var a = text.substring(0, 1);
            var r = text.substring(1, 2);
            var g = text.substring(2, 3);
            var b = text.substring(3, 4);
            return (int) Long.parseLong(a + a + r + r + g + g + b + b, 16);
        }
        if (text.length() == 6) {
            return (int) Long.parseLong("ff" + text, 16);
        }
        if (text.length() == 8) {
            return (int) Long.parseLong(text, 16);
        }
        throw new IllegalArgumentException("Invalid color: #" + text);
    }
}

