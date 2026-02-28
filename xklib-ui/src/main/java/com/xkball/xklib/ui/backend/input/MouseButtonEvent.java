package com.xkball.xklib.ui.backend.input;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;

public record MouseButtonEvent(double x, double y, int button, int modifiers) implements IMouseButtonEvent {

}