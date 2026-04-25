package com.xkball.xklib.antlr.css;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IPropertyFactory;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.ITextDisplayWidget;
import com.xkball.xklib.ui.css.property.BackgroundColorProperty;
import com.xkball.xklib.ui.css.property.BorderColorProperty;
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
import com.xkball.xklib.ui.widget.CheckBox;
import com.xkball.xklib.ui.widget.DragBox;
import com.xkball.xklib.ui.widget.IconCheckBox;
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
import dev.vfyjxf.taffy.style.GridPlacement;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CssStyles {

    public static final Predicate<IGuiWidget> ANY_WIDGET = w -> true;
    
    public static final CssStyles INSTANCE = new CssStyles();
    private static final Logger LOGGER = LoggerFactory.getLogger(CssStyles.class);
    
    public final Map<String, StyleData<?>> styleMap = new HashMap<>();
    
    private CssStyles(){
        this.register("background-color", BackgroundColorProperty::new, Integer.class);
        this.register("button-shape", ButtonShapeProperty::new, String.class);
        this.registerWidgetInt("button-bg-color", IGuiWidget.class, (_, _) -> {});
        this.register("button-hover-color", ButtonHoverProperty::new, Integer.class);

        this.registerField("display", TaffyDisplay.class, (s, v) -> s.display = v, TaffyDisplay.DEFAULT);
        this.registerField("direction", TaffyDirection.class, (s, v) -> s.direction = v, TaffyDirection.INHERIT);
        this.registerField("item-is-table", Boolean.class, (s, v) -> s.itemIsTable = v, false);
        this.registerField("item-is-replaced", Boolean.class, (s, v) -> s.itemIsReplaced = v, false);
        this.registerField("box-sizing", BoxSizing.class, (s, v) -> s.boxSizing = v, BoxSizing.BORDER_BOX);
        this.registerField("overflow", CssOverflow.class, (s, v) -> s.overflow = new TaffyPoint<>(v.x(), v.y()), new TaffyPoint<>(Overflow.VISIBLE, Overflow.VISIBLE));
        this.registerField("overflow-x", Overflow.class, (s, v) -> s.overflow = new TaffyPoint<>(v, s.overflow.y), Overflow.VISIBLE);
        this.registerField("overflow-y", Overflow.class, (s, v) -> s.overflow = new TaffyPoint<>(s.overflow.x, v), Overflow.VISIBLE);

        this.register("width", WidthProperty::new, CssLengthUnit.class);
        this.register("height", HeightProperty::new, CssLengthUnit.class);
        this.register("size", SizeProperty::new, CssSize.class);
        this.register("scrollbar-width", ScrollbarWidthProperty::new, Float.class);

        this.registerWidgetInt("text-color", ITextDisplayWidget.class, ITextDisplayWidget::setTextColor);
        this.registerWidgetStyle("text-scale", ITextDisplayWidget.class, TextScale.class, ITextDisplayWidget::setTextScale);
        this.registerWidgetStyle("text-height", ITextDisplayWidget.class, CssLengthUnit.class,ITextDisplayWidget::setLineHeight);
        this.registerWidgetBoolean("text-drop-shadow", ITextDisplayWidget.class, ITextDisplayWidget::setDropShadow);
        this.registerWidgetStyle("text-extra-width", ITextDisplayWidget.class, CssLengthUnit.class, ITextDisplayWidget::setExtraWidth);
        this.registerWidgetInt("checkbox-track-color", CheckBox.class, CheckBox::setTrackColor, 0xFFCBD5E1);
        this.registerWidgetInt("checkbox-thumb-color", CheckBox.class, CheckBox::setThumbColor, 0xFFFFFFFF);
        this.registerWidgetInt("checkbox-on-color", CheckBox.class, CheckBox::setOnOverlayColor, 0x8022C55E);

        this.registerWidgetInt("iconcheckbox-bg-color", IconCheckBox.class, IconCheckBox::setBackgroundColor, 0);
        this.registerWidgetInt("dragbox-track-color", DragBox.class, DragBox::setTrackColor);
        this.registerWidgetInt("dragbox-thumb-color", DragBox.class, DragBox::setThumbColor);
        this.registerWidgetInt("dragbox-thumb-hover-color", DragBox.class, DragBox::setThumbHoverColor);
        this.registerWidgetInt("dragbox-border-color", DragBox.class, DragBox::setBorderColor);
        this.registerWidgetInt("linegraph-axis-color", LineGraph.class, (w, c) -> w.axisColor = c);
        this.registerWidgetInt("linegraph-num-color", LineGraph.class, (w, c) -> w.numColor = c);
        this.registerWidgetInt("linegraph-line-color", LineGraph.class, (w, c) -> w.lineColor = c);
        this.registerWidgetInt("linegraph-bg-color", LineGraph.class, (w, c) -> w.bgColor = c);
        this.registerWidgetInt("linegraph-grid-color", LineGraph.class, (w, c) -> w.gridColor = c);
        this.registerWidgetInt("textedit-text-color", TextEdit.class, TextEdit::setTextColor, 0xFFFFFFFF);
        this.registerWidgetInt("textedit-selection-color", TextEdit.class, TextEdit::setSelectionColor, 0x800080FF);
        this.registerWidgetInt("textedit-cursor-color", TextEdit.class, TextEdit::setCursorColor, 0xFFFFFFFF);
        this.registerWidgetInt("split-bar-color", SplitContainer.class, SplitContainer::setBarColor, 0xFF444444);
        this.registerWidgetInt("split-bar-hover-color", SplitContainer.class, SplitContainer::setBarHoverColor, 0xFF888888);
        this.registerWidgetInt("container-scrollbar-track-color", ContainerWidget.class, ContainerWidget::setScrollBarTrackColor, 0xFF2D2D2D);
        this.registerWidgetInt("container-scrollbar-thumb-color", ContainerWidget.class, ContainerWidget::setScrollBarThumbColor, 0xFF888888);
        this.registerWidgetInt("container-scrollbar-thumb-hover-color", ContainerWidget.class, ContainerWidget::setScrollBarThumbHoverColor, 0xFFAAAAAA);
        this.registerWidgetBoolean("scalable-grid-enabled", ScalableContainer.class, ScalableContainer::setGridEnabled, false);
        this.registerWidgetInt("scalable-grid-color", ScalableContainer.class, ScalableContainer::setGridColor, 0x404B5563);

        this.registerField("position", TaffyPosition.class, (s, v) -> s.position = v, TaffyPosition.RELATIVE);
        this.registerField("inset", CssRect.class, (s, v) -> s.inset = toLengthPercentageAutoRect(v), TaffyRect.all(LengthPercentageAuto.AUTO));
        this.registerField("left", CssLengthUnit.class, (s, v) -> s.inset.left = v.toLengthPercentageAuto(), LengthPercentageAuto.AUTO);
        this.registerField("right", CssLengthUnit.class, (s, v) -> s.inset.right = v.toLengthPercentageAuto(), LengthPercentageAuto.AUTO);
        this.registerField("top", CssLengthUnit.class, (s, v) -> s.inset.top = v.toLengthPercentageAuto(), LengthPercentageAuto.AUTO);
        this.registerField("bottom", CssLengthUnit.class, (s, v) -> s.inset.bottom = v.toLengthPercentageAuto(), LengthPercentageAuto.AUTO);

        this.registerField("min-size", CssSize.class, (s, v) -> s.minSize = v.toDimension(), TaffySize.all(TaffyDimension.AUTO));
        this.registerField("min-width", CssLengthUnit.class, (s, v) -> s.minSize = TaffySize.of(v.toDimension(), s.minSize.height), TaffyDimension.AUTO);
        this.registerField("min-height", CssLengthUnit.class, (s, v) -> s.minSize = TaffySize.of(s.minSize.width, v.toDimension()), TaffyDimension.AUTO);
        this.registerField("max-size", CssSize.class, (s, v) -> s.maxSize = v.toDimension(), TaffySize.all(TaffyDimension.AUTO));
        this.registerField("max-width", CssLengthUnit.class, (s, v) -> s.maxSize = TaffySize.of(v.toDimension(), s.maxSize.height), TaffyDimension.AUTO);
        this.registerField("max-height", CssLengthUnit.class, (s, v) -> s.maxSize = TaffySize.of(s.maxSize.width, v.toDimension()), TaffyDimension.AUTO);
        this.registerField("aspect-ratio", Float.class, (s, v) -> s.aspectRatio = v, Float.NaN);

        this.registerField("margin", CssRect.class, (s, v) -> s.margin = toLengthPercentageAutoRect(v), TaffyRect.all(LengthPercentageAuto.ZERO));
        this.registerField("margin-left", CssLengthUnit.class, (s, v) -> s.margin.left = v.toLengthPercentageAuto(), LengthPercentageAuto.ZERO);
        this.registerField("margin-right", CssLengthUnit.class, (s, v) -> s.margin.right = v.toLengthPercentageAuto(), LengthPercentageAuto.ZERO);
        this.registerField("margin-top", CssLengthUnit.class, (s, v) -> s.margin.top = v.toLengthPercentageAuto(), LengthPercentageAuto.ZERO);
        this.registerField("margin-bottom", CssLengthUnit.class, (s, v) -> s.margin.bottom = v.toLengthPercentageAuto(), LengthPercentageAuto.ZERO);

        this.registerField("padding", CssRect.class, (s, v) -> s.padding = toLengthPercentageRect(v), TaffyRect.all(LengthPercentage.ZERO));
        this.registerField("padding-left", CssLengthUnit.class, (s, v) -> s.padding.left = v.toLengthPercentage(), LengthPercentage.ZERO);
        this.registerField("padding-right", CssLengthUnit.class, (s, v) -> s.padding.right = v.toLengthPercentage(), LengthPercentage.ZERO);
        this.registerField("padding-top", CssLengthUnit.class, (s, v) -> s.padding.top = v.toLengthPercentage(), LengthPercentage.ZERO);
        this.registerField("padding-bottom", CssLengthUnit.class, (s, v) -> s.padding.bottom = v.toLengthPercentage(), LengthPercentage.ZERO);

        this.registerField("border", CssRect.class, (s, v) -> s.border = toLengthPercentageRect(v), TaffyRect.all(LengthPercentage.ZERO));
        this.registerField("border-left", CssLengthUnit.class, (s, v) -> s.border.left = v.toLengthPercentage(), LengthPercentage.ZERO);
        this.registerField("border-right", CssLengthUnit.class, (s, v) -> s.border.right = v.toLengthPercentage(), LengthPercentage.ZERO);
        this.registerField("border-top", CssLengthUnit.class, (s, v) -> s.border.top = v.toLengthPercentage(), LengthPercentage.ZERO);
        this.registerField("border-bottom", CssLengthUnit.class, (s, v) -> s.border.bottom = v.toLengthPercentage(), LengthPercentage.ZERO);

        this.register("border-color", BorderColorProperty::new, Integer.class);

        this.registerField("align-items", AlignItems.class, (s, v) -> s.alignItems = v, AlignItems.AUTO);
        this.registerField("align-self", AlignItems.class, (s, v) -> s.alignSelf = v, AlignItems.AUTO);
        this.registerField("justify-items", AlignItems.class, (s, v) -> s.justifyItems = v, AlignItems.AUTO);
        this.registerField("justify-self", AlignItems.class, (s, v) -> s.justifySelf = v, AlignItems.AUTO);
        this.registerField("align-content", AlignContent.class, (s, v) -> s.alignContent = v, AlignContent.AUTO);
        this.registerField("justify-content", AlignContent.class, (s, v) -> s.justifyContent = v, AlignContent.AUTO);
        this.registerField("gap", CssSize.class, (s, v) -> s.gap = toLengthPercentageSize(v), TaffySize.all(LengthPercentage.ZERO));
        this.registerField("row-gap", CssLengthUnit.class, (s, v) -> s.gap = TaffySize.of(s.gap.width, v.toLengthPercentage()), LengthPercentage.ZERO);
        this.registerField("column-gap", CssLengthUnit.class, (s, v) -> s.gap = TaffySize.of(v.toLengthPercentage(), s.gap.height), LengthPercentage.ZERO);

        this.registerField("text-align", TextAlign.class, (s, v) -> s.textAlign = v, TextAlign.AUTO);

        this.registerField("flex-direction", FlexDirection.class, (s, v) -> s.flexDirection = v, FlexDirection.ROW);
        this.registerField("flex-wrap", FlexWrap.class, (s, v) -> s.flexWrap = v, FlexWrap.NO_WRAP);
        this.registerField("flex", Float.class, (s, v) -> s.flex = v, Float.NaN);
        this.registerField("flex-grow", Float.class, (s, v) -> s.flexGrow = v, 0.0f);
        this.registerField("flex-shrink", Float.class, (s, v) -> s.flexShrink = v, 1.0f);
        this.registerField("flex-basis", TaffyDimension.class, (s, v) -> s.flexBasis = v, TaffyDimension.AUTO);

        this.registerField("grid-template-rows", CssTrackList.class, (s, v) -> s.gridTemplateRows = List.copyOf(v.get()), List.of());
        this.registerField("grid-template-columns", CssTrackList.class, (s, v) -> s.gridTemplateColumns = List.copyOf(v.get()), List.of());
        this.registerField("grid-auto-rows", CssTrackList.class, (s, v) -> s.gridAutoRows = List.copyOf(v.get()), List.of());
        this.registerField("grid-auto-columns", CssTrackList.class, (s, v) -> s.gridAutoColumns = List.copyOf(v.get()), List.of());
        this.registerField("grid-auto-flow", GridAutoFlow.class, (s, v) -> s.gridAutoFlow = v, GridAutoFlow.ROW);
        this.registerField("grid-row", CssGridLine.class, (s, v) -> s.gridRow = new TaffyLine<>(v.start(), v.end()), new TaffyLine<>(GridPlacement.AUTO_INSTANCE, GridPlacement.AUTO_INSTANCE));
        this.registerField("grid-column", CssGridLine.class, (s, v) -> s.gridColumn = new TaffyLine<>(v.start(), v.end()), new TaffyLine<>(GridPlacement.AUTO_INSTANCE, GridPlacement.AUTO_INSTANCE));
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

    private <T> void registerField(String name, Class<T> clazz, BiConsumer<TaffyStyle, T> setter, @Nullable Object defaultValue) {
        this.register(name, value -> new FunctionalStyleProperty<>(name, value, setter), clazz, ANY_WIDGET, defaultValue);
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
    
    public <T> void register(String name, Function<T, IStyleProperty<T>> factory, Class<T> clazz) {
        this.register(name, factory, clazz, ANY_WIDGET, null);
    }

    public <T> void register(String name, Function<T, IStyleProperty<T>> factory, Class<T> clazz, Predicate<IGuiWidget> widgetFilter) {
        this.register(name, factory, clazz, widgetFilter, null);
    }

    public <T> void register(String name, Function<T, IStyleProperty<T>> factory, Class<T> clazz, Predicate<IGuiWidget> widgetFilter, @Nullable Object defaultValue) {
        Set<String> enumValues = clazz.isEnum()
                ? Arrays.stream(clazz.getEnumConstants()).map(e -> ((Enum<?>) e).name()).collect(java.util.stream.Collectors.toUnmodifiableSet())
                : null;
        this.styleMap.put(name, new StyleData<>(name, factory, clazz, widgetFilter, defaultValue, enumValues));
    }

    public <W> void registerWidgetInt(String name, Class<W> widgetType, BiConsumer<W, Integer> setter) {
        this.registerWidgetInt(name, widgetType, setter, null);
    }

    public <W> void registerWidgetInt(String name, Class<W> widgetType, BiConsumer<W, Integer> setter, @Nullable Object defaultValue) {
        this.register(name, v -> new WidgetIntStyleProperty<>(name, v, widgetType, setter), Integer.class, widgetType::isInstance, defaultValue);
    }

    public <W> void registerWidgetBoolean(String name, Class<W> widgetType, BiConsumer<W, Boolean> setter) {
        this.registerWidgetBoolean(name, widgetType, setter, null);
    }

    public <W> void registerWidgetBoolean(String name, Class<W> widgetType, BiConsumer<W, Boolean> setter, @Nullable Object defaultValue) {
        this.register(name, v -> new WidgetBooleanStyleProperty<>(name, v, widgetType, setter), Boolean.class, widgetType::isInstance, defaultValue);
    }

    public <W, V> void registerWidgetStyle(String name, Class<W> widgetType, Class<V> valueClass, BiConsumer<W, V> setter) {
        this.register(name, v -> new WidgetStyleProperty<>(name, v, widgetType, setter), valueClass, widgetType::isInstance, null);
    }
    
    public record StyleData<T>(
            String name,
            Function<T, IStyleProperty<T>> factory,
            Class<T> clazz,
            Predicate<IGuiWidget> widgetFilter,
            @Nullable Object defaultValue,
            @Nullable Set<String> enumValues) {}
    
}
