package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssSize;

public class SizeParser implements IPropertyFactory<CssSize> {
    
    @Override
    public CssSize parse(css3Parser.ExprContext expr) {
        var groups = CssExprTerms.splitBySpace(expr);
        if(groups.size() == 1){
            var value = LengthUnitParser.parseInner(groups.getFirst());
            return new CssSize(value,value);
        }
        if(groups.size() == 2){
            var first = LengthUnitParser.parseInner(groups.getFirst());
            var second = LengthUnitParser.parseInner(groups.get(1));
            return new CssSize(first,second);
        }
        throw new IllegalArgumentException("Cannot parse size: " + expr.getText());
    }
    
}
