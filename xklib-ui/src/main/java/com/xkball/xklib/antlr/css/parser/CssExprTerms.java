package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;

import java.util.ArrayList;
import java.util.List;

public final class CssExprTerms {

    private CssExprTerms() {
    }

    public static List<List<css3Parser.TermContext>> splitBySpace(css3Parser.ExprContext expr) {
        return splitBySpace(expr.term());
    }

    public static List<List<css3Parser.TermContext>> splitBySpace(List<css3Parser.TermContext> terms) {
        List<List<css3Parser.TermContext>> groups = new ArrayList<>();
        if (terms.isEmpty()) {
            return groups;
        }
        int start = 0;
        for (int i = 0; i < terms.size() - 1; i++) {
            var ws = terms.get(i).getRuleContext(css3Parser.WsContext.class, 0);
            if (ws != null && !ws.Space().isEmpty()) {
                groups.add(terms.subList(start, i + 1));
                start = i + 1;
            }
        }
        groups.add(terms.subList(start, terms.size()));
        return groups;
    }

    public static String textOf(List<css3Parser.TermContext> terms) {
        StringBuilder builder = new StringBuilder();
        for (var term : terms) {
            builder.append(term.getRuleContext().getText());
        }
        return builder.toString().trim();
    }
}

