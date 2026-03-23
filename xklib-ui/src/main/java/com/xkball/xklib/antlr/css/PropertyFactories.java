package com.xkball.xklib.antlr.css;

import com.xkball.xklib.antlr.css.parser.EnumParser;
import com.xkball.xklib.antlr.css.parser.BooleanParser;
import com.xkball.xklib.antlr.css.parser.GridLineParser;
import com.xkball.xklib.antlr.css.parser.LengthUnitParser;
import com.xkball.xklib.antlr.css.parser.OverflowParser;
import com.xkball.xklib.antlr.css.parser.RectParser;
import com.xkball.xklib.antlr.css.parser.SizeParser;
import com.xkball.xklib.antlr.css.parser.TrackListParser;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.value.CssGridLine;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.css.property.value.CssOverflow;
import com.xkball.xklib.ui.css.property.value.CssRect;
import com.xkball.xklib.ui.css.property.value.CssSize;
import com.xkball.xklib.ui.css.property.value.CssTrackList;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.BoxSizing;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.FlexWrap;
import dev.vfyjxf.taffy.style.GridAutoFlow;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.LengthPercentageAuto;
import dev.vfyjxf.taffy.style.Overflow;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDirection;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyPosition;
import dev.vfyjxf.taffy.style.TextAlign;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PropertyFactories {
    
    public static final PropertyFactories INSTANCE = new PropertyFactories();
    
    private PropertyFactories(){
        this.register(CssLengthUnit.class, LengthUnitParser::new);
        this.register(CssRect.class, RectParser::new);
        this.register(CssSize.class, SizeParser::new);
        this.register(CssOverflow.class, OverflowParser::new);
        this.register(CssGridLine.class, GridLineParser::new);
        this.register(CssTrackList.class, TrackListParser::new);
        this.register(TaffyDisplay.class, new EnumParser<>(TaffyDisplay.class));
        this.register(TaffyDirection.class, new EnumParser<>(TaffyDirection.class));
        this.register(BoxSizing.class, new EnumParser<>(BoxSizing.class));
        this.register(TaffyPosition.class, new EnumParser<>(TaffyPosition.class));
        this.register(AlignItems.class, new EnumParser<>(AlignItems.class));
        this.register(AlignContent.class, new EnumParser<>(AlignContent.class));
        this.register(FlexDirection.class, new EnumParser<>(FlexDirection.class));
        this.register(FlexWrap.class, new EnumParser<>(FlexWrap.class));
        this.register(GridAutoFlow.class, new EnumParser<>(GridAutoFlow.class));
        this.register(TextAlign.class, new EnumParser<>(TextAlign.class));
        this.register(Overflow.class, new EnumParser<>(Overflow.class));
        this.register(Boolean.class, BooleanParser::new);
        this.register(Float.class,(expr) -> {
            String text = expr.getText().trim();
            String[] ratio = text.split("/", 2);
            if (ratio.length == 2) {
                return Float.parseFloat(ratio[0].trim()) / Float.parseFloat(ratio[1].trim());
            }
            return Float.parseFloat(text);
        });
        this.register(TaffyDimension.class, expr -> LengthUnitParser.parseInner(expr.term()).toDimension());
        this.register(LengthPercentage.class, expr -> LengthUnitParser.parseInner(expr.term()).toLengthPercentage());
        this.register(LengthPercentageAuto.class, expr -> LengthUnitParser.parseInner(expr.term()).toLengthPercentageAuto());
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
