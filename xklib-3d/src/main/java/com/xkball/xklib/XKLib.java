package com.xkball.xklib;

import com.xkball.xklib.utils.RegisterEventHandler;
import com.xkball.xklib.x3d.api.event.IEvent;
import com.xkball.xklib.x3d.api.render.IRenderContext;
import net.lenni0451.lambdaevents.LambdaManager;
import net.lenni0451.lambdaevents.generator.LambdaMetaFactoryGenerator;

import java.lang.management.ManagementFactory;
import java.util.Scanner;

public class XKLib {
    public static final String NAME = "xklib";
    public static final ThreadLocal<IRenderContext> RENDER_CONTEXT = new ThreadLocal<>();
    public static final LambdaManager EVENT_BUS = LambdaManager.threadSafe(new LambdaMetaFactoryGenerator())
            .setEventFilter((clazz, _) -> IEvent.class.isAssignableFrom(clazz));
    public static final boolean ON_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
    public static final boolean IS_RUNNING_WITH_JDWP = ManagementFactory.getRuntimeMXBean().getInputArguments().stream().anyMatch(str -> str.startsWith("-agentlib:jdwp"));
    public static final boolean IS_DEBUG = IS_RUNNING_WITH_JDWP;
    static {
        RegisterEventHandler.runRegisterEvent();
    }
    
//    public static void main(String[] args) {
//        new DrawTestHighLoad().run();
//    }
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        for (int i = 0; i < n; i++) {
            int a = in.nextInt();
            int b = in.nextInt();
            if(b > a) System.out.println("YES");
            else System.out.println(a % (b+1) == 0 ? "NO" : "YES");
        }
    }
}
