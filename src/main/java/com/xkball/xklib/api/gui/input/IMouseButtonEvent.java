package com.xkball.xklib.api.gui.input;

public interface IMouseButtonEvent extends IInputWithModifiers {

    double x();
    
    double y();
    
    int button();
    
    int modifiers();
    
    @Override
    default int input(){
        return this.button();
    }
}
