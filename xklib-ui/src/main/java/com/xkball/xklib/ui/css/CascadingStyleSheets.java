package com.xkball.xklib.ui.css;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.css.selector.AnySelector;
import com.xkball.xklib.ui.css.selector.UniversalSelector;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CascadingStyleSheets {

    private static final Comparator<StyleSheetUnit> APPLY_ORDER = Comparator
            .comparingInt(StyleSheetUnit::weight)
            .thenComparingInt(StyleSheetUnit::sourceOrder);

    protected final List<StyleSheetUnit> sheets = new ArrayList<>();

    public void add(StyleSheetUnit unit) {
        this.sheets.add(unit);
    }

    public List<StyleSheetUnit> sheets() {
        return this.sheets;
    }
    
    public static class Inline extends CascadingStyleSheets{
        
        private StyleSheetUnit style = new StyleSheetUnit(1000,0,new UniversalSelector(),List.of());
        
        public Inline(){
            this.add(style);
        }
        
        public void addProperties(List<IStyleProperty<?>> properties) {
            this.sheets.clear();
            var list = new ArrayList<>(this.style.properties());
            list.addAll(properties);
            this.style = new StyleSheetUnit(1000,0,new UniversalSelector(),list);
            this.add(style);
        }
    }
    
    public static class SimpleStyleSheet implements IStyleSheet{
        
        private final Map<String,IStyleProperty<?>> style = new LinkedHashMap<>();
        private List<IStyleProperty<?>> renderable = List.of();
        private List<StyleSheetUnit> staticMatched = List.of();
        private List<StyleSheetUnit> dynamicMatched = List.of();
        private List<IStyleProperty<?>> activeDynamicProperties = List.of();
        private int dynamicMatchedCount = -1;
        
        @Override
        public void update(CascadingStyleSheets sheets, IGuiWidget widget) {
            var newStaticMatched = this.staticMatched;
            var staticChanged = false;
            if (widget.isDirty()) {
                newStaticMatched = collectMatched(sheets, widget, false);
                staticChanged = !newStaticMatched.equals(this.staticMatched);
            }

            var newDynamicMatched = collectMatched(sheets, widget, true);
            var dynamicChanged = !newDynamicMatched.equals(this.dynamicMatched);
            this.dynamicMatchedCount = newDynamicMatched.size();

            if (staticChanged || dynamicChanged || this.style.isEmpty()) {
                this.staticMatched = newStaticMatched;
                this.dynamicMatched = newDynamicMatched;
                if (rebuild(widget, this.staticMatched, this.dynamicMatched)) {
                    widget.onStyleSheetChanged();
                }
                return;
            }

            this.dynamicMatched = newDynamicMatched;
            applyDynamic(widget);
        }
        
        @Override
        public @Nullable IStyleProperty<?> getProperty(String key) {
            return this.style.get(key);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <T> T getValue(String key) {
            return (T) this.style.get(key).value();
        }
        
        @Override
        public List<IStyleProperty<?>> renderableProperty() {
            return this.renderable;
        }

        public int dynamicMatchedCount() {
            return this.dynamicMatchedCount;
        }

        public List<StyleSheetUnit> matchedUnits() {
            var ordered = new ArrayList<StyleSheetUnit>(this.staticMatched.size() + this.dynamicMatched.size());
            ordered.addAll(this.staticMatched);
            ordered.addAll(this.dynamicMatched);
            ordered.sort(APPLY_ORDER);
            return List.copyOf(ordered);
        }

        private static List<StyleSheetUnit> collectMatched(CascadingStyleSheets root, IGuiWidget widget, boolean dynamic) {
            var unitList = new ArrayList<StyleSheetUnit>();
            for (var unit : root.sheets) {
                if (unit.selector().isDynamic() == dynamic && unit.selector().match(widget)) {
                    unitList.add(unit);
                }
            }
            var selfList = widget.getStyleSheetAsSelf().sheets;
            for (int i = 0; i < selfList.size(); i++) {
                var self = selfList.get(i);
                if (self.selector().isDynamic() == dynamic && self.selector().match(widget)) {
                    unitList.add(self.withOrder(-i - 1));
                }
            }
            return unitList;
        }

        private boolean rebuild(IGuiWidget widget, List<StyleSheetUnit> staticUnits, List<StyleSheetUnit> dynamicUnits) {
            var oldValues = snapshotStyleValues(this.style);
            var ordered = new ArrayList<StyleSheetUnit>(staticUnits.size() + dynamicUnits.size());
            ordered.addAll(staticUnits);
            ordered.addAll(dynamicUnits);
            ordered.sort(APPLY_ORDER);

            var dynamicPropertySet = Collections.newSetFromMap(new IdentityHashMap<IStyleProperty<?>, Boolean>());
            for (var unit : dynamicUnits) {
                dynamicPropertySet.addAll(unit.properties());
            }

            this.style.clear();
            for (var unit : ordered) {
                for (var property : unit.properties()) {
                    this.style.put(property.propertyName(), property);
                }
            }
            this.renderable = this.style.values().stream().filter(IStyleProperty::renderable).toList();
            var currentDynamicProperties = new ArrayList<IStyleProperty<?>>();
            for (var property : this.style.values()) {
                property.apply(this, widget);
                if (dynamicPropertySet.contains(property)) {
                    currentDynamicProperties.add(property);
                }
            }
            this.activeDynamicProperties = List.copyOf(currentDynamicProperties);
            return !oldValues.equals(snapshotStyleValues(this.style));
        }

        private static Map<String, Object> snapshotStyleValues(Map<String, IStyleProperty<?>> styleMap) {
            var values = new LinkedHashMap<String, Object>(styleMap.size());
            for (var entry : styleMap.entrySet()) {
                values.put(entry.getKey(), entry.getValue().value());
            }
            return values;
        }

        private void applyDynamic(IGuiWidget widget) {
            for (var property : this.activeDynamicProperties) {
                property.apply(this, widget);
            }
        }
    }

}
