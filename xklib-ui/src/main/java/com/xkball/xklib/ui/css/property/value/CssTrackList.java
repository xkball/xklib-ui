package com.xkball.xklib.ui.css.property.value;

import dev.vfyjxf.taffy.style.TrackSizingFunction;

import java.util.List;
import java.util.function.Supplier;

public record CssTrackList(List<Supplier<TrackSizingFunction>> values) {
    
    public List<TrackSizingFunction> get(){
        return values.stream().map(Supplier::get).toList();
    }
}

