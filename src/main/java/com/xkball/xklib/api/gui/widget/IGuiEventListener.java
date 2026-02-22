package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.layout.ScreenRectangle;

public interface IGuiEventListener {
    default boolean mouseMoved(double x, double y) {
        return false;
    }

    default boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    default boolean mouseReleased(IMouseButtonEvent event) {
        return false;
    }

    default boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
        return false;
    }

    default boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return false;
    }

    default boolean keyPressed(IKeyEvent event) {
        return false;
    }

    default boolean keyReleased(IKeyEvent event) {
        return false;
    }

    default boolean charTyped(ICharEvent event) {
        return false;
    }

    default boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    boolean isFocusable();
    
    void setFocused(final boolean focused);

    boolean isFocused();

    default boolean shouldTakeFocusAfterInteraction() {
        return true;
    }

    default ScreenRectangle getRectangle() {
        return ScreenRectangle.empty();
    }


}