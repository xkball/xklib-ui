package com.xkball.xklibmc.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklibmc.ui.widget.mc.XKLibMultiLineEditBox;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class WidgetWrapper extends Widget {
    
    private final AbstractWidget widget;
    private boolean userInput = false;
    
    public WidgetWrapper(AbstractWidget widget) {
        this.widget = widget;
    }
    
    public void setUserInput(boolean userInput) {
        this.userInput = userInput;
        this.focusNode.setCanTakePrimaryFocus(userInput);
    }
    
    public AbstractWidget getWidget() {
        return widget;
    }
    
    private float rescaleX(double x){
        return rescaleX((float) x);
    }
    
    private float rescaleY(double y){
        return rescaleY((float) y);
    }
    
    private float rescaleX(float x){
        if(XKLib.RENDER_CONTEXT.get().getGUIGraphics() instanceof B3dGuiGraphics guiGraphics) {
            return this.x + (x - this.x) / guiGraphics.scale;
        }
        return x;
    }
    
    private float rescaleY(float y){
        if(XKLib.RENDER_CONTEXT.get().getGUIGraphics() instanceof B3dGuiGraphics guiGraphics) {
            return this.y + (y - this.y) / guiGraphics.scale;
        }
        return y;
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        super.resize(offsetX, offsetY);
        widget.setPosition((int) this.x, (int) this.y);
        if(XKLib.RENDER_CONTEXT.get().getGUIGraphics() instanceof B3dGuiGraphics guiGraphics) {
            widget.setSize((int) (this.width/guiGraphics.scale), (int) (this.height/guiGraphics.scale));
        }
        else widget.setSize((int) this.width, (int) this.height);
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if (graphics instanceof B3dGuiGraphics b3dGraphics) {
            var guiGraphics = b3dGraphics.getInner();
            b3dGraphics.getPose().pushMatrix();
            b3dGraphics.getPose().translate(x, y);
            b3dGraphics.getPose().scale(b3dGraphics.scale, b3dGraphics.scale);
            b3dGraphics.getPose().translate(-x, -y);
            widget.extractRenderState(guiGraphics, (int) rescaleX(mouseX), (int) rescaleY(mouseY), a);
            b3dGraphics.getPose().popMatrix();
        }
    }
    
    private MouseButtonEvent convertMouseButtonEvent(IMouseButtonEvent event){
        var nx = rescaleX(event.x());
        var ny = rescaleY(event.y());
        return new MouseButtonEvent(nx, ny,new MouseButtonInfo(event.button(),event.modifiers()));
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        return widget.mouseClicked(convertMouseButtonEvent(event), doubleClick);
    }
    
    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        widget.mouseReleased(convertMouseButtonEvent(event));
        return false;
    }
    
    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        widget.mouseDragged(convertMouseButtonEvent(event), dx, dy);
        return false;
    }
    
    @Override
    protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
        var nx =  rescaleX(x);
        var ny = rescaleY(y);
        var nsx = scrollX / XKLibBaseScreen.tryGetScaleX();
        var nsy = scrollY / XKLibBaseScreen.tryGetScaleY();
        return widget.mouseScrolled(nx, ny, nsx, nsy);
      
    }
    
    @Override
    protected boolean onKeyPressed(IKeyEvent event) {
        var mcEvent = new KeyEvent(event.key(), event.scancode(), event.modifiers());
        return widget.keyPressed(mcEvent);
    }
    
    @Override
    protected boolean onKeyReleased(IKeyEvent event) {
        var mcEvent = new KeyEvent(event.key(), event.scancode(), event.modifiers());
        return widget.keyReleased(mcEvent);
    }
    
    @Override
    protected boolean onCharTyped(ICharEvent event) {
        var mcEvent = new CharacterEvent(event.codepoint());
        return widget.charTyped(mcEvent);
    }
    
    @Override
    public boolean isFocusable() {
        return userInput;
    }
    
    @Override
    public boolean preeditUpdated(@Nullable Object event) {
        if(event == null){
            return widget.preeditUpdated(null);
        }
        if(event instanceof PreeditEvent e){
            return widget.preeditUpdated(e);
        }
        return false;
    }
    
    @Override
    public void onFocusChanged(boolean focused) {
        if(userInput){
            widget.setFocused(this.isPrimaryFocused());
        }
        else widget.setFocused(focused);
    }
    
    public static WidgetWrapper button(String text, Consumer<Button> onPress) {
        var btn = Button.builder(Component.literal(text), onPress::accept)
                .bounds(0, 0, 0, 0)
                .build();
        return new WidgetWrapper(btn);
    }
    
    public static WidgetWrapper editBox(String placeholder, int maxLength) {
        var editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 0,0, Component.literal(placeholder));
        editBox.setMaxLength(maxLength);
        var result = new WidgetWrapper(editBox);
        result.setUserInput(true);
        return result;
    }
    
    public static WidgetWrapper multiLineTextWidget() {
        var multiLine = new XKLibMultiLineEditBox();
        var result = new WidgetWrapper(multiLine);
        result.setUserInput(true);
        return result;
    }
}
