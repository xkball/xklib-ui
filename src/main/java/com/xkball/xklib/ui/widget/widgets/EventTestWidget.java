package com.xkball.xklib.ui.widget.widgets;

import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.AbstractWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTestWidget extends AbstractWidget {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventTestWidget.class);
    
    public EventTestWidget() {
        super();
    }
    
    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.render(graphics, mouseX, mouseY, a);
        graphics.drawString(mouseX + "," + mouseY, this.x, this.y, 0xFF000000);
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        LOGGER.info("Mouse clicked at ({}, {}) with button {}. Double click: {}", event.x(), event.y(), event.button(), doubleClick);
        return super.onMouseClicked(event, doubleClick);
    }
    
    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        LOGGER.info("Mouse released at ({}, {}) with button {}", event.x(), event.y(), event.button());
        return super.onMouseReleased(event);
    }
    
    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        LOGGER.info("Mouse dragged at ({}, {}) with button {}. Delta: ({}, {})", event.x(), event.y(), event.button(), dx, dy);
        return super.onMouseDragged(event, dx, dy);
    }
    
    @Override
    protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
        LOGGER.info("Mouse scrolled at ({}, {}). Scroll delta: ({}, {})", x, y, scrollX, scrollY);
        return super.onMouseScrolled(x, y, scrollX, scrollY);
    }
    
    @Override
    protected boolean onKeyPressed(IKeyEvent event) {
        LOGGER.info("Key pressed: {} ,scancode {}, mods {} ", event.key(), event.scancode(), event.modifiers());
        return super.onKeyPressed(event);
    }
    
    @Override
    protected boolean onKeyReleased(IKeyEvent event) {
        LOGGER.info("Key released: {} ,scancode {}, mods {} ", event.key(), event.scancode(), event.modifiers());
        return super.onKeyReleased(event);
    }
    
    @Override
    protected boolean onCharTyped(ICharEvent event) {
        LOGGER.info("Char typed: {} ,mods {} ", event.codepointAsString(), event.modifiers());
        return super.onCharTyped(event);
    }
}
