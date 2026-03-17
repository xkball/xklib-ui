package com.xkball.xklib.antlr.css.parser;

import com.xkball.xklib.antlr.css.css3Parser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnumParser<T extends Enum<T>> implements IPropertyFactory<T> {
    
    private final Class<T> clazz;
    private final Map<String,T> values = new HashMap<>();
    
    public EnumParser(Class<T> clazz) {
        this.clazz = clazz;
        for(var e : clazz.getEnumConstants()){
            values.put(e.name().toLowerCase(Locale.ROOT),e);
        }
    }
    
    @Override
    public T parse(css3Parser.ExprContext expr) {
        if(expr.term().size() != 1) return null;
        return values.get(expr.term().getFirst().getRuleContext(css3Parser.IdentContext.class,0).getText());
    }
}
