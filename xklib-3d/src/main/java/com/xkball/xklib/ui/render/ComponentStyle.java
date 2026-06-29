package com.xkball.xklib.ui.render;

import java.util.function.Supplier;

public record ComponentStyle(
    Integer color,
    boolean strikethrough,
    boolean baseline,
    boolean bold,
    boolean italic,
    //todo 移动IGUIWidget到x3d包
    Supplier<Object> tooltip,
    Runnable clickEvent
) {
    public static final ComponentStyle EMPTY = new ComponentStyle(null, false, false, false, false, null, null);

    public ComponentStyle withColor(int color) {
        return new ComponentStyle(color, strikethrough, baseline, bold, italic, tooltip, clickEvent);
    }

    public ComponentStyle withStrikethrough(boolean strikethrough) {
        return new ComponentStyle(color, strikethrough, baseline, bold, italic, tooltip, clickEvent);
    }

    public ComponentStyle withBaselineOffset(boolean baseline) {
        return new ComponentStyle(color, strikethrough, baseline, bold, italic,  tooltip, clickEvent);
    }
    
    public ComponentStyle withTooltip(Supplier<Object> tooltip) {
        return new ComponentStyle(color, strikethrough, baseline, bold, italic, tooltip,  clickEvent);
    }
    
    public ComponentStyle withClickEvent(Runnable clickEvent) {
        return new ComponentStyle(color, strikethrough, baseline, bold, italic, tooltip, clickEvent);
    }

    public ComponentStyle applyParent(ComponentStyle parent) {
        return new ComponentStyle(color != null ? color : parent.color, strikethrough, baseline, bold, italic, tooltip, clickEvent);
    }
}

