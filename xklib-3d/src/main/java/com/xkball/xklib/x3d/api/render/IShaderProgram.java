package com.xkball.xklib.x3d.api.render;

import java.util.function.Consumer;

public interface IShaderProgram {
    
    IUniform getUniform(String name);
    
    void safeSetUniform(String name, Consumer<IUniform> uniformSetter);
}
