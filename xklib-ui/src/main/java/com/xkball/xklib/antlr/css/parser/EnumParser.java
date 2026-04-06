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
            String lowered = e.name().toLowerCase(Locale.ROOT);
            values.put(lowered, e);
            values.put(lowered.replace('_', '-'), e);
        }
    }
    
    @Override
    public T parse(css3Parser.ExprContext expr) {
        if(expr.term().size() != 1) return null;
        String raw = expr.term().getFirst().getRuleContext().getText().trim().toLowerCase(Locale.ROOT);
        var result = values.get(raw);
        return result == null ? clazz.getEnumConstants()[0] : result;
    }
}
