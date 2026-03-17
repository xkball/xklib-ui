package com.xkball.xklib.antlr.css;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.ui.css.property.HeightProperty;
import com.xkball.xklib.ui.css.property.ScrollbarWidthProperty;
import com.xkball.xklib.ui.css.property.SizeProperty;
import com.xkball.xklib.ui.css.property.WidthProperty;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.css.property.value.CssSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CssStyles {
    
    public static final CssStyles INSTANCE = new CssStyles();
    private static final Logger LOGGER = LoggerFactory.getLogger(CssStyles.class);
    
    public final Map<String, StyleData<?>> styleMap = new HashMap<>();
    
    private CssStyles(){
        this.register(WidthProperty.NAME,WidthProperty::new, CssLengthUnit.class);
        this.register(HeightProperty.NAME,HeightProperty::new, CssLengthUnit.class);
        this.register(SizeProperty.NAME, SizeProperty::new, CssSize.class);
        this.register(ScrollbarWidthProperty.NAME, ScrollbarWidthProperty::new, Float.class);
        
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public @Nullable IStyleProperty<?> parse(String name, css3Parser.ExprContext expr){
        try {
            StyleData data = this.styleMap.get(name);
            if(data == null){
                LOGGER.warn("Unknown style: {}:{}", name, expr.getText());
                return null;
            }
            Object t = PropertyFactories.INSTANCE.get(data.clazz).parse(expr);
            return (IStyleProperty<?>) data.factory.apply(t);
        } catch (Exception e){
            LOGGER.warn("Cannot parse style property: {}:{}", name, expr.getText(), e);
        }
        return null;
    }
    
    public <T> void register(String name, Function<T,IStyleProperty<T>> factory, Class<T> clazz){
        this.styleMap.put(name, new StyleData<>(name, factory, clazz));
    }
    
    public record StyleData<T>(String name, Function<T,IStyleProperty<T>> factory, Class<T> clazz){}
    
}
