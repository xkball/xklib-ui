package com.xkball.xklib.ui.system;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ap.annotation.RegisterEvent;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.css.CascadingStyleSheets;
import com.xkball.xklib.ui.input.CharacterEvent;
import com.xkball.xklib.ui.input.KeyEvent;
import com.xkball.xklib.ui.input.MouseButtonEvent;
import com.xkball.xklib.ui.layout.FocusNode;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.screen.DebugScreen;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.utils.SimpleProfiler;
import com.xkball.xklib.x3d.backend.window.WindowEvent;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AvailableSpace;
import dev.vfyjxf.taffy.tree.TaffyTree;
import net.lenni0451.lambdaevents.EventHandler;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

@RegisterEvent
public class GuiSystem implements AutoCloseable {
    
    public static final ThreadLocal<GuiSystem> INSTANCE = new ThreadLocal<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiSystem.class);
    private static final long DOUBLE_CLICK_TIME = 200;
    public final List<WidgetEntry> screenLayers = new ArrayList<>();
    private final Queue<Runnable> treeUpdateQueue = new ConcurrentLinkedQueue<>();
    private final FocusManager focusManager;
    public TooltipData tooltip = new TooltipData();
    public DraggingWidgetData draggingWidget = new DraggingWidgetData();
    public long windowHandle;
    public int screenWidth;
    public int screenHeight;
    public WidgetTestFrame debugScreen;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;
    private int dragButton = -1;
    private long lastClickTime = 0;
    private boolean debug = false;
    private boolean isClosed = false;
    private IGUIGraphics graphics;
    
    public GuiSystem() {
        this.focusManager = new FocusManager();
        this.focusManager.root = new FocusNode(null);
    }
    
    @EventHandler
    public static void onWindowInit(WindowEvent.Init event) {
        var guiSystem = new GuiSystem();
        INSTANCE.set(guiSystem);
        guiSystem.initGLFWCallbacks(event.window.getHandle());
        guiSystem.setGraphics(XKLib.RENDER_CONTEXT.get().getGUIGraphics());
        guiSystem.resize(event.window.getWidth(), event.window.getHeight());
    }
    
    @EventHandler
    public static void onWindowResize(WindowEvent.Resize event) {
        INSTANCE.get().resize(event.window.getWidth(), event.window.getHeight());
    }
    
    public boolean isDebug() {
        return this.debug;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void resize(int width, int height) {
        LOGGER.debug("Resize GUI System to {}x{}", width, height);
        this.screenWidth = width;
        this.screenHeight = height;
        for (var layer : screenLayers) {
            layer.widget().markDirty();
        }
        CascadingStyleSheets.resizing = true;
    }
    
    public void setGraphics(IGUIGraphics graphics) {
        this.graphics = graphics;
    }
    
    public IGUIGraphics getGuiGraphics() {
        return this.graphics;
    }
    
    public void submitTreeUpdate(Runnable runnable) {
        this.treeUpdateQueue.offer(runnable);
    }
    
    public void initGLFWCallbacks(long windowHandle) {
        this.windowHandle = windowHandle;
        GLFW.glfwSetCursorPosCallback(windowHandle, this::onCursorPos);
        GLFW.glfwSetMouseButtonCallback(windowHandle, this::onMouseButton);
        GLFW.glfwSetScrollCallback(windowHandle, this::onScroll);
        GLFW.glfwSetKeyCallback(windowHandle, this::onKey);
        GLFW.glfwSetCharCallback(windowHandle, this::onChar);
//        GLFW.glfwSetCharModsCallback(windowHandle, this::onCharMods);
        GLFW.glfwSetWindowCloseCallback(windowHandle, l -> this.close());
    }
    
    public FocusManager getFocusManager() {
        return this.focusManager;
    }
    
    public boolean isShiftDown() {
        return GLFW.glfwGetKey(this.windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(this.windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }
    
    public boolean isCtrlDown() {
        return GLFW.glfwGetKey(this.windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(this.windowHandle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }
    
    public boolean isAltDown() {
        return GLFW.glfwGetKey(this.windowHandle, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(this.windowHandle, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
    }
    
    private void onCursorPos(long window, double x, double y) {
        if (window != this.windowHandle) {
            return;
        }
        
        double dx = x - this.lastMouseX;
        double dy = y - this.lastMouseY;
        this.lastMouseX = x;
        this.lastMouseY = y;
        
        if (this.isDragging) {
            MouseButtonEvent event = new MouseButtonEvent(x, y, this.dragButton, 0);
            this.dispatchEventReversed(widget -> widget.mouseDragged(event, dx, dy));
            if (this.haveDraggingWidget()) {
                this.dispatchEventReversed(widget -> widget.widgetDraggingHovered(event, this.draggingWidget.widget()));
            }
        } else {
            boolean handled = false;
            for (int i = this.screenLayers.size() - 1; i >= 0; i--) {
                var pair = this.screenLayers.get(i);
                var layer = pair.widget();
                if (!handled && layer.mouseMoved(x, y)) {
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
    }
    
    public void onMouseButton(long window, int button, int action, int mods) {
        if (window != this.windowHandle) {
            return;
        }
        
        MouseButtonEvent event = new MouseButtonEvent(this.lastMouseX, this.lastMouseY, button, mods);
        
        if (action == GLFW.GLFW_PRESS) {
            long currentTime = System.currentTimeMillis();
            boolean isDoubleClick = (currentTime - this.lastClickTime) < DOUBLE_CLICK_TIME;
            this.lastClickTime = currentTime;
            
            this.isDragging = true;
            this.dragButton = button;
            
            boolean handled = false;
            for (int i = this.screenLayers.size() - 1; i >= 0; i--) {
                var layer = this.screenLayers.get(i);
                if (!handled && layer.widget().mouseClicked(event, isDoubleClick)) {
                    handled = true;
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            this.isDragging = false;
            this.dragButton = -1;
            
            this.dispatchEventReversed(widget -> widget.mouseReleased(event));
            if (this.haveDraggingWidget()) {
                this.dispatchEventReversed(widget -> widget.widgetDropped(event, this.draggingWidget.widget()));
            }
        }
    }
    
    private void onScroll(long window, double scrollX, double scrollY) {
        if (window != this.windowHandle) {
            return;
        }
        
        this.dispatchEventReversed(widget -> widget.mouseScrolled(this.lastMouseX, this.lastMouseY, scrollX, scrollY));
    }
    
    private void onKey(long window, int key, int scancode, int action, int mods) {
        if (window != this.windowHandle) {
            return;
        }
        
        if (key == GLFW.GLFW_KEY_F3 && action == GLFW.GLFW_PRESS) {
            this.debug = !this.debug;
            LOGGER.debug("Debug mode: {}", this.debug);
            return;
        }
        
        if (key == GLFW.GLFW_KEY_F10 && action == GLFW.GLFW_PRESS) {
            this.printLayout();
        }
        
        if (key == GLFW.GLFW_KEY_F12 && action == GLFW.GLFW_PRESS) {
//            this.printLayout();
            if (this.debugScreen == null) {
                debugScreen = new WidgetTestFrame(() -> new DebugScreen(this));
                var thread = new Thread(() -> debugScreen.run());
                thread.setName("debug-screen-" + Thread.currentThread().getName());
                thread.start();
            } else {
                try {
                    debugScreen.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                debugScreen = null;
            }
            return;
        }
        
        KeyEvent event = new KeyEvent(key, scancode, mods);
        
        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
            this.dispatchEventReversed(widget -> widget.keyPressed(event));
        } else if (action == GLFW.GLFW_RELEASE) {
            this.dispatchEventReversed(widget -> widget.keyReleased(event));
        }
    }
    
    private void onChar(long window, int codepoint) {
        if (window != this.windowHandle) {
            return;
        }
        
        CharacterEvent event = new CharacterEvent(codepoint, 0);
        this.dispatchEventReversed(widget -> widget.charTyped(event));
    }
    
    private void onCharMods(long window, int codepoint, int mods) {
        if (window != this.windowHandle) {
            return;
        }
        
        CharacterEvent event = new CharacterEvent(codepoint, mods);
        this.dispatchEventReversed(widget -> widget.charTyped(event));
    }
    
    public boolean dispatchEventReversed(Function<Widget, Boolean> eventHandler) {
        for (int i = this.screenLayers.size() - 1; i >= 0; i--) {
            var layer = this.screenLayers.get(i);
            if (eventHandler.apply(layer.widget())) {
                return true;
            }
        }
        return false;
    }
    
    public void render() {
        this.render((int) this.lastMouseX, (int) this.lastMouseY, 0);
    }
    
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.graphics == null) {
            return;
        }
        var profiler = XKLib.RENDER_CONTEXT.get().getProfiler();
        profiler.push("gui update");
        synchronized (this) {
            this.processTreeUpdates();
            this.processLayoutUpdates();
            this.processTooltipUpdates(mouseX, mouseY);
            this.processDraggingWidgetUpdates(mouseX, mouseY);
            if (this.debugScreen != null && this.debugScreen.widget instanceof DebugScreen ds) {
                ds.updatePerformanceData(((SimpleProfiler) profiler).getData());
            }
        }
        CascadingStyleSheets.resizing = false;
        profiler.pushPop("gui render");
        for (var pair : this.screenLayers) {
            var layer = pair.widget();
            var visible = layer.visible();
            if (visible) {
                this.renderWidget(layer, mouseX, mouseY, partialTicks);
            }
        }
        if (this.tooltip.widgetEntry != null) {
            this.renderWidget(tooltip.widget(), mouseX, mouseY, partialTicks);
        }
        if (this.draggingWidget.widgetEntry != null) {
            this.renderWidget(draggingWidget.widget(), mouseX, mouseY, partialTicks);
        }
        this.graphics.draw();
        profiler.pop();
    }
    
    private void renderWidget(Widget widget, int mouseX, int mouseY, float partialTicks) {
        widget.renderBelow(this.graphics, mouseX, mouseY, partialTicks);
        this.graphics.layerUp();
        widget.render(this.graphics, mouseX, mouseY, partialTicks);
        this.graphics.layerUp();
        widget.renderAbove(this.graphics, mouseX, mouseY, partialTicks);
    }
    
    private void processTreeUpdates() {
        Runnable task;
        while ((task = this.treeUpdateQueue.poll()) != null) {
            task.run();
        }
    }
    
    private void processLayoutUpdates() {
        for (var layer : this.screenLayers) {
            var widget = layer.widget();
            var tree = layer.treeRoot();
            widget.updateStyle(widget.getStyleSheetAsRoot());
            tree.computeLayout(widget.getNodeId(), new TaffySize<>(AvailableSpace.definite(this.screenWidth), AvailableSpace.definite(this.screenHeight)));
            widget.resize(0, 0);
        }
    }
    
    private void processTooltipUpdates(int mouseX, int mouseY) {
        if (this.tooltip.parent == null || !this.tooltip.parent.isHovered()) {
            tooltip.parent = null;
            tooltip.widgetEntry = null;
        } else {
            tooltip.widget().updateStyle(tooltip.widget().getStyleSheetAsRoot());
            tooltip.tree().computeLayout(tooltip.widget().getNodeId(), new TaffySize<>(AvailableSpace.definite(this.screenWidth), AvailableSpace.definite(this.screenHeight)));
            var w = tooltip.widget().getLayout().contentBoxWidth();
            var h = tooltip.widget().getLayout().contentBoxHeight();
            var wLeft = this.screenWidth - mouseX;
            var hLeft = this.screenHeight - mouseY;
            var px = mouseX;
            var py = mouseY;
            if (w > wLeft) px = (int) (this.screenWidth - w);
            if (h > hLeft) py = (int) (this.screenHeight - h);
            tooltip.widget().resize(px, py);
        }
    }
    
    private void processDraggingWidgetUpdates(int mouseX, int mouseY) {
        if (this.draggingWidget.widgetEntry == null) return;
        draggingWidget.widget().updateStyle(draggingWidget.widget().getStyleSheetAsRoot());
        draggingWidget.tree().computeLayout(draggingWidget.widget().getNodeId(), new TaffySize<>(AvailableSpace.definite(this.screenWidth), AvailableSpace.definite(this.screenHeight)));
        draggingWidget.widget().resize(mouseX - draggingWidget.xOffset, mouseY - draggingWidget.yOffset);
    }
    
    public void addScreenLayer(Widget layer) {
        if (layer.getTree() == null) layer.asTreeRoot();
        layer.setGuiSystem(this);
        layer.init();
        layer.markDirty();
        this.screenLayers.add(new WidgetEntry(layer, layer.getTree()));
        this.focusManager.root.addChild(layer.getFocusNode());
    }
    
    public void insertLayerAfter(Widget layer, IGuiWidget after) {
        if (layer.getTree() == null) layer.asTreeRoot();
        layer.init();
        layer.markDirty();
        int idx = -1;
        for (int i = 0; i < this.screenLayers.size(); i++) {
            if (this.screenLayers.get(i).widget().equals(after)) {
                idx = i;
                break;
            }
        }
        if (idx >= 0) {
            this.screenLayers.add(idx + 1, new WidgetEntry(layer, layer.getTree()));
        } else {
            this.screenLayers.add(new WidgetEntry(layer, layer.getTree()));
        }
        this.focusManager.root.addChild(layer.getFocusNode());
    }
    
    public void removeScreenLayer(Widget layer) {
        var removed = this.screenLayers.removeIf(p -> p.widget().equals(layer));
        if (removed) {
            layer.onRemove();
            this.focusManager.root.removeChild(layer.getFocusNode());
        }
        
    }
    
    public void printLayout() {
        for (int i = 0; i < screenLayers.size(); i++) {
            var layer = screenLayers.get(i).widget();
            LOGGER.info("=== Layer {} [{}] ===", i, layer.getClass().getSimpleName());
            printWidgetTree(layer, 0);
        }
    }
    
    private void printWidgetTree(Widget widget, int depth) {
        var indent = "  ".repeat(depth);
        var style = widget.getStyle();
        var layout = widget.getLayout();
        var styleStr = "display=" + style.display
                + " size=" + style.size;
        String layoutStr;
        if (layout != null) {
            layoutStr = "x=" + layout.contentBoxX()
                    + " y=" + layout.contentBoxY()
                    + " w=" + layout.contentBoxWidth()
                    + " h=" + layout.contentBoxHeight();
        } else {
            layoutStr = "null";
        }
        LOGGER.info("{}[{}] style=[{}] layout=[{}]", indent, widget.getClass().getSimpleName(), styleStr, layoutStr);
        for (var child : widget.getChildren()) {
            if (child instanceof Widget w) {
                printWidgetTree(w, depth + 1);
            }
        }
    }
    
    public boolean isClosed() {
        return this.isClosed;
    }
    
    @Override
    public void close() {
        if (this.isClosed()) return;
        this.isClosed = true;
        for (var screenLayer : this.screenLayers) {
            screenLayer.widget().onRemove();
        }
        if (this.debugScreen != null && !this.debugScreen.isClosed()) {
            try {
                this.debugScreen.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public boolean haveDraggingWidget() {
        return this.draggingWidget.widgetEntry != null;
    }
    
    public void removeDraggingWidget() {
        this.draggingWidget.widgetEntry = null;
    }
    
    public void setDraggingWidget(Widget widget) {
        widget.asTreeRoot();
        widget.setGuiSystem(this);
        widget.markDirty();
        this.draggingWidget.widgetEntry = new WidgetEntry(widget, widget.getTree());
        this.draggingWidget.xOffset = (float) (lastMouseX - widget.getX());
        this.draggingWidget.yOffset = (float) (lastMouseY - widget.getY());
    }
    
    public void setTooltip(Widget tooltip, Widget parent) {
        this.tooltip.parent = parent;
        tooltip.asTreeRoot();
        tooltip.setGuiSystem(this);
        tooltip.init();
        tooltip.markDirty();
        this.tooltip.widgetEntry = new WidgetEntry(tooltip, tooltip.getTree());
    }
    
    public record WidgetEntry(Widget widget, TaffyTree treeRoot) {
    
    }
    
    public static class DraggingWidgetData {
        WidgetEntry widgetEntry;
        float xOffset;
        float yOffset;
        
        public Widget widget() {
            return widgetEntry.widget();
        }
        
        public TaffyTree tree() {
            return widgetEntry.treeRoot();
        }
        
    }
    
    public static class TooltipData {
        WidgetEntry widgetEntry;
        Widget parent;
        
        public Widget widget() {
            return widgetEntry.widget();
        }
        
        public TaffyTree tree() {
            return widgetEntry.treeRoot();
        }
        
    }
}
