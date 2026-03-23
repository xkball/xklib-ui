package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;

public class BooleanParser implements IPropertyFactory<Boolean> {

    @Override
    public Boolean parse(css3Parser.ExprContext expr) {
        if (expr.term().size() != 1) {
            throw new IllegalArgumentException("Cannot parse boolean: " + expr.getText());
        }
        String text = expr.term().getFirst().getRuleContext().getText().trim().toLowerCase();
        if ("true".equals(text) || "1".equals(text)) {
            return true;
        }
        if ("false".equals(text) || "0".equals(text)) {
            return false;
        }
        throw new IllegalArgumentException("Cannot parse boolean: " + expr.getText());
    }
}

