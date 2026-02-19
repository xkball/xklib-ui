package com.xkball.xklib.api.gui;

public interface IComponent {
    
    static IComponent literal(String literal){
        return () -> literal;
    }
    
    //在mc实现时 返回空字符串即可
    String visit();
    
}
