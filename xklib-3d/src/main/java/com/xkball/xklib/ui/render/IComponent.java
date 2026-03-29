package com.xkball.xklib.ui.render;

import com.xkball.xklib.annotation.NoImplInMinecraft;
import com.xkball.xklib.resource.ResourceLocation;

import java.util.List;

public interface IComponent {

    static IComponent literal(String text) {
        return new LiteralComponent(text, ComponentStyle.EMPTY);
    }

    static IComponent translatable(String key) {
        return new TranslatableComponent(key, ComponentStyle.EMPTY);
    }

    static IComponent sequence(IComponent... parts) {
        return new SequenceComponent(List.of(parts), ComponentStyle.EMPTY);
    }

    static IComponent sequence(List<IComponent> parts) {
        return new SequenceComponent(List.copyOf(parts), ComponentStyle.EMPTY);
    }
    
    static IComponent icon(ResourceLocation icon) {
        return new IconComponent(icon, ComponentStyle.EMPTY);
    }

    @NoImplInMinecraft
    default ComponentStyle style() {
        return ComponentStyle.EMPTY;
    }

    @NoImplInMinecraft
    default IComponent withStyle(ComponentStyle style) {
        return this;
    }

    @NoImplInMinecraft
    default IComponent withColor(int color) {
        return withStyle(style().withColor(color));
    }

    @NoImplInMinecraft
    default IComponent withStrikethrough(boolean strikethrough) {
        return withStyle(style().withStrikethrough(strikethrough));
    }

    @NoImplInMinecraft
    default IComponent withBaseline(boolean baseline) {
        return withStyle(style().withBaselineOffset(baseline));
    }

    @NoImplInMinecraft
    default IComponent append(IComponent other) {
        return new SequenceComponent(List.of(this, other), ComponentStyle.EMPTY);
    }

    @NoImplInMinecraft
    default void visitStyled(StyledVisitor visitor, ComponentStyle parentStyle) {
        visitor.accept(this,visit(), parentStyle);
    }
    
    default int extraWidth(){
        return 0;
    }

    @NoImplInMinecraft
    String visit();

    @FunctionalInterface
    interface StyledVisitor {
        void accept(IComponent self, String text, ComponentStyle style);
    }
}
