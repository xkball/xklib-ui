package com.xkball.xklibmc.x3d.backend.b3d.gui;

import com.xkball.xklib.ui.render.ComponentStyle;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.LiteralComponent;
import com.xkball.xklib.ui.render.SequenceComponent;
import com.xkball.xklib.ui.render.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;

public class ComponentConverter {
    
    public static Component toComponent(IComponent component) {
        if(component instanceof LiteralComponent(String text, ComponentStyle style)){
            return Component.literal(text).withStyle(toStyle(style));
        }
        if(component instanceof TranslatableComponent(String key, ComponentStyle style)){
            return Component.translatable(key, style).withStyle(toStyle(style));
        }
        if(component instanceof SequenceComponent(List<IComponent> parts, ComponentStyle style)){
            var result = Component.empty();
            for(var c : parts){
                result.append(toComponent(c));
            }
            return result.withStyle(toStyle(style));
        }
        return Component.empty();
    }
    
    public static Style toStyle(ComponentStyle componentStyle) {
        var result = Style.EMPTY;
        if(componentStyle.color() != null) result.withColor(componentStyle.color());
        if(componentStyle.baseline()) result.withUnderlined(true);
        if(componentStyle.strikethrough()) result.withStrikethrough(true);
        if(componentStyle.bold()) result.withBold(true);
        if(componentStyle.italic()) result.withItalic(true);
        return result;
    }
}
