package com.xkball.xklib.ui.css;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 并不对应真正的CSS, 而是为设置属性提供方便
 */
public class CascadingStyleSheets implements IStyleSheet {

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
    
    @Override
    public @Nullable IStyleProperty<?> getProperty(String key) {
        return null;
    }
}
