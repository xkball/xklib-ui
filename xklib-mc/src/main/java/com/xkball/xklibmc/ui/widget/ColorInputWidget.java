package com.xkball.xklibmc.ui.widget;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.widget.IInputWidget;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklibmc.utils.VanillaUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

@GuiWidgetClass
public class ColorInputWidget extends ContainerWidget implements IInputWidget<Integer> {

    private static final String ROOT_STYLE = """
            .color_input_row {
                size: 100% 25%;
                flex-direction: row;
            }
            .color_input_slider {
                size: 70% 100%;
            }
            .color_input_number {
                size: 30% 100%;
            }
            """;

    private static final int HANDLE_WIDTH = 8;

    private final List<ILayoutVariable<Integer>> bindings = new ArrayList<>();
    private Consumer<ColorInputWidget> callback;

    private int r, g, b, a;
    private boolean updating;

    private final GradientSlider sliderR;
    private final GradientSlider sliderG;
    private final GradientSlider sliderB;
    private final GradientSlider sliderA;

    private final NumberInputWidget<Integer> inputR;
    private final NumberInputWidget<Integer> inputG;
    private final NumberInputWidget<Integer> inputB;
    private final NumberInputWidget<Integer> inputA;

    public ColorInputWidget() {
        this(255, 255, 255, 255);
    }

    public ColorInputWidget(int defaultR, int defaultG, int defaultB, int defaultA) {
        this.r = defaultR;
        this.g = defaultG;
        this.b = defaultB;
        this.a = defaultA;
        this.updating = true;

        this.asRootStyle(ROOT_STYLE);
        this.inlineStyle("flex-direction: column;");

        this.sliderR = new GradientSlider(0, this::getR, this::getG, this::getB, this::getA, v -> onSliderChanged(0, v));
        this.sliderG = new GradientSlider(1, this::getR, this::getG, this::getB, this::getA, v -> onSliderChanged(1, v));
        this.sliderB = new GradientSlider(2, this::getR, this::getG, this::getB, this::getA, v -> onSliderChanged(2, v));
        this.sliderA = new GradientSlider(3, this::getR, this::getG, this::getB, this::getA, v -> onSliderChanged(3, v));

        this.inputR = NumberInputWidget.ofInt(0, 255, 1);
        this.inputG = NumberInputWidget.ofInt(0, 255, 1);
        this.inputB = NumberInputWidget.ofInt(0, 255, 1);
        this.inputA = NumberInputWidget.ofInt(0, 255, 1);

        this.updating = false;

        inputR.setCallback(w -> onInputChanged(0, w.getValue()));
        inputG.setCallback(w -> onInputChanged(1, w.getValue()));
        inputB.setCallback(w -> onInputChanged(2, w.getValue()));
        inputA.setCallback(w -> onInputChanged(3, w.getValue()));

        sliderR.setValue(r);
        sliderG.setValue(g);
        sliderB.setValue(b);
        sliderA.setValue(a);

        inputR.setValue(r);
        inputG.setValue(g);
        inputB.setValue(b);
        inputA.setValue(a);

        addSliderRow(sliderR, inputR);
        addSliderRow(sliderG, inputG);
        addSliderRow(sliderB, inputB);
        addSliderRow(sliderA, inputA);
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getA() {
        return a;
    }

    private void setR(int v) {
        this.r = clamp(v);
    }

    private void setG(int v) {
        this.g = clamp(v);
    }

    private void setB(int v) {
        this.b = clamp(v);
    }

    private void setA(int v) {
        this.a = clamp(v);
    }

    public ColorInputWidget setCallback(Consumer<ColorInputWidget> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public Integer getValue() {
        return VanillaUtils.getColor(r, g, b, a);
    }

    @Override
    public void setValue(Integer value) {
        if (value == null) return;
        this.a = (value >> 24) & 0xFF;
        this.r = (value >> 16) & 0xFF;
        this.g = (value >> 8) & 0xFF;
        this.b = value & 0xFF;
        syncFromFields();
    }

    @Override
    public ColorInputWidget bind(ILayoutVariable<Integer> variable) {
        this.bindings.add(variable);
        return this;
    }

    private void addSliderRow(GradientSlider slider, NumberInputWidget<Integer> input) {
        var row = new ContainerWidget();
        row.setCSSClassName("color_input_row");

        var sliderWrapper = new WidgetWrapper(slider);
        sliderWrapper.setCSSClassName("color_input_slider");
        row.addChild(sliderWrapper);

        input.setCSSClassName("color_input_number");
        row.addChild(input);

        this.addChild(row);
    }

    private void onSliderChanged(int channel, int value) {
        if (updating) return;
        updating = true;
        switch (channel) {
            case 0 -> this.r = value;
            case 1 -> this.g = value;
            case 2 -> this.b = value;
            case 3 -> this.a = value;
        }
        syncInputs();
        fireChange();
        updating = false;
    }

    private void onInputChanged(int channel, int value) {
        if (updating) return;
        updating = true;
        switch (channel) {
            case 0 -> this.r = value;
            case 1 -> this.g = value;
            case 2 -> this.b = value;
            case 3 -> this.a = value;
        }
        syncSliders();
        fireChange();
        updating = false;
    }

    private void syncFromFields() {
        updating = true;
        syncSliders();
        syncInputs();
        updating = false;
    }

    private void syncSliders() {
        sliderR.setValue(r);
        sliderG.setValue(g);
        sliderB.setValue(b);
        sliderA.setValue(a);
    }

    private void syncInputs() {
        inputR.setValue(r);
        inputG.setValue(g);
        inputB.setValue(b);
        inputA.setValue(a);
    }

    private void fireChange() {
        for (var bind : bindings) bind.set(getValue());
        if (callback != null) callback.accept(this);
    }

    private static int clamp(int v) {
        return Math.clamp(v, 0, 255);
    }

    private static class GradientSlider extends ExtendedSlider {

        private static final int CHANNEL_R = 0;
        private static final int CHANNEL_G = 1;
        private static final int CHANNEL_B = 2;
        private static final int CHANNEL_A = 3;

        private final int channel;
        private final IntSupplier rSup;
        private final IntSupplier gSup;
        private final IntSupplier bSup;
        private final IntSupplier aSup;
        private final Consumer<Integer> onChange;

        GradientSlider(
                int channel,
                IntSupplier rSup,
                IntSupplier gSup,
                IntSupplier bSup,
                IntSupplier aSup,
                Consumer<Integer> onChange
        ) {
            super(0, 0, 0, 0, Component.empty(), Component.empty(), 0, 255, 0, 1, 0, false);
            this.channel = channel;
            this.rSup = rSup;
            this.gSup = gSup;
            this.bSup = bSup;
            this.aSup = aSup;
            this.onChange = onChange;
        }

        @Override
        public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            int x0 = getX();
            int y0 = getY();
            int w = getWidth();
            int h = getHeight();

            if (this.active) {
                int r = rSup.getAsInt();
                int g = gSup.getAsInt();
                int b = bSup.getAsInt();
                int a = aSup.getAsInt();

                int prevColor = -1;
                int fillStartX = x0;
                for (int px = 0; px < w; px++) {
                    float t = (float) px / (float) Math.max(1, w - 1);
                    int v = Math.clamp((int) (t * 255), 0, 255);
                    int color = switch (this.channel) {
                        case CHANNEL_R -> VanillaUtils.getColor(v, g, b, a);
                        case CHANNEL_G -> VanillaUtils.getColor(r, v, b, a);
                        case CHANNEL_B -> VanillaUtils.getColor(r, g, v, a);
                        case CHANNEL_A -> VanillaUtils.getColor(r, g, b, v);
                        default -> 0;
                    };
                    if (color != prevColor) {
                        if (prevColor != -1) {
                            guiGraphics.fill(fillStartX, y0, x0 + px, y0 + h, prevColor);
                        }
                        fillStartX = x0 + px;
                        prevColor = color;
                    }
                }
                if (prevColor != -1) {
                    guiGraphics.fill(fillStartX, y0, x0 + w, y0 + h, prevColor);
                }
            }

            double handlePos = (this.value - this.minValue) / (this.maxValue - this.minValue);
            int handleX = x0 + (int) (handlePos * (w - HANDLE_WIDTH));
            guiGraphics.fill(handleX, y0, handleX + HANDLE_WIDTH, y0 + h, 0xFFFFFFFF);
            guiGraphics.fill(handleX + 1, y0 + 1, handleX + HANDLE_WIDTH - 1, y0 + h - 1, 0xFFAAAAAA);
        }

        @Override
        protected void applyValue() {
            onChange.accept((int) this.value);
        }

    }
}
