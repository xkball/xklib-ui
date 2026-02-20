package com.xkball.xklib.ui.backend.window;

import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.gl.OpenGLWorkaround;

public class DrawTestGuiGraphics extends WindowAppBase{
    
    @Override
    public void render() {
        super.render();
        var guiRenderer = OpenGLWorkaround.guiRenderer;
        var guiGraphics = guiRenderer.getGuiGraphics();
        guiGraphics.fill(20,100,400,200,0xFFFF0000);
        guiGraphics.fill(200,60,300,800,0xFF0000ff);
        guiGraphics.blit(ResourceLocation.of("textures/img.png"),10,10,250,250,0,1,0,1);
        guiGraphics.fillGradient(100,230,400,500,0xFFFF0000,0xFF00FF00);
        guiGraphics.drawString(OpenGLWorkaround.font,"114514abcdEFG", 100, 100, 0xFFFFFF00);
        guiGraphics.drawString(OpenGLWorkaround.font,"中国智造 惠及全球", 100, 140, 0xFFFFFF00);
        guiGraphics.fillRounded(800, 400, 1000, 600, 0xFF0000FF, 80);
        guiGraphics.fillRounded(800, 200, 1000, 300, 0xFF0000FF, 20);
        guiGraphics.fillRounded(800, 100, 1000, 150, 0xFF0000FF, 25);
        guiRenderer.draw();
    }
}
