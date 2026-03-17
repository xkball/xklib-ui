package com.xkball.xklib.api.gui.css;

import com.xkball.xklib.antlr.css.css3Parser;

public interface IPropertyFactory<T> {
    
    T parse(css3Parser.ExprContext expr);
    
}
