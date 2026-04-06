package com.xkball.xklibmc.api.client.mixin;


import com.xkball.xklibmc.client.b3d.uniform.SSBOIndexStorage;

import java.util.Map;

public interface IExtendedGLProgram {
    
    Map<String, SSBOIndexStorage> dysonCubeProgram$getSSBOByName();
    
    static IExtendedGLProgram cast(Object obj){
        return (IExtendedGLProgram)obj;
    }
}
