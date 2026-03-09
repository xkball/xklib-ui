package com.xkball.xklib.utils;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Queue;

public class FPSLimiter {
    
    private double fpsLimit = 60;
    private double lastFrameTime;
    private final Queue<Double> deltaTime = new ArrayDeque<>();
    
    public FPSLimiter() {
    }
    
    public FPSLimiter(double fpsLimit) {
        this.fpsLimit = fpsLimit;
    }
    
    public void tickFrame(){
        double d1 = GLFW.glfwGetTime();
        if(fpsLimit > 0){
            var d0 = lastFrameTime + 1f/fpsLimit;
            
            while (d1 < d0){
                GLFW.glfwWaitEventsTimeout(d0 - d1);
                d1 = GLFW.glfwGetTime();
            }
        }
        deltaTime.add(d1 - lastFrameTime);
        var all = timeSum();
        while (all > 1){
            deltaTime.remove();
            all = timeSum();
        }
        lastFrameTime = GLFW.glfwGetTime();
    }
    
    public void setFpsLimit(double fpsLimit) {
        this.fpsLimit = fpsLimit;
    }
    
    private double timeSum(){
        var all = 0d;
        for(var time : deltaTime){
            all += time;
        }
        return all;
    }
    
    public double getFPS(){
        return deltaTime.isEmpty() ? 0 : timeSum() / deltaTime.size();
    }
    
}
