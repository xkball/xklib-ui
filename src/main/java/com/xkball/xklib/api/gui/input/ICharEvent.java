package com.xkball.xklib.api.gui.input;

public interface ICharEvent {

    int codepoint();
    
    int modifiers();
    
    String codepointAsString();
}
