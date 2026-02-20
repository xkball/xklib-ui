package com.xkball.xklib.ui.backend.input;

import com.xkball.xklib.api.gui.input.ICharEvent;

public record CharacterEvent(int codepoint, int modifiers) implements ICharEvent {
    
    public String codepointAsString() {
        return Character.toString(this.codepoint);
    }
    
}