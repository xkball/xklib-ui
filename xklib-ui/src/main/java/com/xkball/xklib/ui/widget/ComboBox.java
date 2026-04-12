package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;

import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.Overflow;
import dev.vfyjxf.taffy.geometry.TaffyPoint;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ComboBox<T> extends ContainerWidget {

    private static final String SELF_CSS = """
            display: flex;
            flex-direction: row;
            align-items: stretch;
            """;

    private static final float ARROW_BTN_WIDTH = 24f;
    private static final int BORDER_COLOR = 0xFF94A3B8;
    private static final int BG_COLOR = 0xFF1E293B;
    private static final int ARROW_BTN_COLOR = 0xFF334155;
    private static final int ARROW_BTN_HOVER_COLOR = 0xFF475569;

    private final List<T> options;
    private final Function<T, String> toDisplay;
    private final boolean nullable;

    private T selected;
    private final Label displayLabel;
    private Consumer<T> onChange;
    private ComboBoxOverlay overlay;

    public ComboBox(List<T> options, Function<T, String> toDisplay, boolean nullable) {
        this.options = options;
        this.toDisplay = toDisplay;
        this.nullable = nullable;
        this.selected = nullable ? null : (options.isEmpty() ? null : options.getFirst());
        this.displayLabel = new Label(selectedText(),0xFFE2E8F0);
        this.inlineStyle(SELF_CSS);
    }

    public ComboBox(List<T> options, Function<T, String> toDisplay) {
        this(options, toDisplay, false);
    }

    private String selectedText() {
        if (selected == null) return "";
        return toDisplay.apply(selected);
    }

    public T getSelected() {
        return selected;
    }

    public void setSelected(T value) {
        this.selected = value;
        this.displayLabel.setText(selectedText());
    }

    public void setOnChange(Consumer<T> onChange) {
        this.onChange = onChange;
    }

    @Override
    public void afterTreeAndNodeSet() {
        super.afterTreeAndNodeSet();


        var labelStyle = new TaffyStyle();
        labelStyle.flexGrow = 1f;
        labelStyle.flexShrink = 1f;
        labelStyle.size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.percent(1f));
        this.displayLabel.inlineStyle("background-color: " + BG_COLOR + ";");
        this.addChild(this.displayLabel, labelStyle);

        var arrowBtn = new ArrowButton();
        arrowBtn.inlineStyle("button-shape: rect; button-bg-color: " + ARROW_BTN_COLOR + "; button-hover-color: " + ARROW_BTN_HOVER_COLOR + ";");
        var arrowStyle = new TaffyStyle();
        arrowStyle.size = new TaffySize<>(TaffyDimension.length(ARROW_BTN_WIDTH), TaffyDimension.percent(1f));
        arrowStyle.flexShrink = 0f;
        this.addChild(arrowBtn, arrowStyle);
    }

    private void openOverlay() {
        if (overlay != null) return;
        overlay = new ComboBoxOverlay();
        var guiSystem = GuiSystem.INSTANCE.get();
        float itemHeight = Math.max(this.height, 24f);
        overlay.asTreeRoot();
        overlay.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.alignItems = AlignItems.STRETCH;
            s.justifyContent = AlignContent.START;
        });
        overlay.inlineStyle("background-color: 0xFF0F172A;");
        
        if (nullable) {
            var nullLabel = new Label("",  0xFF94A3B8);
            nullLabel.inlineStyle("button-hover-color: 0x33FFFFFF;");
            var nullStyle = new TaffyStyle();
            nullStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(itemHeight));
            nullStyle.flexShrink = 0f;
            overlay.addOptionItem(nullLabel, nullStyle, null);
        }

        for (T option : options) {
            var itemLabel = new Label(toDisplay.apply(option), 0xFFE2E8F0);
            itemLabel.inlineStyle("button-hover-color: 0x33FFFFFF;");
            var itemStyle = new TaffyStyle();
            itemStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(itemHeight));
            itemStyle.flexShrink = 0f;
            overlay.addOptionItem(itemLabel, itemStyle, option);
        }

        guiSystem.insertLayerAfter(overlay, this.getRoot());
    }
    
    private void closeOverlay() {
        if (overlay == null) return;
        GuiSystem.INSTANCE.get().removeScreenLayer(overlay);
        overlay = null;
    }

    private void selectOption(T option) {
        this.selected = option;
        this.displayLabel.setText(selectedText());
        if (onChange != null) onChange.accept(option);
        closeOverlay();
    }

    public class ArrowButton extends Widget {

        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            super.doRender(graphics, mouseX, mouseY, a);
            float cx = this.x + this.width / 2f;
            float cy = this.y + this.height / 2f;
            float s = Math.min(this.width, this.height) * 0.25f;
            int arrowColor = 0xFFE2E8F0;
            if (overlay == null) {
                graphics.fill(cx - s, cy - s * 0.5f, cx + s, cy - s * 0.5f + 1, arrowColor);
                graphics.fill(cx - s, cy - s * 0.5f, cx - s + 1, cy + s * 0.5f, arrowColor);
                graphics.fill(cx + s - 1, cy - s * 0.5f, cx + s, cy + s * 0.5f, arrowColor);
                graphics.fill(cx - s, cy + s * 0.5f - 1, cx + s, cy + s * 0.5f, arrowColor);
                graphics.fill(cx - 1, cy - s * 0.5f, cx + 1, cy + s * 0.5f, arrowColor);
            } else {
                graphics.fill(cx - s, cy - s * 0.5f, cx + s, cy + s * 0.5f, 0x33FFFFFF);
            }
        }

        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (overlay == null) {
                openOverlay();
            } else {
                closeOverlay();
            }
            return true;
        }
    }

    public class ComboBoxOverlay extends ContainerWidget {

        void addOptionItem(Label label, TaffyStyle style, T option) {
            var item = new OptionItem(label, option);
            this.addChild(item, style);
        }

        @Override
        public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (!this.isMouseOver(event.x(), event.y())) {
                closeOverlay();
                return true;
            }
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public void resize(float offsetX, float offsetY) {
            var guiSystem = GuiSystem.INSTANCE.get();
            var myRect = ComboBox.this.getRectangle();
            float overlayX = myRect.left();
            float overlayY = myRect.bottom();
            float overlayW = myRect.width();

            float itemHeight = Math.max(ComboBox.this.height, 24f);
            int itemCount = options.size() + (nullable ? 1 : 0);
            float totalContentH = itemCount * itemHeight;
            float maxH = guiSystem.screenHeight * 0.3f;
            float spaceBelow = guiSystem.screenHeight - overlayY;
            float overlayH = Math.min(totalContentH, Math.min(maxH, spaceBelow));

            var s = this.style;
            s.size = new TaffySize<>(TaffyDimension.length(overlayW), TaffyDimension.length(overlayH));
            if (totalContentH > overlayH) {
                s.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
                s.scrollbarWidth = 8;
            } else {
                s.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.VISIBLE);
                s.scrollbarWidth = 0;
            }
            this.setStyle(s);

            super.resize(overlayX, overlayY);
        }

        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            graphics.renderOutline(this.x, this.y, this.width, this.height, BORDER_COLOR);
            super.doRender(graphics, mouseX, mouseY, a);
        }
    }

    public class OptionItem extends ContainerWidget {

        private final Label label;
        private final T option;

        OptionItem(Label label, T option) {
            this.label = label;
            this.option = option;
        }

        @Override
        public void afterTreeAndNodeSet() {
            super.afterTreeAndNodeSet();
            var lStyle = new TaffyStyle();
            lStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            this.addChild(label, lStyle);
        }

        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            selectOption(option);
            return true;
        }
    }
}
