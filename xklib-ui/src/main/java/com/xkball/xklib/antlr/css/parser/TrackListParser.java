package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.css.property.value.CssTrackList;
import dev.vfyjxf.taffy.style.TrackSizingFunction;

import java.util.ArrayList;
import java.util.List;

public class TrackListParser implements IPropertyFactory<CssTrackList> {

    @Override
    public CssTrackList parse(css3Parser.ExprContext expr) {
        List<TrackSizingFunction> values = new ArrayList<>();
        for (var term : expr.term()) {
            values.add(parseTrack(term.getText().trim()));
        }
        return new CssTrackList(values);
    }

    private TrackSizingFunction parseTrack(String text) {
        text = text.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Empty track sizing function text");
        }
        if ("auto".equals(text)) {
            return TrackSizingFunction.auto();
        }
        if ("min-content".equals(text)) {
            return TrackSizingFunction.minContent();
        }
        if ("max-content".equals(text)) {
            return TrackSizingFunction.maxContent();
        }
        if (text.endsWith("fr")) {
            return TrackSizingFunction.fr(Float.parseFloat(text.substring(0, text.length() - 2)));
        }
        if (text.startsWith("fit-content(") && text.endsWith(")")) {
            String inner = text.substring("fit-content(".length(), text.length() - 1);
            return TrackSizingFunction.fitContent(CssLengthUnit.of(inner).toLengthPercentage());
        }
        if (text.startsWith("minmax(") && text.endsWith(")")) {
            String inner = text.substring("minmax(".length(), text.length() - 1);
            int comma = findComma(inner);
            if (comma <= 0) {
                throw new IllegalArgumentException("Cannot parse track sizing function: " + text);
            }
            TrackSizingFunction min = parseTrack(inner.substring(0, comma).trim());
            TrackSizingFunction max = parseTrack(inner.substring(comma + 1).trim());
            return TrackSizingFunction.minmax(min, max);
        }
        return TrackSizingFunction.fixed(CssLengthUnit.of(text).toLengthPercentage());
    }

    private int findComma(String text) {
        int depth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (c == ',' && depth == 0) {
                return i;
            }
        }
        return -1;
    }
}

