package com.xkball.xklib.ui.render;

import com.xkball.xklib.resource.ResourceLocation;

public record IconComponent(ResourceLocation icon, ComponentStyle style) implements IComponent {

    @Override
    public ComponentStyle style() {
        return style;
    }

    @Override
    public IComponent withStyle(ComponentStyle newStyle) {
        return new IconComponent(icon, newStyle);
    }

    @Override
    public void visitStyled(IComponent.StyledVisitor visitor, ComponentStyle parentStyle) {
        visitor.accept(this,"", style.applyParent(parentStyle));
    }

    @Override
    public String visit() {
        return "";
    }

    public ResourceLocation getIcon() {
        return icon;
    }
}


