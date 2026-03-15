package com.xkball.xklib.ui.render;

record TranslatableComponent(String key, ComponentStyle style) implements IComponent {

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
        visitor.accept(key, style.applyParent(parentStyle));
    }

    @Override
    public String visit() {
        return key;
    }
}

