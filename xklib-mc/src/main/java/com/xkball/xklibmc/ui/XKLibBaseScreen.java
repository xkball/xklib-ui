package com.xkball.xklibmc.ui;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import com.xkball.xklibmc.x3d.backend.b3d.B3dRenderContext;
import dev.vfyjxf.taffy.style.TextAlign;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

@NonNullByDefault
public class XKLibBaseScreen extends Screen {
    
    protected GuiSystem guiSystem = new GuiSystem();
    protected float scaleX = 1;
    protected float scaleY = 1;
    
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
            guiSystem.windowHandle = XKLib.RENDER_CONTEXT.get().getWindow().getHandle();
        }
        this.resize_();
    }
    
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        var guiGraphics = (B3dGuiGraphics) XKLib.RENDER_CONTEXT.get().getGUIGraphics();
        guiGraphics.setInner(graphics);
        guiGraphics.scaleX = this.scaleX;
        guiGraphics.scaleY = this.scaleY;
        guiSystem.setGraphics(guiGraphics);
        graphics.pose().pushMatrix();
        graphics.pose().scale(1/scaleX,1/scaleY);
        guiSystem.render((int) (mouseX * scaleX), (int) (mouseY * scaleY), a);
        graphics.pose().popMatrix();
        super.extractRenderState(graphics, (int) (mouseX * scaleX), (int) (mouseY * scaleY), a);
    }
    
    public com.xkball.xklib.ui.input.MouseButtonEvent convertMouseButtonEvent(MouseButtonEvent event) {
        return new com.xkball.xklib.ui.input.MouseButtonEvent(event.x() * scaleX,event.y() * scaleY,event.button(),event.modifiers());
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
        var h = this.height;
        this.scaleX = actualW/(float)w;
        this.scaleY = actualH/(float)h;
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
        return this.guiSystem.dispatchEventReversed(widget -> widget.mouseReleased(e));
    }
    
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        var e = convertMouseButtonEvent(event);
        return this.guiSystem.dispatchEventReversed(widget -> widget.mouseDragged(e,dx * scaleX, dy * scaleY));
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return this.guiSystem.dispatchEventReversed(widget -> widget.mouseScrolled(x * scaleX, y * scaleY, scrollX * scaleX, scrollY * scaleY));
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
        return this.guiSystem.dispatchEventReversed(widget -> widget.keyReleased(e));
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        var e = convertCharacterEvent(event);
        return this.guiSystem.dispatchEventReversed(widget -> widget.charTyped(e));
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        this.guiSystem.dispatchEventReversed(widget -> widget.mouseMoved(x * scaleX,y * scaleY));
    }
    
    @Override
    public boolean preeditUpdated(@Nullable PreeditEvent event) {
        return this.guiSystem.dispatchEventReversed(widget -> widget.preeditUpdated(event));
    }
    
    public static float tryGetScaleX(){
        if(XKLib.RENDER_CONTEXT.get().getGUIGraphics() instanceof B3dGuiGraphics guiGraphics){
            return guiGraphics.scaleX;
        }
        return 1;
    }
    
    public static float tryGetScaleY(){
        if(XKLib.RENDER_CONTEXT.get().getGUIGraphics() instanceof B3dGuiGraphics guiGraphics){
            return guiGraphics.scaleY;
        }
        return 1;
    }
    
    public static Widget biPanelFrame(IComponent title, Widget left, Widget right){
        return new ContainerWidget()
                .inlineStyle("""
                        display: grid;
                        size: 100% 100%;
                        grid-template-columns: 100%;
                        grid-template-rows: 20rpx 1fr 20rpx;
                        """)
                .addChild(
                        new Label(title, TextAlign.CENTER)
                                .inlineStyle("""
                                        background-color: 0xaa111111;
                                        label-text-color: -2039584;
                                        label-text-scale: fit-to-max;
                                        """))
                .addChild(
                        new SplitContainer()
                                .setPanel(0, left)
                                .setPanel(1,right)
                                .setRatio(0,0.3f)
                )
                .addChild(new Widget().inlineStyle("""
                        background-color: 0xaa111111;
                        """));
    }
    
}
