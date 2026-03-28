package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.antlr.css.css3Parser;

import java.util.Locale;

public class ColorParser implements IPropertyFactory<Integer> {

    @Override
    public Integer parse(css3Parser.ExprContext expr) {
        var text = expr.getText().trim().toLowerCase(Locale.ROOT);
        if (text.startsWith("#")) {
            return parseHash(text.substring(1));
        }
        if (text.startsWith("0x")) {
            return (int) Long.parseLong(text.substring(2), 16);
        }
        return Integer.decode(text);
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

