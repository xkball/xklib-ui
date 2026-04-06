package com.xkball.xklib.antlr.css;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.ui.css.property.BackgroundColorProperty;
import com.xkball.xklib.ui.css.property.ButtonHoverProperty;
import com.xkball.xklib.ui.css.property.ButtonShapeProperty;
import com.xkball.xklib.ui.css.property.FunctionalStyleProperty;
import com.xkball.xklib.ui.css.property.HeightProperty;
import com.xkball.xklib.ui.css.property.ScrollbarWidthProperty;
import com.xkball.xklib.ui.css.property.SizeProperty;
import com.xkball.xklib.ui.css.property.WidgetBooleanStyleProperty;
import com.xkball.xklib.ui.css.property.WidgetIntStyleProperty;
import com.xkball.xklib.ui.css.property.WidgetStyleProperty;
import com.xkball.xklib.ui.css.property.WidthProperty;
import com.xkball.xklib.ui.css.property.value.CssGridLine;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.css.property.value.CssOverflow;
import com.xkball.xklib.ui.css.property.value.CssRect;
import com.xkball.xklib.ui.css.property.value.CssSize;
import com.xkball.xklib.ui.css.property.value.CssTrackList;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.CheckBox;
import com.xkball.xklib.ui.widget.DragBox;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.LineGraph;
import com.xkball.xklib.ui.widget.TextEdit;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklib.ui.widget.container.ScalableContainer;
import dev.vfyjxf.taffy.geometry.TaffyLine;
import dev.vfyjxf.taffy.geometry.TaffyPoint;
import dev.vfyjxf.taffy.geometry.TaffyRect;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.BoxSizing;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.FlexWrap;
import dev.vfyjxf.taffy.style.GridAutoFlow;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.LengthPercentageAuto;
import dev.vfyjxf.taffy.style.Overflow;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDirection;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyPosition;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CssStyles {
    
    public static final CssStyles INSTANCE = new CssStyles();
    private static final Logger LOGGER = LoggerFactory.getLogger(CssStyles.class);
    
    public final Map<String, StyleData<?>> styleMap = new HashMap<>();
    
    private CssStyles(){
        this.register(BackgroundColorProperty.NAME, BackgroundColorProperty::new, Integer.class);
        this.register("button-shape", ButtonShapeProperty::new, String.class);
        this.register("button-bg-color", value -> new WidgetIntStyleProperty("button-bg-color", value, (_, _) -> {}), Integer.class);
        this.register("button-hover-color", ButtonHoverProperty::new, Integer.class);

        this.registerField("display", TaffyDisplay.class, (s, v) -> s.display = v);
        this.registerField("direction", TaffyDirection.class, (s, v) -> s.direction = v);
        this.registerField("item-is-table", Boolean.class, (s, v) -> s.itemIsTable = v);
        this.registerField("item-is-replaced", Boolean.class, (s, v) -> s.itemIsReplaced = v);
        this.registerField("box-sizing", BoxSizing.class, (s, v) -> s.boxSizing = v);
        this.registerField("overflow", CssOverflow.class, (s, v) -> s.overflow = new TaffyPoint<>(v.x(), v.y()));
        this.registerField("overflow-x", Overflow.class, (s, v) -> s.overflow = new TaffyPoint<>(v, s.overflow.y));
        this.registerField("overflow-y", Overflow.class, (s, v) -> s.overflow = new TaffyPoint<>(s.overflow.x, v));

        this.register(WidthProperty.NAME,WidthProperty::new, CssLengthUnit.class);
        this.register(HeightProperty.NAME,HeightProperty::new, CssLengthUnit.class);
        this.register(SizeProperty.NAME, SizeProperty::new, CssSize.class);
        this.register(ScrollbarWidthProperty.NAME, ScrollbarWidthProperty::new, Float.class);

        this.register("label-text-color", value -> new WidgetIntStyleProperty("label-text-color", value,
                (widget, color) -> {
                    if (widget instanceof Label label) {
                        label.setColor(color);
                    }
                }), Integer.class);
        this.register("label-text-scale", value -> new WidgetStyleProperty<>("label-text-scale", value,
                (widget, textScale) -> {
                    if (widget instanceof Label label) {
                        label.setTextScale(textScale);
                    }
                }), TextScale.class);
        this.register("button-text-color", value -> new WidgetIntStyleProperty("button-text-color", value,
                (widget, color) -> {
                    if (widget instanceof Button button) {
                        button.setColor(color);
                    }
                }), Integer.class);
        this.register("checkbox-track-color", value -> new WidgetIntStyleProperty("checkbox-track-color", value,
                (widget, color) -> {
                    if (widget instanceof CheckBox checkBox) {
                        checkBox.setTrackColor(color);
                    }
                }), Integer.class);
        this.register("checkbox-thumb-color", value -> new WidgetIntStyleProperty("checkbox-thumb-color", value,
                (widget, color) -> {
                    if (widget instanceof CheckBox checkBox) {
                        checkBox.setThumbColor(color);
                    }
                }), Integer.class);
        this.register("checkbox-on-color", value -> new WidgetIntStyleProperty("checkbox-on-color", value,
                (widget, color) -> {
                    if (widget instanceof CheckBox checkBox) {
                        checkBox.setOnOverlayColor(color);
                    }
                }), Integer.class);
        this.register("dragbox-track-color", value -> new WidgetIntStyleProperty("dragbox-track-color", value,
                (widget, color) -> {
                    if (widget instanceof DragBox dragBox) {
                        dragBox.setTrackColor(color);
                    }
                }), Integer.class);
        this.register("dragbox-thumb-color", value -> new WidgetIntStyleProperty("dragbox-thumb-color", value,
                (widget, color) -> {
                    if (widget instanceof DragBox dragBox) {
                        dragBox.setThumbColor(color);
                    }
                }), Integer.class);
        this.register("dragbox-thumb-hover-color", value -> new WidgetIntStyleProperty("dragbox-thumb-hover-color", value,
                (widget, color) -> {
                    if (widget instanceof DragBox dragBox) {
                        dragBox.setThumbHoverColor(color);
                    }
                }), Integer.class);
        this.register("dragbox-border-color", value -> new WidgetIntStyleProperty("dragbox-border-color", value,
                (widget, color) -> {
                    if (widget instanceof DragBox dragBox) {
                        dragBox.setBorderColor(color);
                    }
                }), Integer.class);
        this.register("linegraph-axis-color", value -> new WidgetIntStyleProperty("linegraph-axis-color", value,
                (widget, color) -> {
                    if (widget instanceof LineGraph lineGraph) {
                        lineGraph.axisColor = color;
                    }
                }), Integer.class);
        this.register("linegraph-num-color", value -> new WidgetIntStyleProperty("linegraph-num-color", value,
                (widget, color) -> {
                    if (widget instanceof LineGraph lineGraph) {
                        lineGraph.numColor = color;
                    }
                }), Integer.class);
        this.register("linegraph-line-color", value -> new WidgetIntStyleProperty("linegraph-line-color", value,
                (widget, color) -> {
                    if (widget instanceof LineGraph lineGraph) {
                        lineGraph.lineColor = color;
                    }
                }), Integer.class);
        this.register("linegraph-bg-color", value -> new WidgetIntStyleProperty("linegraph-bg-color", value,
                (widget, color) -> {
                    if (widget instanceof LineGraph lineGraph) {
                        lineGraph.bgColor = color;
                    }
                }), Integer.class);
        this.register("linegraph-grid-color", value -> new WidgetIntStyleProperty("linegraph-grid-color", value,
                (widget, color) -> {
                    if (widget instanceof LineGraph lineGraph) {
                        lineGraph.gridColor = color;
                    }
                }), Integer.class);
        this.register("textedit-text-color", value -> new WidgetIntStyleProperty("textedit-text-color", value,
                (widget, color) -> {
                    if (widget instanceof TextEdit textEdit) {
                        textEdit.setTextColor(color);
                    }
                }), Integer.class);
        this.register("textedit-selection-color", value -> new WidgetIntStyleProperty("textedit-selection-color", value,
                (widget, color) -> {
                    if (widget instanceof TextEdit textEdit) {
                        textEdit.setSelectionColor(color);
                    }
                }), Integer.class);
        this.register("textedit-cursor-color", value -> new WidgetIntStyleProperty("textedit-cursor-color", value,
                (widget, color) -> {
                    if (widget instanceof TextEdit textEdit) {
                        textEdit.setCursorColor(color);
                    }
                }), Integer.class);
        this.register("split-bar-color", value -> new WidgetIntStyleProperty("split-bar-color", value,
                (widget, color) -> {
                    if (widget instanceof SplitContainer splitContainer) {
                        splitContainer.setBarColor(color);
                    }
                }), Integer.class);
        this.register("split-bar-hover-color", value -> new WidgetIntStyleProperty("split-bar-hover-color", value,
                (widget, color) -> {
                    if (widget instanceof SplitContainer splitContainer) {
                        splitContainer.setBarHoverColor(color);
                    }
                }), Integer.class);
        this.register("container-scrollbar-track-color", value -> new WidgetIntStyleProperty("container-scrollbar-track-color", value,
                (widget, color) -> {
                    if (widget instanceof ContainerWidget containerWidget) {
                        containerWidget.setScrollBarTrackColor(color);
                    }
                }), Integer.class);
        this.register("container-scrollbar-thumb-color", value -> new WidgetIntStyleProperty("container-scrollbar-thumb-color", value,
                (widget, color) -> {
                    if (widget instanceof ContainerWidget containerWidget) {
                        containerWidget.setScrollBarThumbColor(color);
                    }
                }), Integer.class);
        this.register("container-scrollbar-thumb-hover-color", value -> new WidgetIntStyleProperty("container-scrollbar-thumb-hover-color", value,
                (widget, color) -> {
                    if (widget instanceof ContainerWidget containerWidget) {
                        containerWidget.setScrollBarThumbHoverColor(color);
                    }
                }), Integer.class);
        this.register("scalable-grid-enabled", value -> new WidgetBooleanStyleProperty("scalable-grid-enabled", value,
                (widget, enabled) -> {
                    if (widget instanceof ScalableContainer scalableContainer) {
                        scalableContainer.setGridEnabled(enabled);
                    }
                }), Boolean.class);
        this.register("scalable-grid-color", value -> new WidgetIntStyleProperty("scalable-grid-color", value,
                (widget, color) -> {
                    if (widget instanceof ScalableContainer scalableContainer) {
                        scalableContainer.setGridColor(color);
                    }
                }), Integer.class);

        this.registerField("position", TaffyPosition.class, (s, v) -> s.position = v);
        this.registerField("inset", CssRect.class, (s, v) -> s.inset = toLengthPercentageAutoRect(v));
        this.registerField("left", CssLengthUnit.class, (s, v) -> s.inset.left = v.toLengthPercentageAuto());
        this.registerField("right", CssLengthUnit.class, (s, v) -> s.inset.right = v.toLengthPercentageAuto());
        this.registerField("top", CssLengthUnit.class, (s, v) -> s.inset.top = v.toLengthPercentageAuto());
        this.registerField("bottom", CssLengthUnit.class, (s, v) -> s.inset.bottom = v.toLengthPercentageAuto());

        this.registerField("min-size", CssSize.class, (s, v) -> s.minSize = v.toDimension());
        this.registerField("min-width", CssLengthUnit.class, (s, v) -> s.minSize = TaffySize.of(v.toDimension(), s.minSize.height));
        this.registerField("min-height", CssLengthUnit.class, (s, v) -> s.minSize = TaffySize.of(s.minSize.width, v.toDimension()));
        this.registerField("max-size", CssSize.class, (s, v) -> s.maxSize = v.toDimension());
        this.registerField("max-width", CssLengthUnit.class, (s, v) -> s.maxSize = TaffySize.of(v.toDimension(), s.maxSize.height));
        this.registerField("max-height", CssLengthUnit.class, (s, v) -> s.maxSize = TaffySize.of(s.maxSize.width, v.toDimension()));
        this.registerField("aspect-ratio", Float.class, (s, v) -> s.aspectRatio = v);

        this.registerField("margin", CssRect.class, (s, v) -> s.margin = toLengthPercentageAutoRect(v));
        this.registerField("margin-left", CssLengthUnit.class, (s, v) -> s.margin.left = v.toLengthPercentageAuto());
        this.registerField("margin-right", CssLengthUnit.class, (s, v) -> s.margin.right = v.toLengthPercentageAuto());
        this.registerField("margin-top", CssLengthUnit.class, (s, v) -> s.margin.top = v.toLengthPercentageAuto());
        this.registerField("margin-bottom", CssLengthUnit.class, (s, v) -> s.margin.bottom = v.toLengthPercentageAuto());

        this.registerField("padding", CssRect.class, (s, v) -> s.padding = toLengthPercentageRect(v));
        this.registerField("padding-left", CssLengthUnit.class, (s, v) -> s.padding.left = v.toLengthPercentage());
        this.registerField("padding-right", CssLengthUnit.class, (s, v) -> s.padding.right = v.toLengthPercentage());
        this.registerField("padding-top", CssLengthUnit.class, (s, v) -> s.padding.top = v.toLengthPercentage());
        this.registerField("padding-bottom", CssLengthUnit.class, (s, v) -> s.padding.bottom = v.toLengthPercentage());

        this.registerField("border", CssRect.class, (s, v) -> s.border = toLengthPercentageRect(v));
        this.registerField("border-left", CssLengthUnit.class, (s, v) -> s.border.left = v.toLengthPercentage());
        this.registerField("border-right", CssLengthUnit.class, (s, v) -> s.border.right = v.toLengthPercentage());
        this.registerField("border-top", CssLengthUnit.class, (s, v) -> s.border.top = v.toLengthPercentage());
        this.registerField("border-bottom", CssLengthUnit.class, (s, v) -> s.border.bottom = v.toLengthPercentage());

        this.registerField("align-items", AlignItems.class, (s, v) -> s.alignItems = v);
        this.registerField("align-self", AlignItems.class, (s, v) -> s.alignSelf = v);
        this.registerField("justify-items", AlignItems.class, (s, v) -> s.justifyItems = v);
        this.registerField("justify-self", AlignItems.class, (s, v) -> s.justifySelf = v);
        this.registerField("align-content", AlignContent.class, (s, v) -> s.alignContent = v);
        this.registerField("justify-content", AlignContent.class, (s, v) -> s.justifyContent = v);
        this.registerField("gap", CssSize.class, (s, v) -> s.gap = toLengthPercentageSize(v));
        this.registerField("row-gap", CssLengthUnit.class, (s, v) -> s.gap = TaffySize.of(s.gap.width, v.toLengthPercentage()));
        this.registerField("column-gap", CssLengthUnit.class, (s, v) -> s.gap = TaffySize.of(v.toLengthPercentage(), s.gap.height));

        this.registerField("text-align", TextAlign.class, (s, v) -> s.textAlign = v);

        this.registerField("flex-direction", FlexDirection.class, (s, v) -> s.flexDirection = v);
        this.registerField("flex-wrap", FlexWrap.class, (s, v) -> s.flexWrap = v);
        this.registerField("flex", Float.class, (s, v) -> s.flex = v);
        this.registerField("flex-grow", Float.class, (s, v) -> s.flexGrow = v);
        this.registerField("flex-shrink", Float.class, (s, v) -> s.flexShrink = v);
        this.registerField("flex-basis", TaffyDimension.class, (s, v) -> s.flexBasis = v);

        this.registerField("grid-template-rows", CssTrackList.class, (s, v) -> s.gridTemplateRows = List.copyOf(v.values()));
        this.registerField("grid-template-columns", CssTrackList.class, (s, v) -> s.gridTemplateColumns = List.copyOf(v.values()));
        this.registerField("grid-auto-rows", CssTrackList.class, (s, v) -> s.gridAutoRows = List.copyOf(v.values()));
        this.registerField("grid-auto-columns", CssTrackList.class, (s, v) -> s.gridAutoColumns = List.copyOf(v.values()));
        this.registerField("grid-auto-flow", GridAutoFlow.class, (s, v) -> s.gridAutoFlow = v);
        this.registerField("grid-row", CssGridLine.class, (s, v) -> s.gridRow = new TaffyLine<>(v.start(), v.end()));
        this.registerField("grid-column", CssGridLine.class, (s, v) -> s.gridColumn = new TaffyLine<>(v.start(), v.end()));
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public @Nullable IStyleProperty<?> parse(String name, css3Parser.ExprContext expr){
        try {
            StyleData data = this.styleMap.get(name);
            if(data == null){
                LOGGER.warn("Unknown style: {}:{}", name, expr.getText());
                return null;
            }
            IPropertyFactory factory = PropertyFactories.INSTANCE.get(data.clazz);
            if (factory == null) {
                LOGGER.warn("No parser for style type: {} {}:{}", data.clazz.getName(), name, expr.getText());
                return null;
            }
            Object t = factory.parse(expr);
            return (IStyleProperty<?>) data.factory.apply(t);
        } catch (Exception e){
            LOGGER.warn("Cannot parse style property: {}:{}", name, expr.getText(), e);
        }
        return null;
    }

    private <T> void registerField(String name, Class<T> clazz, BiConsumer<TaffyStyle, T> setter) {
        this.register(name, value -> new FunctionalStyleProperty<>(name, value, setter), clazz);
    }

    private static TaffyRect<LengthPercentageAuto> toLengthPercentageAutoRect(CssRect rect) {
        return TaffyRect.of(
                rect.left().toLengthPercentageAuto(),
                rect.right().toLengthPercentageAuto(),
                rect.top().toLengthPercentageAuto(),
                rect.bottom().toLengthPercentageAuto()
        );
    }

    private static TaffyRect<LengthPercentage> toLengthPercentageRect(CssRect rect) {
        return TaffyRect.of(
                rect.left().toLengthPercentage(),
                rect.right().toLengthPercentage(),
                rect.top().toLengthPercentage(),
                rect.bottom().toLengthPercentage()
        );
    }

    private static TaffySize<LengthPercentage> toLengthPercentageSize(CssSize size) {
        return TaffySize.of(size.w().toLengthPercentage(), size.h().toLengthPercentage());
    }
    
    public <T> void register(String name, Function<T,IStyleProperty<T>> factory, Class<T> clazz){
        this.styleMap.put(name, new StyleData<>(name, factory, clazz));
    }
    
    public record StyleData<T>(String name, Function<T,IStyleProperty<T>> factory, Class<T> clazz){}
    
}
