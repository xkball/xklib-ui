package com.xkball.xklibmc.ui;

import com.xkball.xklib.XKLib;
import com.xkball.xklibmc.annotation.NonnullByDefault;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import com.xkball.xklibmc.x3d.backend.b3d.B3dRenderContext;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

@NonnullByDefault
public class XKLibBaseScreen extends Screen {
    
    protected GuiSystem guiSystem = new GuiSystem();
    
    public XKLibBaseScreen() {
        super(Component.empty());
        GuiSystem.INSTANCE.set(guiSystem);
    }
    
    public void addScreenLayer(Widget layer){
        this.guiSystem.addScreenLayer(layer);
    }
    
    public void insertLayerAfter(Widget layer, IGuiWidget after){
        this.guiSystem.insertLayerAfter(layer, after);
    }
    
    public void removeScreenLayer(Widget layer){
        this.guiSystem.removeScreenLayer(layer);
    }
    
    @Override
    public void tick() {
        super.tick();
        if(guiSystem.screenLayers.isEmpty()){
            this.minecraft.setScreen(null);
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        if(!this.initialized){
            XKLib.RENDER_CONTEXT.set(new B3dRenderContext());
        }
        var window = XKLib.RENDER_CONTEXT.get().getWindow();
        guiSystem.resize(window.getWidth(), window.getHeight());
    }
    
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        var guiGraphics = (B3dGuiGraphics) XKLib.RENDER_CONTEXT.get().getGUIGraphics();
        guiGraphics.setInner(graphics);
        guiSystem.setGraphics(guiGraphics);
        var window = XKLib.RENDER_CONTEXT.get().getWindow();
        var actualW = window.getWidth();
        var actualH = window.getHeight();
        var w = this.width;
        var h = this.height;
        //todo 变换输入
        graphics.pose().pushMatrix();
        graphics.pose().scale(w/(float)actualW, h/(float)actualH);
        guiSystem.render(mouseX, mouseY, a);
        graphics.pose().popMatrix();
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
    
    public com.xkball.xklib.ui.input.MouseButtonEvent convertMouseButtonEvent(MouseButtonEvent event) {
        return new com.xkball.xklib.ui.input.MouseButtonEvent(event.x(),event.y(),event.button(),event.modifiers());
    }
    
    public com.xkball.xklib.ui.input.KeyEvent convertKeyEvent(KeyEvent event) {
        return new com.xkball.xklib.ui.input.KeyEvent(event.key(), event.scancode(), event.modifiers());
    }
    
    public com.xkball.xklib.ui.input.CharacterEvent convertCharacterEvent(CharacterEvent event) {
        return new com.xkball.xklib.ui.input.CharacterEvent(event.codepoint(), 0);
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        var window = XKLib.RENDER_CONTEXT.get().getWindow();
        guiSystem.resize(window.getWidth(), window.getHeight());
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        var e = convertMouseButtonEvent(event);
        this.guiSystem.dispatchEventReversed(widget -> widget.mouseClicked(e,doubleClick));
        return true;
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        var e = convertMouseButtonEvent(event);
        this.guiSystem.dispatchEventReversed(widget -> widget.mouseReleased(e));
        return true;
    }
    
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        var e = convertMouseButtonEvent(event);
        this.guiSystem.dispatchEventReversed(widget -> widget.mouseDragged(e,dx,dy));
        return true;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        this.guiSystem.dispatchEventReversed(widget -> widget.mouseScrolled(x,y,scrollX,scrollY));
        return true;
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        var e = convertKeyEvent(event);
        this.guiSystem.dispatchEventReversed(widget -> widget.keyPressed(e));
        return super.keyPressed(event);
    }
    
    @Override
    public boolean keyReleased(KeyEvent event) {
        var e = convertKeyEvent(event);
        this.guiSystem.dispatchEventReversed(widget -> widget.keyReleased(e));
        return true;
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        var e = convertCharacterEvent(event);
        this.guiSystem.dispatchEventReversed(widget -> widget.charTyped(e));
        return true;
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        this.guiSystem.dispatchEventReversed(widget -> widget.mouseMoved(x,y));
    }
    
}
