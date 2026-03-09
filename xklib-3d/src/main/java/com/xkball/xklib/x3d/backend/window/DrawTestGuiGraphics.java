package com.xkball.xklib.x3d.backend.window;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.resource.ResourceLocation;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class DrawTestGuiGraphics extends WindowAppBase {
    
    @Override
    public void render() {
        super.render();
        var guiGraphics = XKLib.RENDER_CONTEXT.get().getGUIGraphics();
        guiGraphics.fill(20,100,400,200,0xFFFF0000);
        guiGraphics.fill(200,60,300,800,0xFF0000ff);
        guiGraphics.blit(ResourceLocation.of("textures/img.png"),10,10,250,250,0,1,0,1);
        guiGraphics.fillGradient(100,230,400,500,0xFFFF0000,0xFF00FF00);
        guiGraphics.drawString("114514abcdEFG", 100, 100, 0xFFFFFF00);
        guiGraphics.drawString("中国智造 惠及全球", 100, 140, 0xFFFFFF00);
        guiGraphics.fillRounded(800, 400, 1000, 600, 0xFF0000FF, 80);
        guiGraphics.fillRounded(800, 200, 1000, 300, 0xFF0000FF, 20);
        guiGraphics.fillRounded(800, 100, 1000, 150, 0xFF0000FF, 25);
        guiGraphics.renderLine(0,0,1000,1000,0xFF000000);
        
        float startX = 50;
        float startY = 700;
        float amplitude = 100;
        float frequency = 0.02f;
        int segments = 200;
        float segmentWidth = 800f / segments;
        
        for (int i = 0; i < segments; i++) {
            float x0 = startX + i * segmentWidth;
            float y0 = startY + (float) Math.sin(x0 * frequency) * amplitude;
            float x1 = startX + (i + 1) * segmentWidth;
            float y1 = startY + (float) Math.sin(x1 * frequency) * amplitude;
            
            guiGraphics.renderLine(x0, y0, x1, y1, 0xff000000);
        }
        
        for (int i = 0; i < 90; i++) {
            guiGraphics.getPose().pushMatrix();
            guiGraphics.getPose().rotate((float) Math.toRadians(i));
            guiGraphics.renderLine(0,0, 1000,0,0xffff0000);
            var p = guiGraphics.getPose().transformPosition(new Vector2f(1000,0));
            guiGraphics.getPose().popMatrix();
            guiGraphics.renderLine(1000,0,1000+p.x,p.y,0xff0000ff);
        }
        
        guiGraphics.draw();
    }
}
