package com.xkball.xklib.ui.render;

import java.util.List;

public record TranslatableComponent(String key, ComponentStyle style, List<Object> args) implements IComponent {

    public TranslatableComponent(String key, ComponentStyle style){
        this(key,style,List.of());
    }
    
    public TranslatableComponent(String key, ComponentStyle style, Object[] args){
        this(key,style,List.of(args));
    }
    
    @Override
    public ComponentStyle style() {
        return style;
    }

    @Override
    public IComponent withStyle(ComponentStyle newStyle) {
        return new TranslatableComponent(key, newStyle);
    }

    @Override
    public void visitStyled(IComponent.StyledVisitor visitor, ComponentStyle parentStyle) {
        visitor.accept(this,key, style.applyParent(parentStyle));
    }

    @Override
    public String visit() {
        return key;
    }
}

