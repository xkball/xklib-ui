package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssGridLine;
import dev.vfyjxf.taffy.style.GridPlacement;

public class GridLineParser implements IPropertyFactory<CssGridLine> {

    @Override
    public CssGridLine parse(css3Parser.ExprContext expr) {
        String text = expr.getText().trim();
        String[] parts = text.split("/", 2);
        GridPlacement start = parsePlacement(parts[0].trim());
        GridPlacement end = parts.length > 1 ? parsePlacement(parts[1].trim()) : GridPlacement.auto();
        return new CssGridLine(start, end);
    }

    private GridPlacement parsePlacement(String text) {
        if (text.isEmpty() || "auto".equals(text)) {
            return GridPlacement.auto();
        }
        String[] tokens = text.split("\\s+");
        if (tokens.length == 1) {
            return parseSingle(tokens[0]);
        }
        if ("span".equals(tokens[0])) {
            if (tokens.length == 2) {
                if (isInteger(tokens[1])) {
                    return GridPlacement.span(Integer.parseInt(tokens[1]));
                }
                return GridPlacement.namedSpan(tokens[1], 1);
            }
            if (tokens.length == 3 && !isInteger(tokens[1]) && isInteger(tokens[2])) {
                return GridPlacement.namedSpan(tokens[1], Integer.parseInt(tokens[2]));
            }
        }
        if (tokens.length == 2 && !isInteger(tokens[0]) && isInteger(tokens[1])) {
            return GridPlacement.namedLine(tokens[0], Integer.parseInt(tokens[1]));
        }
        throw new IllegalArgumentException("Cannot parse grid placement: " + text);
    }

    private GridPlacement parseSingle(String token) {
        if (isInteger(token)) {
            return GridPlacement.line(Integer.parseInt(token));
        }
        return GridPlacement.namedLine(token);
    }

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

