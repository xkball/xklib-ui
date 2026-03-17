package com.xkball.xklib.ui.css.property.value;

import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.TaffyDimension;
import org.jspecify.annotations.NonNull;

public record CssSize(CssLengthUnit w, CssLengthUnit h) {
    
    @Override
    public @NonNull String toString() {
        return w.toString() + " " + h.toString();
    }
    
    public TaffySize<TaffyDimension> toDimension(){
        return TaffySize.of(w.toDimension(),h.toDimension());
    }
}
