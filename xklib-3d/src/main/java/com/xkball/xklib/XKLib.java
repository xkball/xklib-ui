package com.xkball.xklib;

import com.xkball.xklib.utils.RegisterEventHandler;
import com.xkball.xklib.x3d.api.event.IEvent;
import com.xkball.xklib.x3d.api.render.IRenderContext;
import com.xkball.xklib.x3d.backend.window.DrawTestGuiGraphics;
import com.xkball.xklib.x3d.backend.window.DrawTestText;
import net.lenni0451.lambdaevents.LambdaManager;
import net.lenni0451.lambdaevents.generator.LambdaMetaFactoryGenerator;

public class XKLib {
    public static final String NAME = "xklib";
    public static final ThreadLocal<IRenderContext> RENDER_CONTEXT = new ThreadLocal<>();
    public static final LambdaManager EVENT_BUS = LambdaManager.threadSafe(new LambdaMetaFactoryGenerator())
            .setEventFilter((clazz, _) -> IEvent.class.isAssignableFrom(clazz));
    public static final boolean ON_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
    
    static {
        RegisterEventHandler.runRegisterEvent();
    }
    
    public static void main(String[] args) {
        new DrawTestGuiGraphics().run();
    }
}
