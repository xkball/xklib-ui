package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.input.CharacterEvent;
import com.xkball.xklib.ui.backend.input.KeyEvent;
import com.xkball.xklib.ui.backend.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

public class GuiSystem {

    public final List<AbstractWidget> screenLayers = new ArrayList<>();
    
    private long windowHandle;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;
    private int dragButton = -1;
    private double dragStartX;
    private double dragStartY;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME = 500;
    public int screenWidth;
    public int screenHeight;
    
    private final Queue<Runnable> treeUpdateQueue = new ConcurrentLinkedQueue<>();
    
    private IGUIGraphics graphics;
    
    public GuiSystem() {
    
    }
    
    public void resize(int width, int height){
        this.screenWidth = width;
        this.screenHeight = height;
        for(var layer : this.screenLayers){
            layer.markDirty();
        }
    }
    
    public void setGraphics(IGUIGraphics graphics) {
        this.graphics = graphics;
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
                AbstractWidget layer = this.screenLayers.get(i);
                if (!handled && layer.mouseMoved(x, y)) {
                    handled = true;
                    continue;
                }
                if (layer instanceof AbstractContainerWidget acw) {
                    acw.clearHoveredRecursive();
                } else {
                    layer.hovered = false;
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
            this.dragStartX = this.lastMouseX;
            this.dragStartY = this.lastMouseY;
            
            boolean handled = false;
            for (int i = this.screenLayers.size() - 1; i >= 0; i--) {
                AbstractWidget layer = this.screenLayers.get(i);
                if (!handled && layer.mouseClicked(event, isDoubleClick)) {
                    handled = true;
                    continue;
                }
                if (layer instanceof AbstractContainerWidget acw) {
                    acw.clearFocusedRecursive();
                } else {
                    layer.focused = false;
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
    
    private void dispatchEventReversed(Function<AbstractWidget, Boolean> eventHandler) {
        for (int i = this.screenLayers.size() - 1; i >= 0; i--) {
            AbstractWidget layer = this.screenLayers.get(i);
            if (eventHandler.apply(layer)) {
                break;
            }
        }
    }
    
    private void dispatchEventToAll(Consumer<AbstractWidget> eventHandler) {
        for (AbstractWidget layer : this.screenLayers) {
            eventHandler.accept(layer);
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
        
        for (AbstractWidget layer : this.screenLayers) {
            if (layer.visible()) {
                layer.render(this.graphics, mouseX, mouseY, partialTicks);
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
            layer.setPosition(0,0);
            layer.setSize(this.screenWidth,this.screenHeight);
            layer.visitWidgets(w -> {
                if (w.isDirty()){
                    w.markDirty(false);
                    w.resize();
                }
            });
        }
    }
    
    public void addScreenLayer(AbstractWidget layer) {
        this.screenLayers.add(layer);
    }
    
    public void removeScreenLayer(AbstractWidget layer) {
        this.screenLayers.remove(layer);
    }
}
