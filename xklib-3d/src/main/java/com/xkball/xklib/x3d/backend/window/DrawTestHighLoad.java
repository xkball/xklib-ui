package com.xkball.xklib.x3d.backend.window;

import com.xkball.xklib.XKLib;

import java.util.concurrent.ThreadLocalRandom;

public class DrawTestHighLoad extends WindowAppBase{
    private ThreadLocalRandom random = ThreadLocalRandom.current();
    @Override
    public void render() {
        super.render();
        var guiGraphics = XKLib.RENDER_CONTEXT.get().getGUIGraphics();
        var w = XKLib.RENDER_CONTEXT.get().getWindow().getWidth();
        var h = XKLib.RENDER_CONTEXT.get().getWindow().getHeight();
        for (int i = 0; i < 1000; i++) {
            var x1 = random.nextFloat() * w;
            var y1 = random.nextFloat() * h;
            guiGraphics.fill(x1,y1,x1+random.nextFloat()*5,y1+random.nextFloat()*5,random.nextInt()|0XFF000000);
        }
        guiGraphics.drawString(""+XKLib.RENDER_CONTEXT.get().getProfiler().getTime("frame"),100,100,0xff000000);
        guiGraphics.draw();
    }
}
