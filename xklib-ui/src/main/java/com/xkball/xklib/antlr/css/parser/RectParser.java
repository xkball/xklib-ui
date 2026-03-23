package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.css.property.value.CssRect;

import java.util.List;

public class RectParser implements IPropertyFactory<CssRect> {

    @Override
    public CssRect parse(css3Parser.ExprContext expr) {
        List<CssLengthUnit> values = CssExprTerms.splitBySpace(expr).stream().map(LengthUnitParser::parseInner).toList();
        return switch (values.size()) {
            case 1 -> new CssRect(values.getFirst(), values.getFirst(), values.getFirst(), values.getFirst());
            case 2 -> new CssRect(values.get(1), values.get(1), values.getFirst(), values.getFirst());
            case 3 -> new CssRect(values.get(1), values.get(1), values.getFirst(), values.get(2));
            case 4 -> new CssRect(values.get(3), values.get(1), values.getFirst(), values.get(2));
            default -> throw new IllegalArgumentException("Cannot parse rect: " + expr.getText());
        };
    }
}

