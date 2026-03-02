package com.xkball.xklib;

import com.xkball.xklib.x3d.api.event.IEvent;
import com.xkball.xklib.x3d.api.render.IRenderContext;
import net.lenni0451.lambdaevents.LambdaManager;
import net.lenni0451.lambdaevents.generator.LambdaMetaFactoryGenerator;

public class XKLib {
    public static final String NAME = "xklib";
    public static final ThreadLocal<IRenderContext> RENDER_CONTEXT = new ThreadLocal<>();
    public static final LambdaManager EVENT_BUS = LambdaManager.threadSafe(new LambdaMetaFactoryGenerator())
            .setEventFilter((clazz, _) -> clazz.isAssignableFrom(IEvent.class));
    
    
}
