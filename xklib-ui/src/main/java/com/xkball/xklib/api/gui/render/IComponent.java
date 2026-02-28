package com.xkball.xklib.api.gui.render;

import com.xkball.xklib.api.annotation.NoImplInMinecraft;

public interface IComponent {
    
    static IComponent literal(String literal){
        return () -> literal;
    }
    
    @NoImplInMinecraft
    String visit();
    
}
