package com.xkball.xklib.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.annotation.OnlyImplInMinecraft;
import com.xkball.xklib.antlr.css.CssParser;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IAbsoluteLayoutElement;
import com.xkball.xklib.ui.layout.FocusNode;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IGuiEventListener;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.IRenderable;
import com.xkball.xklib.ui.css.CascadingStyleSheets;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.utils.XKLibUtils;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.tree.NodeId;
import dev.vfyjxf.taffy.tree.TaffyTree;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class Widget implements IGuiWidget, IRenderable, IGuiEventListener, IAbsoluteLayoutElement {
    
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    public float absoluteX;
    public float absoluteY;
    public boolean enabled = true;
    public boolean visible = true;
    public boolean hovered = false;
    public String cssId = XKLibUtils.objName(this);
    public String cssType = XKLibUtils.objClassName(this);
    public String cssClass = "";
    protected boolean overflow = true;
    
    public TaffyTree tree = null;
    public TaffyStyle style = new TaffyStyle();
    public NodeId nodeId = null;
    protected IGuiWidget parent = null;
    protected FocusNode focusNode;
    private GuiSystem guiSystem;
    protected CascadingStyleSheets styleSheetAsRoot;
    protected final CascadingStyleSheets.Inline styleSheetAsSelf;
    protected IStyleSheet styleSheet = new CascadingStyleSheets.SimpleStyleSheet();
    protected final Queue<Runnable> untilSetTree = new ArrayDeque<>();
    protected Supplier<Widget> tooltipFactory = () -> null;
    
    public Widget(){
        this(0, 0, 0, 0);
    }
    
    public Widget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.focusNode = new FocusNode(null);
        this.focusNode.widget = this;
        this.focusNode.setCanTakePrimaryFocus(this.isFocusable());
        this.styleSheetAsRoot =  new CascadingStyleSheets();
        this.styleSheetAsSelf = new CascadingStyleSheets.Inline();
    }
    
    @Override
    public IGuiWidget asRootStyle(String style) {
        this.styleSheetAsRoot = CssParser.parse(style);
        return this;
    }
    
    @Override
    public Widget inlineStyle(String style){
        if ((style.isEmpty())) return this;
        style ="* { %s }".formatted(style);
        var sheets = CssParser.parse(style).sheets();
        if(sheets.size() == 1){
            this.styleSheetAsSelf.addProperties(sheets.getFirst().properties());
        }
        return this;
    }
    
    protected void untilSetTree(Runnable runnable) {
        if(this.tree == null) {
            untilSetTree.add(runnable);
        }
        else {
            runnable.run();
        }
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        boolean wasMouseOver = this.isMouseOver(mouseX, mouseY);
        if (wasMouseOver && this.enabled && this.visible) {
            this.setHovered(true);
            return true;
        }
        this.setHovered(false);
        return false;
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if (this.isMouseOver(event.x(), event.y())) {
            GuiSystem.INSTANCE.get().getFocusManager().takeFocus(this.getFocusNode());
            return this.onMouseClicked(event, doubleClick);
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(IMouseButtonEvent event) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if(this.isMouseOver(event.x(),event.y())){
            return this.onMouseReleased(event);
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if(this.isMouseOver(event.x(),event.y())){
            return this.onMouseDragged(event, dx, dy);
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if (this.isMouseOver(x, y)) {
            return this.onMouseScrolled(x, y, scrollX, scrollY);
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(IKeyEvent event) {
        if (!this.enabled || !this.visible || !this.isPrimaryFocused()) {
            return false;
        }
        return this.onKeyPressed(event);
    }
    
    @Override
    public boolean keyReleased(IKeyEvent event) {
        if (!this.enabled || !this.visible || !this.isPrimaryFocused()) {
            return false;
        }
        return this.onKeyReleased(event);
    }
    
    @Override
    public boolean charTyped(ICharEvent event) {
        if (!this.enabled || !this.visible || !this.isPrimaryFocused()) {
            return false;
        }
        return this.onCharTyped(event);
    }
    
    @OnlyImplInMinecraft
    public boolean preeditUpdated(@Nullable Object event) {
        return false;
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.enabled && this.visible && getRectangle().containsPoint((int) mouseX, (int) mouseY);
    }
    
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        return false;
    }
    
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        return false;
    }
    
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        return false;
    }
    
    protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
        return false;
    }
    
    protected boolean onKeyPressed(IKeyEvent event) {
        return false;
    }
    
    protected boolean onKeyReleased(IKeyEvent event) {
        return false;
    }
    
    protected boolean onCharTyped(ICharEvent event) {
        return false;
    }
    
    @Override
    public String getCSSType() {
        return this.cssType;
    }
    
    @Override
    public Widget setCSSClassName(String name) {
        this.cssClass = name;
        return this;
    }
    
    @Override
    public String getCSSClassName() {
        return this.cssClass;
    }
    
    @Override
    public IGuiWidget setCSSId(String name) {
        this.cssId = name;
        return this;
    }
    
    @Override
    public String getCSSId() {
        return this.cssId;
    }
    
    @Override
    public void setX(float x) {
        this.x = x;
    }
    
    @Override
    public void setY(float y) {
        this.y = y;
    }
    
    @Override
    public float getX() {
        return this.x;
    }
    
    @Override
    public float getY() {
        return this.y;
    }
    
    @Override
    public void setWidth(float width) {
        this.width = width;
    }
    
    @Override
    public void setHeight(float height) {
        this.height = height;
    }
    
    @Override
    public float getWidth() {
        return this.width;
    }
    
    @Override
    public float getHeight() {
        return this.height;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public boolean enabled() {
        return this.enabled;
    }
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    @Override
    public boolean visible() {
        return this.visible;
    }
    
    @Override
    public void setHovered(boolean hovered) {
        if(this.hovered != hovered) {
            this.hovered = hovered;
            this.onHoverChanged(hovered);
        }
    }
    
    @Override
    public boolean isHovered() {
        return this.hovered;
    }
    
    @Override
    public FocusNode getFocusNode() {
        return this.focusNode;
    }
    
    @Override
    public void setNodeId(NodeId nodeId) {
        this.nodeId = nodeId;
        if(this.tree != null) {
            this.afterTreeAndNodeSet();
        }
    }
    
    @Override
    public NodeId getNodeId() {
        return this.nodeId;
    }
    
    @Override
    public boolean isFocused() {
        return this.focusNode.isFocused();
    }
    
    @Override
    public boolean isPrimaryFocused() {
        return this.focusNode.isPrimaryFocused();
    }
    
    @Override
    public void markDirty() {
        if(this.tree == null) return;
        this.tree.markDirty(nodeId);
    }
    
    @Override
    public boolean isDirty() {
        if(this.tree == null) return false;
        return this.tree.isDirty(nodeId);
    }
    
    @Override
    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }
    
    @Override
    public boolean overflow() {
        return this.overflow;
    }
    
    public void afterTreeAndNodeSet(){
        this.tree.setStyle(this.nodeId, this.style);
        while (!this.untilSetTree.isEmpty()) {
            this.untilSetTree.poll().run();
        }
    }
    
    @Override
    public void setStyle(TaffyStyle style) {
        this.style = style;
        if(this.tree != null && this.nodeId != null) {
            this.tree.setStyle(this.nodeId, this.style);
        }
    }
    
    @Override
    public TaffyStyle getStyle() {
        return this.style;
    }
    
    @Override
    public void setTree(TaffyTree tree) {
        this.tree = tree;
        if(this.nodeId != null) {
            this.afterTreeAndNodeSet();
        }
    }
    
    @Override
    public TaffyTree getTree() {
        return this.tree;
    }
    
    @Override
    public ScreenRectangle getRectangle() {
        return IGuiWidget.super.getRectangle();
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        this.setVisible(this.style.display != TaffyDisplay.NONE);
        var layout = this.getLayout();
        if (layout != null) {
            this.setPosition(layout.contentBoxX() + offsetX, layout.contentBoxY() + offsetY);
            this.setSize(layout.contentBoxWidth(), layout.contentBoxHeight());
        }
    }
    
    @Override
    public final void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if(!this.visible) return;
        if (this.overflow()){
            this.doRender(graphics, mouseX, mouseY, a);
        }
        else {
            this.renderInScissor(graphics,() -> this.doRender(graphics, mouseX, mouseY, a));
        }
        
    }
    
    @Override
    public void renderAbove(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if(this.hovered){
            this.renderDebug(graphics, mouseX, mouseY);
        }
    }
    
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a){
        for(var p : this.styleSheet.renderableProperty()){
            p.render(this,graphics,mouseX,mouseY,a);
        }
    }
    
    @Override
    public void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
        int marginColor = 0x80FFA500;
        int paddingColor = 0x8000FF00;
        int contentColor = 0x800000FF;
        var layout = this.tree.getLayout(this.nodeId);
        if (layout != null) {
            var offsetX = this.x - layout.contentBoxX();
            var offsetY = this.y - layout.contentBoxY();
            graphics.renderOutline(layout.margin().left + offsetX, layout.margin().top + offsetY, layout.margin().right - layout.margin().left, layout.margin().bottom - layout.margin().top, marginColor);
            graphics.renderOutline(layout.padding().left + offsetX, layout.padding().top + offsetY, layout.padding().right - layout.padding().left, layout.padding().bottom - layout.padding().top, paddingColor);
        }
        graphics.renderOutline(this.x, this.y, this.width, this.height, contentColor);
    
    }
    
    public IGuiWidget getParent(){
        return this.parent;
    }
    
    public void setParent(IGuiWidget widget){
        this.parent = widget;
    }
    
    @Override
    public GuiSystem getGuiSystemAsync() {
        if(this.parent == null) return this.guiSystem;
        return getRoot().getGuiSystemAsync();
    }

    @Override
    public IStyleSheet getStyleSheet() {
        return this.styleSheet;
    }

    @Override
    public void setStyleSheet(IStyleSheet styleSheet) {
        if (this.styleSheet != styleSheet) {
            this.styleSheet = styleSheet;
            this.onStyleSheetChanged();
        }
    }

    @Override
    public CascadingStyleSheets getStyleSheetAsRoot() {
        return this.styleSheetAsRoot;
    }
    
    public void updateStyle(CascadingStyleSheets sheet){
        this.getStyleSheet().update(sheet,this);
    }

    @Override
    public CascadingStyleSheets getStyleSheetAsSelf() {
        return this.styleSheetAsSelf;
    }
    
    @Override
    public void submitTreeUpdateAsync(Runnable runnable) {
        this.untilSetTree(() -> getGuiSystemAsync().submitTreeUpdate(runnable));
    }
    
    public void setGuiSystem(GuiSystem system){
        this.guiSystem = system;
    }
    
    public void renderInScissor(IGUIGraphics graphics, Runnable renderer){
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        renderer.run();
        graphics.disableScissor();
    }
    
    @Override
    public float getAbsoluteX() {
        return this.absoluteX;
    }
    
    @Override
    public float getAbsoluteY() {
        return this.absoluteY;
    }
    
    @Override
    public void setAbsoluteX(float absoluteX) {
        this.absoluteX = absoluteX;
    }
    
    @Override
    public void setAbsoluteY(float absoluteY) {
        this.absoluteY = absoluteY;
    }
    
    @Override
    public void setAbsoluteSize(float x, float y) {
        this.absoluteX = x;
        this.absoluteY = y;
    }
    
    @Override
    public void setAbsoluteLayout(float x, float y, float width, float height) {
        this.absoluteX = x;
        this.absoluteY = y;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public @Nullable IGuiWidget createTooltip() {
        return this.tooltipFactory.get();
    }
    
    public Widget withTooltip(IComponent text){
        this.tooltipFactory = () -> {
            var font =  XKLib.RENDER_CONTEXT.get().getGUIGraphics().defaultFont();
            var w = font.width(text, 20);
            return new ContainerWidget()
                    .addChild(
                    new Label(text).inlineStyle(String.format("""
                            size: %spx %spx;
                            border: 2px;
                            border-color: -1;
                            text-color: -1;
                            text-height: 20;
                            text-align: center;
                            margin-left: 12px;
                            background-color: 0xdd263136;
                        """,w+16,30))
                    );

        };
        return this;
    }
}
