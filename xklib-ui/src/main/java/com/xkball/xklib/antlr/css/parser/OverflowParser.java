package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssOverflow;
import dev.vfyjxf.taffy.style.Overflow;

import java.util.Locale;
import java.util.List;

public class OverflowParser implements IPropertyFactory<CssOverflow> {

    @Override
    public CssOverflow parse(css3Parser.ExprContext expr) {
        List<List<css3Parser.TermContext>> groups = CssExprTerms.splitBySpace(expr);
        if (groups.size() == 1) {
            Overflow value = parseSingle(groups.getFirst());
            return new CssOverflow(value, value);
        }
        if (groups.size() == 2) {
            return new CssOverflow(parseSingle(groups.getFirst()), parseSingle(groups.get(1)));
        }
        throw new IllegalArgumentException("Cannot parse overflow: " + expr.getText());
    }

    private Overflow parseSingle(List<css3Parser.TermContext> terms) {
        String text = CssExprTerms.textOf(terms).replace('-', '_').toUpperCase(Locale.ROOT);
        return Overflow.valueOf(text);
    }
}


