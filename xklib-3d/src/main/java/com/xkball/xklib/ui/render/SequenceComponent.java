package com.xkball.xklib.ui.render;

import java.util.ArrayList;
import java.util.List;

public record SequenceComponent(List<IComponent> parts, ComponentStyle style) implements IComponent {

    @Override
    public ComponentStyle style() {
        return style;
    }

    @Override
    public IComponent withStyle(ComponentStyle newStyle) {
        return new SequenceComponent(parts, newStyle);
    }

    @Override
    public void visitStyled(IComponent.StyledVisitor visitor, ComponentStyle parentStyle) {
        ComponentStyle merged = style.applyParent(parentStyle);
        for (IComponent part : parts) {
            part.visitStyled(visitor, merged);
        }
    }

    @Override
    public String visit() {
        StringBuilder sb = new StringBuilder();
        for (IComponent part : parts) {
            sb.append(part.visit());
        }
        return sb.toString();
    }

    @Override
    public IComponent append(IComponent other) {
        List<IComponent> newParts = new ArrayList<>(parts);
        newParts.add(other);
        return new SequenceComponent(List.copyOf(newParts), style);
    }
}

