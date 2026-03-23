package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import dev.vfyjxf.taffy.style.CalcExpression;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class LengthUnitParser implements IPropertyFactory<CssLengthUnit> {
    
    @Override
    public CssLengthUnit parse(css3Parser.ExprContext expr) {
        return parseInner(expr.term());
    }
    
    public static CssLengthUnit parseInner(List<css3Parser.TermContext> terms){
        var first = CssLengthUnit.of(terms.getFirst().getRuleContext().getText().trim());
        if(terms.size() == 1){
            return first;
        }
        else{
            CalcExpression result = null;
            for (int i = 1; i < terms.size(); i++) {
                var term = terms.get(i);
                var next = CssLengthUnit.of(term.getRuleContext().getText().trim());
                var type = term.getRuleContext(ParserRuleContext.class,0);
                var sign = type.getToken(css3Parser.Minus, 0);
                if(result == null) result = new CssLengthUnit.Combination(first,next,sign == null);
                else result = new CssLengthUnit.Combination(new CssLengthUnit(result),next,sign == null);
            }
            return new CssLengthUnit(result);
        }
    }
    
}
