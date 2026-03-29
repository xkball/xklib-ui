package com.xkball.xklib.utils.math;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import org.joml.Vector4f;

public class CubicSpline2D {
    
    public static CubicSpline2D BEZIER = new CubicSpline2D(
      new Matrix4d(
               1, 0, 0,0,
              -3, 3, 0,0,
               3,-6, 3,0,
              -1, 3,-3,1
      ));

    private final Matrix4d param;
    private final Matrix4f paramF;
    
    public CubicSpline2D(Matrix4d param) {
        this.param = param;
        this.paramF = new Matrix4f(param);
    }
    
    public Vector2f getPoint(float t, Vector2f a, Vector2f b, Vector2f c, Vector2f d){
        var tv = new Vector4f(1, t, t*t, t*t*t);
        var pm = new Matrix4f(
                a.x,a.y,0,0,
                b.x,b.y,0,0,
                c.x,c.y,0,0,
                d.x,d.y,0,0);
        var res = tv.mul(paramF,new Vector4f()).mul(pm);
        return new Vector2f(res.x,res.y);
    }
    
    public Vector2d getPoint(double t, Vector2d a, Vector2d b, Vector2d c, Vector2d d) {
        var tv = new Vector4d(1, t, t*t, t*t*t);
        var pm = new Matrix4d(
                a.x,a.y,0,0,
                b.x,b.y,0,0,
                c.x,c.y,0,0,
                d.x,d.y,0,0);
        var res = tv.mul(param,new Vector4d()).mul(pm);
        return new Vector2d(res.x,res.y);
    }
}
