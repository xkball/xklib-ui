package com.xkball.xklib.utils.math;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import org.joml.Vector4f;

//https://www.youtube.com/watch?v=jvPPXbo87ds
public class CubicSpline2D {
    
    public static CubicSpline2D BEZIER = new CubicSpline2D(
            new Matrix4d(
               1, 0, 0,0,
              -3, 3, 0,0,
               3,-6, 3,0,
              -1, 3,-3,1
            ));
    
    public static CubicSpline2D HERMITE = new CubicSpline2D(
            new Matrix4d(
                     1, 0,  0, 0,
                     0, 1,  0, 0,
                    -3,-2,  3,-1,
                     2, 1 ,-2, 1
            )){
        @Override
        public void transformInput(Vector2f a, Vector2f b, Vector2f c, Vector2f d) {
            b.sub(a);
            d.sub(c);
        }
        
        @Override
        public void transformInput(Vector2d a, Vector2d b, Vector2d c, Vector2d d) {
            b.sub(a);
            d.sub(c);
        }
    };
    
    public static CubicSpline2D CATMULL_ROM = cardinal(0.5);
    
    public static CubicSpline2D B = new CubicSpline2D(
            new Matrix4d(
                    1/6d, 4/6d, 1/6d,    0,
                    -0.5,    0,  0.5,    0,
                     0.5,   -1,  0.5,    0,
                   -1/6d,  0.5, -0.5, 1/6d
            )
    );
    
    public static CubicSpline2D cardinal(double scale){
        return new CubicSpline2D(
                new Matrix4d(
                              0,       1,         0,     0,
                         -scale,       0,     scale,     0,
                        scale*2, scale-3, 3-scale*2,-scale,
                         -scale, 2-scale,   scale-2, scale
                )
        );
    }
    

    private final Matrix4d param;
    private final Matrix4f paramF;
    
    public CubicSpline2D(Matrix4d param) {
        this.param = param;
        this.paramF = new Matrix4f(param);
    }
    
    public void transformInput(Vector2f a, Vector2f b, Vector2f c, Vector2f d) {
    }
    
    public void transformInput(Vector2d a, Vector2d b, Vector2d c, Vector2d d) {
    }
    
    public Vector2f getPoint(float t, Vector2f a, Vector2f b, Vector2f c, Vector2f d){
        this.transformInput(a,b,c,d);
        var tv = new Vector4f(1, t, t*t, t*t*t);
        var pm = new Matrix4f(
                a.x,a.y,0,0,
                b.x,b.y,0,0,
                c.x,c.y,0,0,
                d.x,d.y,0,0);
        var res = tv.mul(paramF).mul(pm);
        return new Vector2f(res.x,res.y);
    }
    
    public Vector2d getPoint(double t, Vector2d a, Vector2d b, Vector2d c, Vector2d d) {
        this.transformInput(a,b,c,d);
        var tv = new Vector4d(1, t, t*t, t*t*t);
        var pm = new Matrix4d(
                a.x,a.y,0,0,
                b.x,b.y,0,0,
                c.x,c.y,0,0,
                d.x,d.y,0,0);
        var res = tv.mul(param).mul(pm);
        return new Vector2d(res.x,res.y);
    }
}
