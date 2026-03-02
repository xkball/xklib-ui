package com.xkball.xklib.ui.render;

import com.xkball.xklib.annotation.NoImplInMinecraft;

public interface IComponent {
    
    static IComponent literal(String literal){
        return () -> literal;
    }
    
    @NoImplInMinecraft
    String visit();
    
}
