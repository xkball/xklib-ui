package com.xkball.xklib.ui.backend.input;

import com.xkball.xklib.api.gui.input.IKeyEvent;

public record KeyEvent(int key, int scancode, int modifiers) implements IKeyEvent {

}