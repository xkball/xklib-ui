package com.xkball.xklib.ui.css;

import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CascadingStyleSheets {

    private static final Comparator<StyleSheetUnit> APPLY_ORDER = Comparator
            .comparingInt(StyleSheetUnit::weight)
            .thenComparingInt(StyleSheetUnit::sourceOrder);

    private final List<StyleSheetUnit> sheets = new ArrayList<>();

    public void add(StyleSheetUnit unit) {
        this.sheets.add(unit);
    }

    public List<StyleSheetUnit> sheets() {
        return List.copyOf(this.sheets);
    }
    
}
