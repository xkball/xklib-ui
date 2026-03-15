package com.xkball.xklib.ui.render;

record LiteralComponent(String text, ComponentStyle style) implements IComponent {

    @Override
    public ComponentStyle style() {
        return style;
    }

    @Override
    public IComponent withStyle(ComponentStyle newStyle) {
        return new LiteralComponent(text, newStyle);
    }

    @Override
    public void visitStyled(IComponent.StyledVisitor visitor, ComponentStyle parentStyle) {
        visitor.accept(text, style.applyParent(parentStyle));
    }

    @Override
    public String visit() {
        return text;
    }
}

