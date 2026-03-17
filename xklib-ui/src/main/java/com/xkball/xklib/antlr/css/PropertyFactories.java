package com.xkball.xklib.antlr.css;

import com.xkball.xklib.antlr.css.parser.EnumParser;
import com.xkball.xklib.antlr.css.parser.LengthUnitParser;
import com.xkball.xklib.antlr.css.parser.SizeParser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.css.property.value.CssSize;
import dev.vfyjxf.taffy.style.TaffyDisplay;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PropertyFactories {
    
    public static final PropertyFactories INSTANCE = new PropertyFactories();
    
    private PropertyFactories(){
        this.register(CssLengthUnit.class, LengthUnitParser::new);
        this.register(CssSize.class, SizeParser::new);
        this.register(TaffyDisplay.class, new EnumParser<>(TaffyDisplay.class));
        this.register(Float.class,(expr) -> Float.parseFloat(expr.term().getFirst().getRuleContext(css3Parser.NumberContext.class,0).getText()));
    }
    private final Map<Class<?>, IPropertyFactory<?>> factorise = new HashMap<>();
    
    public <T> void register(Class<T> clazz, Supplier<IPropertyFactory<T>> factory){
        this.register(clazz, factory.get());
    }
    
    public <T> void register(Class<T> clazz, IPropertyFactory<T> factory){
        this.factorise.put(clazz,factory);
    }
    
    @SuppressWarnings("unchecked")
    public <T> IPropertyFactory<T> get(Class<T> clazz){
        return (IPropertyFactory<T>) factorise.get(clazz);
    }
    
}
