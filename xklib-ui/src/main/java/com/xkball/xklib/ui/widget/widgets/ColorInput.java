package com.xkball.xklib.ui.widget.widgets;

import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.widget.layout.GridLayout;

import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class ColorInput extends GridLayout {

    private static final String[] CHANNEL_NAMES = {"R", "G", "B", "A"};

    private int originalColor;
    private int currentColor;

    private final DragBox[] sliders = new DragBox[4];
    private final EditBox[] editBoxes = new EditBox[4];
    private final Label[] channelLabels = new Label[4];

    private Consumer<Integer> onConfirm;
    private Consumer<Integer> onCancel;

    private boolean updating = false;

    public ColorInput(int initialColor) {
        super(new GridParam.Builder()
                .addCol("30px").addCol("1").addCol("1").addCol("1").addCol("1")
                .addRow("1").addRow("1").addRow("1").addRow("1").addRow("1")
                .build());
        this.originalColor = initialColor;
        this.currentColor = initialColor;
    }

    public ColorInput setOnConfirm(Consumer<Integer> onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    public ColorInput setOnCancel(Consumer<Integer> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public int getCurrentColor() {
        return this.currentColor;
    }

    public void setColor(int color) {
        this.originalColor = color;
        this.currentColor = color;
        syncSlidersFromColor();
    }

    @Override
    public void init() {
        super.init();

        for (int i = 0; i < 4; i++) {
            final int channel = i;

            var nameLabel = new Label(CHANNEL_NAMES[i], 14, 0xFF000000);
            channelLabels[i] = nameLabel;
            nameLabel.setMargin("2px");
            this.addChild(nameLabel, new GridElementParam(i, 0, 1, 1));

            var slider = new DragBox(0, 255, getChannel(this.currentColor, i));
            sliders[i] = slider;
            slider.setOnChange(v -> onSliderChanged(channel, v.intValue()));
            slider.setMargin("2px");
            this.addChild(slider, new GridElementParam(i, 1, 1, 2));

            var box = new EditBox();
            editBoxes[i] = box;
            box.setText(String.valueOf(getChannel(this.currentColor, i)));
            box.setLineHeight(14);
            box.setMargin("2px");
            this.addChild(box, new GridElementParam(i, 3, 1, 1));

            var numLabel = new Label(String.valueOf((int) slider.getValue()), 14, 0xFF000000);
            numLabel.setMargin("2px");
            this.addChild(numLabel, new GridElementParam(i, 4, 1, 1));
        }

        var originalPreview = new Label("", 14) {
            @Override
            public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, originalColor);
                super.render(graphics, mouseX, mouseY, a);
            }
        };
        this.addChild(originalPreview, new GridElementParam(4, 1, 1, 1));

        var currentPreview = new Label("", 14) {
            @Override
            public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, currentColor);
                super.render(graphics, mouseX, mouseY, a);
            }
        };
        this.addChild(currentPreview, new GridElementParam(4, 2, 1, 1));

        var cancelBtn = new Button("取消", () -> {
            if (this.onCancel != null) {
                this.onCancel.accept(this.originalColor);
            }
        });
        cancelBtn.setStyle(Button.DefaultButtonStyles.TRANSPARENT);
        cancelBtn.setMargin("2px");
        this.addChild(cancelBtn, new GridElementParam(4, 3, 1, 1));

        var confirmBtn = new Button("确认", () -> {
            if (this.onConfirm != null) {
                this.onConfirm.accept(this.currentColor);
            }
        });
        confirmBtn.setStyle(Button.DefaultButtonStyles.TRANSPARENT);
        confirmBtn.setMargin("2px");
        this.addChild(confirmBtn, new GridElementParam(4, 4, 1, 1));
    }

    private void onSliderChanged(int channel, int value) {
        if (this.updating) return;
        this.updating = true;
        this.currentColor = setChannel(this.currentColor, channel, value);
        this.editBoxes[channel].setText(String.valueOf(value));
        this.sliders[channel].setValue(value);
        this.updating = false;
    }

    private void syncSlidersFromColor() {
        if (this.sliders[0] == null) return;
        this.updating = true;
        for (int i = 0; i < 4; i++) {
            int v = getChannel(this.currentColor, i);
            this.sliders[i].setValue(v);
            this.editBoxes[i].setText(String.valueOf(v));
        }
        this.updating = false;
    }

    private static int getChannel(int color, int channel) {
        return switch (channel) {
            case 0 -> (color >> 16) & 0xFF;
            case 1 -> (color >> 8) & 0xFF;
            case 2 -> color & 0xFF;
            case 3 -> (color >> 24) & 0xFF;
            default -> 0;
        };
    }

    private static int setChannel(int color, int channel, int value) {
        value = Math.max(0, Math.min(255, value));
        return switch (channel) {
            case 0 -> (color & 0xFF00FFFF) | (value << 16);
            case 1 -> (color & 0xFFFF00FF) | (value << 8);
            case 2 -> (color & 0xFFFFFF00) | value;
            case 3 -> (color & 0x00FFFFFF) | (value << 24);
            default -> color;
        };
    }
}
