package com.xkball.xklibmc.ui.widget;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.widget.IInputWidget;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklibmc.ui.widget.mc.ObjectInputBox;
import com.xkball.xklibmc.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@GuiWidgetClass
public class ObjectInputWidget<T> extends ContainerWidget implements IInputWidget<T> {
    
    private static final String ROOT_STYLE = """
            .object_input_spacer {
                size: 2rpx 100%;
            }
            .object_input_mid {
                size: 100%-2rpx 100%;
            }
            """;
    
    private final Predicate<String> validator;
    private final Function<String, T> parser;
    private final ObjectInputBox<T> inputBox;
    
    private final List<ILayoutVariable<T>> bindings = new ArrayList<>();
    private Consumer<ObjectInputWidget<T>> callback;
    
    public ObjectInputWidget(
            Predicate<String> validator,
            Function<String, T> parser
    ) {
        this.validator = validator;
        this.parser = parser;
        this.asRootStyle(ROOT_STYLE);
        
        this.inputBox = new ObjectInputBox<>(
                Minecraft.getInstance().font,
                0, 0, 0, 0,
                Component.literal(""),
                validator, parser
        );
        this.inputBox.setResponder(_ -> this.setValue(this.getParsed()));
        
        this.addChild(new Widget() {
            @Override
            public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                super.doRender(graphics, mouseX, mouseY, a);
                if (inputBox.isValid()) {
                    graphics.fill(getX(), getY(), getMaxX(), getMaxY(), VanillaUtils.getColor(0, 200, 0, 255));
                } else {
                    graphics.fill(getX(), getY(), getMaxX(), getMaxY(), VanillaUtils.getColor(200, 0, 0, 255));
                }
            }
        }.setCSSClassName("object_input_spacer"));
        
        WidgetWrapper inputWrapper = new WidgetWrapper(this.inputBox);
        inputWrapper.setUserInput(true);
        inputWrapper.setCSSClassName("object_input_mid");
        this.addChild(inputWrapper);
    }
    
    public static ObjectInputWidget<String> ofString() {
        return new ObjectInputWidget<>(
                ObjectInputBox.PASS_VALIDATOR,
                ObjectInputBox.PASS_RESPONDER
        );
    }
    
    public String getAsString(){
        return this.inputBox.getValue();
    }
    
    @Override
    public T getValue() {
        return this.inputBox.get();
    }
    
    @Override
    public void setValue(T value) {
        throw new UnsupportedOperationException();
    }
    
    public ObjectInputWidget<T> setCallback(Consumer<ObjectInputWidget<T>> callback) {
        this.callback = callback;
        return this;
    }
    
    @Override
    public ObjectInputWidget<T> bind(ILayoutVariable<T> variable) {
        throw new UnsupportedOperationException();
    }
    
    private T getParsed() {
        return this.inputBox.get();
    }
    
}
