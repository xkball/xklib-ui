package com.xkball.xklib.ui.render;

public record ComponentStyle(
    Integer color,
    boolean strikethrough,
    boolean baseline,
    boolean bold,
    boolean italic
) {
    public static final ComponentStyle EMPTY = new ComponentStyle(null, false, false, false, false);

    public ComponentStyle withColor(int color) {
        return new ComponentStyle(color, strikethrough, baseline, bold, italic);
    }

    public ComponentStyle withStrikethrough(boolean strikethrough) {
        return new ComponentStyle(color, strikethrough, baseline, bold, italic);
    }

    public ComponentStyle withBaselineOffset(boolean baseline) {
        return new ComponentStyle(color, strikethrough, baseline, bold, italic);
    }

    public ComponentStyle applyParent(ComponentStyle parent) {
        return new ComponentStyle(
            color != null ? color : parent.color,
            strikethrough,
            baseline,
            bold,
            italic
        );
    }
}

