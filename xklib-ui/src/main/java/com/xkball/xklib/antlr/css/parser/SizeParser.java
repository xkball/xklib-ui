package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssSize;

public class SizeParser implements IPropertyFactory<CssSize> {
    
    @Override
    public CssSize parse(css3Parser.ExprContext expr) {
        var splitIndex = 0;
        var splitCount = 0;
        var terms = expr.term();
        for (int i = 0; i < terms.size() - 1; i++) {
            var split = !terms.get(i).getRuleContext(css3Parser.WsContext.class, 0).Space().isEmpty();
            if(split){
                splitCount += 1;
                splitIndex = i;
            }
        }
        if(splitCount == 1){
            var first = LengthUnitParser.parseInner(terms.subList(0,splitIndex + 1));
            var second = LengthUnitParser.parseInner(terms.subList(splitIndex + 1, terms.size()));
            return new CssSize(first,second);
        }
        throw new IllegalArgumentException("Cannot parse size: " + expr.getText());
    }
    
}
