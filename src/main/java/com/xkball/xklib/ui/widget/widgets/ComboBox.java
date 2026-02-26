package com.xkball.xklib.ui.widget.widgets;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.layout.VerticalAlign;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.ui.widget.layout.AbsolutePosLayout;
import com.xkball.xklib.ui.widget.layout.GridLayout;
import com.xkball.xklib.ui.widget.layout.ScrollableFlexLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComboBox<T> extends GridLayout {

    private static final int ITEM_HEIGHT = 24;
    private static final int MAX_VISIBLE_ITEMS = 8;
    private static final int BG_COLOR = 0xFFE2E8F0;
    private static final int BORDER_COLOR = 0xFF94A3B8;

    private final List<T> options;
    private final Function<T, String> toDisplay;
    private final boolean nullable;

    private T selected;
    private final Label displayLabel;
    private ComboBoxOverlay overlay;

    public ComboBox(List<T> options, Function<T, String> toDisplay, boolean nullable) {
        super(new GridParam.Builder()
                .addCol("1")
                .addCol("30px")
                .addRow("1")
                .build());
        this.options = new ArrayList<>(options);
        this.toDisplay = toDisplay;
        this.nullable = nullable;
        this.displayLabel = new Label("", 16);
        this.displayLabel.setPaddingLeft(SizeParam.parse("4px"));
        this.displayLabel.setInnerVerticalAlign(VerticalAlign.CENTER);
        this.addChild(this.displayLabel, new GridElementParam(0, 0, 1, 1));

        var expandBtn = new Button("v", this::openOverlay);
        expandBtn.setStyle(Button.DefaultButtonStyles.TRANSPARENT);
        this.addChild(expandBtn, new GridElementParam(0, 1, 1, 1));

        this.addDecoration(new Background(BG_COLOR));
        this.setOverflow(false);
        if(!nullable) this.setSelected(options.getFirst());
        updateDisplayLabel();
    }

    public ComboBox(List<T> options, Function<T, String> toDisplay) {
        this(options, toDisplay, false);
    }
    
    public static ComboBox<String> ofString(List<String> options, boolean nullable){
        return new ComboBox<>(options, s -> s, nullable);
    }

    public static <E extends Enum<E>> ComboBox<E> ofEnum(Class<E> enumClass, boolean nullable) {
        E[] constants = enumClass.getEnumConstants();
        List<E> list = new ArrayList<>(List.of(constants));
        return new ComboBox<>(list, Enum::name, nullable);
    }

    public static <E extends Enum<E>> ComboBox<E> ofEnum(Class<E> enumClass) {
        return ofEnum(enumClass, false);
    }

    public T getSelected() {
        return this.selected;
    }

    @SuppressWarnings("unchecked")
    public <R extends Enum<R>> R getSelectedEnum() {
        return (R) this.selected;
    }

    public void setSelected(T value) {
        this.selected = value;
        updateDisplayLabel();
    }

    private void updateDisplayLabel() {
        if (this.selected == null) {
            this.displayLabel.setText("");
        } else {
            this.displayLabel.setText(this.toDisplay.apply(this.selected));
        }
        this.displayLabel.markDirty();
    }

    private void openOverlay() {
        if (this.overlay != null) {
            return;
        }
        this.overlay = new ComboBoxOverlay();
        XKLib.gui.addScreenLayer(this.overlay);
    }

    private void closeOverlay() {
        if (this.overlay != null) {
            XKLib.gui.removeScreenLayer(this.overlay);
            this.overlay = null;
        }
    }
    
    @Override
    public void resize() {
        super.resize();
        this.closeOverlay();
    }
    
    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        graphics.renderOutline(this.x, this.y, this.width, this.height, BORDER_COLOR);
        super.render(graphics, mouseX, mouseY, a);
    }

    public class ComboBoxOverlay extends AbsolutePosLayout {

        private ScrollableFlexLayout list;
        private int lastDropdownX = Integer.MIN_VALUE;
        private int lastDropdownY = Integer.MIN_VALUE;
        private int lastDropdownW = -1;
        private int lastDropdownH = -1;

        public ComboBoxOverlay() {
            super();
            this.setOverflow(true);
        }

        @Override
        public void init() {
            super.init();
            this.list = new ScrollableFlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.START)
                    .align(FlexParam.Align.START)
                    .overflow(false)
                    .build());
            this.list.yScrollable = true;
            this.list.yScrollBarVisible = true;

            int order = 0;
            if (nullable) {
                int o = order++;
                var btn = new Button("", () -> {
                    setSelected(null);
                    closeOverlay();
                });
                btn.setFixHeight(ITEM_HEIGHT);
                btn.setStyle(Button.DefaultButtonStyles.TRANSPARENT);
                this.list.addChild(btn, FlexElementParam.of(o, "100%", ITEM_HEIGHT + "px"));
            }
            for (T opt : options) {
                int o = order++;
                String display = toDisplay.apply(opt);
                var btn = new Button(display, () -> {
                    setSelected(opt);
                    closeOverlay();
                });
                btn.setFixHeight(ITEM_HEIGHT);
                btn.setStyle(Button.DefaultButtonStyles.TRANSPARENT);
                this.list.addChild(btn, FlexElementParam.of(o, "100%", ITEM_HEIGHT + "px"));
            }
            this.addChild(this.list, new ScreenRectangle(0, 0, 0, 0));
        }

        @Override
        public void resize() {
            super.resize();
            var contentH = this.list.getActualHeight();
            int dropdownX = ComboBox.this.x;
            int dropdownY = ComboBox.this.y + ComboBox.this.height;
            int dropdownW = ComboBox.this.contentWidth;
            int dropdownH = (int) Math.min(contentH,Math.min(this.contentHeight*0.2,this.contentHeight - dropdownY));
            
            if (dropdownX != this.lastDropdownX || dropdownY != this.lastDropdownY
                    || dropdownW != this.lastDropdownW || dropdownH != this.lastDropdownH) {
                this.lastDropdownX = dropdownX;
                this.lastDropdownY = dropdownY;
                this.lastDropdownW = dropdownW;
                this.lastDropdownH = dropdownH;
                this.addChild(this.list, new ScreenRectangle(dropdownX, dropdownY, dropdownW, dropdownH));
            }
        }

        @Override
        public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            int lx = this.list.getX();
            int ly = this.list.getY();
            int lw = this.list.getWidth();
            int lh = this.list.getHeight();
            graphics.fill(lx, ly, lx + lw, ly + lh, BG_COLOR);
            super.render(graphics, mouseX, mouseY, a);
            graphics.renderOutline(lx, ly, lw, lh, BORDER_COLOR);
        }

        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            int lx = this.list.getX();
            int ly = this.list.getY();
            int lw = this.list.getWidth();
            int lh = this.list.getHeight();

            boolean insideDropdown = event.x() >= lx && event.x() < lx + lw
                    && event.y() >= ly && event.y() < ly + lh;

            if (!insideDropdown) {
                closeOverlay();
            }
            return true;
        }

        @Override
        public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            boolean insideDropdown = event.x() >= this.list.getX() && event.x() < this.list.getX() + this.list.getWidth()
                    && event.y() >= this.list.getY() && event.y() < this.list.getY() + this.list.getHeight();
            if (insideDropdown) {
                super.mouseClicked(event, doubleClick);
            } else {
                closeOverlay();
            }
            return true;
        }

        @Override
        public boolean isFocusable() {
            return false;
        }
    }
}
