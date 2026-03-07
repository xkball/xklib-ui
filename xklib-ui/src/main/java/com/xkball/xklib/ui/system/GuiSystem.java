package com.xkball.xklib.ui.system;

import com.google.auto.service.AutoService;
import com.xkball.xklib.ap.annotation.RegisterEvent;
import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.layout.FocusNode;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.input.CharacterEvent;
import com.xkball.xklib.ui.input.KeyEvent;
import com.xkball.xklib.ui.input.MouseButtonEvent;
import com.xkball.xklib.ui.widget.ContainerWidget;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.utils.Pair;
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
public class GuiSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuiSystem.class);
    public static final ThreadLocal<GuiSystem> INSTANCE = new ThreadLocal<>();
    
    public final List<Pair<Widget, TaffyTree>> screenLayers = new ArrayList<>();
    
    private long windowHandle;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;
    private int dragButton = -1;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME = 200;
    public int screenWidth;
    public int screenHeight;
    private boolean debug = false;
    private final Queue<Runnable> treeUpdateQueue = new ConcurrentLinkedQueue<>();
    private IGUIGraphics graphics;
    private final FocusManager focusManager;
    
    @EventHandler
    public static void onWindowInit(WindowEvent.Init event){
        var guiSystem = new GuiSystem();
        INSTANCE.set(guiSystem);
        guiSystem.initGLFWCallbacks(event.window.getHandle());
        guiSystem.setGraphics(XKLib.RENDER_CONTEXT.get().getGUIGraphics());
        guiSystem.resize(event.window.getWidth(), event.window.getHeight());
    }
    
    @EventHandler
    public static void onWindowResize(WindowEvent.Resize event){
        INSTANCE.get().resize(event.window.getWidth(), event.window.getHeight());
    }
    
    public GuiSystem() {
        this.focusManager = new FocusManager();
        this.focusManager.root = new FocusNode(null);
    }
    
    public boolean isDebug() {
        return this.debug;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void resize(int width, int height){
        LOGGER.debug("Resize GUI System to {}x{}", width, height);
        this.screenWidth = width;
        this.screenHeight = height;
        for(var layer : screenLayers) {
            layer.getFirst().markDirty();
        }
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
    }
    
    public FocusManager getFocusManager() {
        return this.focusManager;
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
        } else {
            boolean handled = false;
            for (int i = this.screenLayers.size() - 1; i >= 0; i--) {
                var pair = this.screenLayers.get(i);
                var layer = pair.getFirst();
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
    
    private void onMouseButton(long window, int button, int action, int mods) {
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
                if (!handled && layer.getFirst().mouseClicked(event, isDoubleClick)) {
                    handled = true;
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            this.isDragging = false;
            this.dragButton = -1;
            
            this.dispatchEventReversed(widget -> widget.mouseReleased(event));
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
        
        if (key == GLFW.GLFW_KEY_F12 && action == GLFW.GLFW_PRESS) {
            //TODO: toggle debug widget
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
    
    private void dispatchEventReversed(Function<Widget, Boolean> eventHandler) {
        for (int i = this.screenLayers.size() - 1; i >= 0; i--) {
            var layer = this.screenLayers.get(i);
            if (eventHandler.apply(layer.getFirst())) {
                break;
            }
        }
    }
    
    public void render(){
        this.render((int) this.lastMouseX, (int) this.lastMouseY, 0);
    }
    
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.graphics == null) {
            return;
        }
        
        this.processTreeUpdates();
        
        this.processLayoutUpdates();
        
        for (var pair : this.screenLayers) {
            var layer = pair.getFirst();
            var visible = layer.visible();
            if (visible) {
                layer.renderBelow(this.graphics, mouseX, mouseY, partialTicks);
                this.graphics.layerUp();
                layer.render(this.graphics, mouseX, mouseY, partialTicks);
                this.graphics.layerUp();
                layer.renderAbove(this.graphics, mouseX, mouseY, partialTicks);
                this.graphics.layerUp();
            }
        }
        this.graphics.draw();
    }
    
    private void processTreeUpdates() {
        Runnable task;
        while ((task = this.treeUpdateQueue.poll()) != null) {
            task.run();
        }
    }
    
    private void processLayoutUpdates() {
        for(var layer : this.screenLayers){
            var widget = layer.getFirst();
            var tree = layer.getSecond();
//            if(tree.needsVisit(widget.getNodeId())){
                tree.computeLayout(widget.getNodeId(), new TaffySize<>(AvailableSpace.definite(this.screenWidth), AvailableSpace.definite(this.screenHeight)));
                layer.getFirst().resize(0,0);
//            }
        }
    }
    
    public void addScreenLayer(Widget layer) {
        if(layer.getTree() == null) layer.asTreeRoot();
        layer.init();
        layer.markDirty();
        this.screenLayers.add(Pair.of(layer, layer.getTree()));
        this.focusManager.root.addChild(layer.getFocusNode());
    }
    
    public void removeScreenLayer(Widget layer) {
        this.screenLayers.removeIf(p -> p.getFirst().equals(layer));
        this.focusManager.root.removeChild(layer.getFocusNode());
    }
}
