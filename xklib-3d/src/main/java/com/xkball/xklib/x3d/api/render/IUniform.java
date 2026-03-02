package com.xkball.xklib.x3d.api.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface IUniform {
    
    void set(float x);
    
    void set(float x, float y);
    
    void set(float x, float y, float z);
    
    void set(Vector3f vector);
    
    void set(float x, float y, float z, float w);
    
    void set(Vector4f vector);
    
    void set(Matrix3f matrix);
    
    void set(Matrix4f matrix);
}
