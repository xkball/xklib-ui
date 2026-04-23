package com.xkball.xklibmc.ui.widget;

import com.xkball.xklib.api.gui.widget.IInputWidget;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklibmc.ui.widget.mc.ObjectInputBox;
import com.xkball.xklibmc.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class NumberInputWidget<T extends Number> extends ContainerWidget implements IInputWidget<T> {

    private static final Logger LOGGER = Logger.getLogger(NumberInputWidget.class.getName());

    private static final String ROOT_STYLE = """
            .number_input_spacer {
                size: 2rpx 100%;
            }
            .number_input_mid {
                size: 70%-1rpx 100%;
            }
            .number_input_buttons {
                size: 30%-1rpx 100%;
                flex-direction: column;
            }
            .number_input_btn {
                size: 100% 50%;
                text-align: center;
                text-scale: expand-width;
                button-shape: rect;
                button-bg-color: rgb(229,233,239);
                text-drop-shadow: false;
                border-top: 1px;
                border-bottom: 1px;
                border-color: rgb(100,105,112);
            }
            """;

    private final T min;
    private final T max;
    private final T step;

    private final Predicate<String> validator;
    private final Function<String, T> parser;
    private final Function<T, String> formatter;
    
    private final ObjectInputBox<T> inputBox;
    
    private final List<ILayoutVariable<T>> bindings = new ArrayList<>();
    private T value;

    public NumberInputWidget(
            T min,
            T max,
            T step,
            Predicate<String> validator,
            Function<String, T> parser,
            Function<T, String> formatter
    ) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.validator = Objects.requireNonNull(validator);
        this.parser = Objects.requireNonNull(parser);
        this.formatter = Objects.requireNonNull(formatter);

        this.asRootStyle(ROOT_STYLE);
        
        this.inputBox = new ObjectInputBox<>(
                Minecraft.getInstance().font,
                0,
                0,
                0,
                0,
                Component.literal(""),
                str -> {
                    if (str == null) {
                        return false;
                    }
                    var s = str.trim();
                    if (s.isEmpty()) {
                        return false;
                    }
                    if (!this.validator.test(s)) {
                        return false;
                    }
                    try {
                        var v = this.parser.apply(s);
                        if (v == null) {
                            return false;
                        }
                        return inRange(v);
                    } catch (Exception e) {
                        return false;
                    }
                },
                str -> this.parser.apply(str.trim())
        );
        
        this.addChild(new Widget(){
            @Override
            public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                super.doRender(graphics, mouseX, mouseY, a);
                if(inputBox.isValid()){
                    graphics.fill(getX(),getY(),getMaxX(),getMaxY(), VanillaUtils.getColor(0,200,0,255));
                }
                else {
                    graphics.fill(getX(),getY(),getMaxX(),getMaxY(), VanillaUtils.getColor(200,0,0,255));
                }
            }
        }.setCSSClassName("number_input_spacer"));
        
        WidgetWrapper inputWrapper = new WidgetWrapper(this.inputBox);
        inputWrapper.setUserInput(true);
        inputWrapper.setCSSClassName("number_input_mid");
        this.addChild(inputWrapper);
        
        Button downButton = new Button("-", this::stepDown);
        downButton.setCSSClassName("number_input_btn");
        
        Button upButton = new Button("+", this::stepUp);
        upButton.setCSSClassName("number_input_btn");

        var btnContainer = new ContainerWidget();
        btnContainer.setCSSClassName("number_input_buttons");
        btnContainer.addChild(upButton);
        btnContainer.addChild(downButton);
        this.addChild(btnContainer);

        this.setValue(clamp(min));
    }
    
    @Override
    public T getValue() {
        var parsed = getParsedIfValid();
        if (parsed != null) {
            return parsed;
        }
        return this.value;
    }
    
    @Override
    public void setValue(T value) {
        var v = clamp(value);
        this.value = v;
        updateTextFromValue(v);
        for (var bind : bindings) {
            bind.set(v);
        }
    }
    
    @Override
    public NumberInputWidget<T> bind(ILayoutVariable<T> variable) {
        this.setValue(variable.get());
        this.bindings.add(variable);
        return this;
    }

    public void stepUp() {
        applyStep(true);
    }

    public void stepDown() {
        applyStep(false);
    }

    private void applyStep(boolean positive) {
        var base = getParsedIfValid();
        if (base == null) {
            base = this.value;
        }
        if (base == null) {
            base = this.min;
        }
        try {
            var next = addStep(base, positive);
            setValue(next);
        } catch (Exception _) {
        }
    }

    @SuppressWarnings("unchecked")
    private T addStep(T base, boolean positive) {
        if (base instanceof Integer b && step instanceof Integer s) {
            long n = (long) b + (positive ? (long) s : -(long) s);
            return (T) Integer.valueOf((int) n);
        }
        if (base instanceof Long b && step instanceof Long s) {
            long n = b + (positive ? s : -s);
            return (T) Long.valueOf(n);
        }
        if (base instanceof Float b && step instanceof Float s) {
            float n = b + (positive ? s : -s);
            return (T) Float.valueOf(n);
        }
        if (base instanceof Double b && step instanceof Double s) {
            double n = b + (positive ? s : -s);
            return (T) Double.valueOf(n);
        }
        double n = base.doubleValue() + (positive ? step.doubleValue() : -step.doubleValue());
        return switch (base) {
            case Integer i -> (T) Integer.valueOf((int) Math.round(n));
            case Long l -> (T) Long.valueOf(Math.round(n));
            case Float v -> (T) Float.valueOf((float) n);
            default -> (T) Double.valueOf(n);
        };
    }

    private boolean inRange(T v) {
        if (v == null) {
            return false;
        }
        return toDouble(v) >= toDouble(min) && toDouble(v) <= toDouble(max);
    }

    private T clamp(T v) {
        if (v == null) {
            return min;
        }
        double d = toDouble(v);
        double mn = toDouble(min);
        double mx = toDouble(max);
        if (d < mn) {
            return min;
        }
        if (d > mx) {
            return max;
        }
        return v;
    }

    private double toDouble(T v) {
        return v.doubleValue();
    }

    private T getParsedIfValid() {
        try {
            var v = this.inputBox.get();
            if (v == null) {
                return null;
            }
            if (!inRange(v)) {
                return null;
            }
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    private void updateTextFromValue(T v) {
        this.inputBox.setValue(this.formatter.apply(v));
        this.inputBox.displayPos = 0;
    }

    public static NumberInputWidget<Integer> ofInt(int min, int max, int step) {
        return new NumberInputWidget<>(
                min,
                max,
                step,
                ObjectInputBox.INT_VALIDATOR,
                ObjectInputBox.INT_RESPONDER,
                String::valueOf
        );
    }
}
