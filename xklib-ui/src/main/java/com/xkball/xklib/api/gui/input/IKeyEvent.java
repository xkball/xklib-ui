package com.xkball.xklib.api.gui.input;

public interface IKeyEvent extends IInputWithModifiers {

    int key();
    
    int scancode();
    
    int modifiers();
    
    @Override
    default int input(){
        return this.key();
    }
}
