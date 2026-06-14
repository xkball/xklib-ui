package com.xkball.xklibmc.ui;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import com.xkball.xklibmc.x3d.backend.b3d.B3dRenderContext;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

//todo 还没法用物品栏
@NonNullByDefault
public class XKLibBaseContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    
    protected GuiSystem guiSystem = new GuiSystem();
    protected float guiScale = 1;
    
    
    public XKLibBaseContainerScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
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
    public void containerTick() {
        super.tick();
        if(guiSystem.screenLayers.isEmpty()){
            Objects.requireNonNull(this.minecraft.player).closeContainer();
        }
    }

    @Override
    protected void init() {
        super.init();
        if(!this.initialized){
            XKLib.RENDER_CONTEXT.set(new B3dRenderContext());
            guiSystem.windowHandle = XKLib.RENDER_CONTEXT.get().getWindow().getHandle();
        }
        this.resize_();
    }
    
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if(XKLib.RENDER_CONTEXT.get() == null){
            return;
        }
        var guiGraphics = (B3dGuiGraphics) XKLib.RENDER_CONTEXT.get().getGUIGraphics();
        guiGraphics.setInner(graphics);
        guiGraphics.scale = this.guiScale;
        guiSystem.setGraphics(guiGraphics);
        graphics.pose().pushMatrix();
        graphics.pose().scale(1/ guiScale,1/ guiScale);
        guiSystem.render((int) (mouseX * guiScale), (int) (mouseY * guiScale), a);
        graphics.pose().popMatrix();
        super.extractRenderState(graphics, (int) (mouseX * guiScale), (int) (mouseY * guiScale), a);
    }
    
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
    
    }
    
    public com.xkball.xklib.ui.input.MouseButtonEvent convertMouseButtonEvent(MouseButtonEvent event) {
        return new com.xkball.xklib.ui.input.MouseButtonEvent(event.x() * guiScale,event.y() * guiScale,event.button(),event.modifiers());
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
        this.resize_();
    }
    
    private void resize_(){
        var window = XKLib.RENDER_CONTEXT.get().getWindow();
        var actualW = window.getWidth();
        var actualH = window.getHeight();
        var w = this.width;
        this.guiScale = actualW/(float)w;
        CssLengthUnit.rpxScaleWorkaround = this.guiScale;
        guiSystem.resize(actualW, actualH);
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        var e = convertMouseButtonEvent(event);
        return this.guiSystem.dispatchEventReversed(widget -> widget.mouseClicked(e,doubleClick));
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        var e = convertMouseButtonEvent(event);
        var consumed = this.guiSystem.dispatchEventReversed(widget -> widget.mouseReleased(e));
        if(this.guiSystem.haveDraggingWidget()){
            consumed |= this.guiSystem.dispatchEventReversed(widget -> widget.widgetDropped(e,this.guiSystem.draggingWidget.widget()));
        }
        return consumed;
    }
    
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        var e = convertMouseButtonEvent(event);
        var consumed = this.guiSystem.dispatchEventReversed(widget -> widget.mouseDragged(e,dx * guiScale, dy * guiScale));
        if(this.guiSystem.haveDraggingWidget()){
            consumed |= this.guiSystem.dispatchEventReversed(widget -> widget.widgetDraggingHovered(e,this.guiSystem.draggingWidget.widget()));
        }
        return consumed;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return this.guiSystem.dispatchEventReversed(widget -> widget.mouseScrolled(x * guiScale, y * guiScale, scrollX * guiScale, scrollY * guiScale));
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        var e = convertKeyEvent(event);
        this.guiSystem.dispatchEventReversed(widget -> widget.keyPressed(e));
        if (event.key() == GLFW.GLFW_KEY_F10) {
            this.guiSystem.printLayout();
        }
        return super.keyPressed(event);
    }
    
    @Override
    public boolean keyReleased(KeyEvent event) {
        var e = convertKeyEvent(event);
        return this.guiSystem.dispatchEventReversed(widget -> widget.keyReleased(e));
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        var e = convertCharacterEvent(event);
        return this.guiSystem.dispatchEventReversed(widget -> widget.charTyped(e));
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        boolean handled = false;
        for (int i = this.guiSystem.screenLayers.size() - 1; i >= 0; i--) {
            var pair = this.guiSystem.screenLayers.get(i);
            var layer = pair.widget();
            if (!handled && layer.mouseMoved(x * guiScale, y * guiScale)) {
                handled = true;
                continue;
            }
            if (layer instanceof ContainerWidget acw) {
                acw.clearHoveredRecursive();
            } else {
                layer.setHovered(false);
            }
        }
    }
    
    @Override
    public boolean preeditUpdated(@Nullable PreeditEvent event) {
        return this.guiSystem.dispatchEventReversed(widget -> widget.preeditUpdated(event));
    }
    
    public static float tryGetScaleX(){
        if(XKLib.RENDER_CONTEXT.get().getGUIGraphics() instanceof B3dGuiGraphics guiGraphics){
            return guiGraphics.scale;
        }
        return 1;
    }
    
    public static float tryGetScaleY(){
        if(XKLib.RENDER_CONTEXT.get().getGUIGraphics() instanceof B3dGuiGraphics guiGraphics){
            return guiGraphics.scale;
        }
        return 1;
    }
    
}
