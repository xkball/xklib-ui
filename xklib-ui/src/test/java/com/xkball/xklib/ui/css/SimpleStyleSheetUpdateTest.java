package com.xkball.xklib.ui.css;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.widget.Widget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class SimpleStyleSheetUpdateTest {

    @Test
    public void shouldUpdateOnlyDynamicSelectorsWhenNotDirty() {
        var root = new CascadingStyleSheets();
        var widget = new TestWidget();
        widget.setCSSClassName("a");
        widget.setHovered(false);
        widget.setDirtyFlag(true);

        var styleSheet = new CascadingStyleSheets.SimpleStyleSheet();
        widget.setStyleSheet(styleSheet);

        var staticSelector = new CountingSelector(w -> "a".equals(w.getCSSClassName()), false, 10);
        var dynamicSelector = new CountingSelector(IGuiWidget::isHovered, true, 10);

        var staticProperty = new CountingProperty("static-prop", 1);
        var dynamicProperty = new CountingProperty("dynamic-prop", 2);

        root.add(new StyleSheetUnit(10, 0, staticSelector, List.of(staticProperty)));
        root.add(new StyleSheetUnit(10, 1, dynamicSelector, List.of(dynamicProperty)));

        styleSheet.update(root, widget);

        Assertions.assertEquals(1, staticSelector.matchCount.get());
        Assertions.assertEquals(1, dynamicSelector.matchCount.get());
        Assertions.assertEquals(1, staticProperty.applyCount.get());
        Assertions.assertEquals(0, dynamicProperty.applyCount.get());
        Assertions.assertEquals(0, styleSheet.dynamicMatchedCount());

        widget.setDirtyFlag(false);
        styleSheet.update(root, widget);

        Assertions.assertEquals(1, staticSelector.matchCount.get());
        Assertions.assertEquals(2, dynamicSelector.matchCount.get());
        Assertions.assertEquals(1, staticProperty.applyCount.get());
        Assertions.assertEquals(0, dynamicProperty.applyCount.get());

        widget.setHovered(true);
        styleSheet.update(root, widget);

        Assertions.assertEquals(1, staticSelector.matchCount.get());
        Assertions.assertEquals(3, dynamicSelector.matchCount.get());
        Assertions.assertEquals(2, staticProperty.applyCount.get());
        Assertions.assertEquals(1, dynamicProperty.applyCount.get());
        Assertions.assertEquals(1, styleSheet.dynamicMatchedCount());

        styleSheet.update(root, widget);

        Assertions.assertEquals(1, staticSelector.matchCount.get());
        Assertions.assertEquals(4, dynamicSelector.matchCount.get());
        Assertions.assertEquals(2, staticProperty.applyCount.get());
        Assertions.assertEquals(2, dynamicProperty.applyCount.get());
        Assertions.assertEquals(1, styleSheet.dynamicMatchedCount());
    }

    @Test
    public void shouldMergeByWeightThenOrder() {
        var root = new CascadingStyleSheets();
        var widget = new TestWidget();
        widget.setDirtyFlag(true);
        var styleSheet = new CascadingStyleSheets.SimpleStyleSheet();
        widget.setStyleSheet(styleSheet);

        var p1 = new CountingProperty("x", 1);
        var p2 = new CountingProperty("x", 2);
        var p3 = new CountingProperty("x", 3);

        root.add(new StyleSheetUnit(10, 0, new CountingSelector(w -> true, false, 1), List.of(p1)));
        root.add(new StyleSheetUnit(10, 1, new CountingSelector(w -> true, false, 1), List.of(p2)));
        root.add(new StyleSheetUnit(20, 0, new CountingSelector(w -> true, false, 1), List.of(p3)));

        styleSheet.update(root, widget);

        Assertions.assertEquals(3, ((Integer) styleSheet.getValue("x")).intValue());
    }

    @Test
    public void shouldRebuildWhenDynamicMatchedUnitsChangedWithSameCount() {
        var root = new CascadingStyleSheets();
        var widget = new TestWidget();
        widget.setDirtyFlag(true);
        widget.setHovered(false);

        var styleSheet = new CascadingStyleSheets.SimpleStyleSheet();
        widget.setStyleSheet(styleSheet);

        var dynamicSelectorA = new CountingSelector(w -> !w.isHovered(), true, 10);
        var dynamicSelectorB = new CountingSelector(IGuiWidget::isHovered, true, 10);
        var propertyA = new CountingProperty("x", 1);
        var propertyB = new CountingProperty("x", 2);

        root.add(new StyleSheetUnit(10, 0, dynamicSelectorA, List.of(propertyA)));
        root.add(new StyleSheetUnit(10, 1, dynamicSelectorB, List.of(propertyB)));

        styleSheet.update(root, widget);

        Assertions.assertEquals(1, styleSheet.dynamicMatchedCount());
        Assertions.assertEquals(1, ((Integer) styleSheet.getValue("x")).intValue());
        Assertions.assertEquals(1, propertyA.applyCount.get());
        Assertions.assertEquals(0, propertyB.applyCount.get());

        widget.setDirtyFlag(false);
        widget.setHovered(true);
        styleSheet.update(root, widget);

        Assertions.assertEquals(1, styleSheet.dynamicMatchedCount());
        Assertions.assertEquals(2, ((Integer) styleSheet.getValue("x")).intValue());
        Assertions.assertEquals(1, propertyA.applyCount.get());
        Assertions.assertEquals(1, propertyB.applyCount.get());
    }

    private static class TestWidget extends Widget {

        private boolean dirtyFlag;

        public void setDirtyFlag(boolean dirtyFlag) {
            this.dirtyFlag = dirtyFlag;
        }

        @Override
        public boolean isDirty() {
            return this.dirtyFlag;
        }
    }

    private static class CountingSelector implements ISelector {

        private final Predicate<IGuiWidget> matcher;
        private final boolean dynamic;
        private final int weight;
        private final AtomicInteger matchCount = new AtomicInteger();

        private CountingSelector(Predicate<IGuiWidget> matcher, boolean dynamic, int weight) {
            this.matcher = matcher;
            this.dynamic = dynamic;
            this.weight = weight;
        }

        @Override
        public boolean match(IGuiWidget widget) {
            this.matchCount.incrementAndGet();
            return this.matcher.test(widget);
        }

        @Override
        public int weight() {
            return this.weight;
        }

        @Override
        public boolean isDynamic() {
            return this.dynamic;
        }
    }

    private static class CountingProperty implements IStyleProperty<Integer> {

        private final String name;
        private final AtomicInteger applyCount = new AtomicInteger();
        private int value;

        private CountingProperty(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String propertyName() {
            return this.name;
        }

        @Override
        public String valueString() {
            return String.valueOf(this.value);
        }

        @Override
        public Integer value() {
            return this.value;
        }

        @Override
        public void setValue(Integer value) {
            this.value = value;
        }

        @Override
        public void apply(IStyleSheet sheet, IGuiWidget widget) {
            this.applyCount.incrementAndGet();
        }
    }
}


