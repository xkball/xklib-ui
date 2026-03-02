package com.xkball.xklib;

import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.api.resource.IResourceManager;
import com.xkball.xklib.resource.ClasspathResourceManager;
import com.xkball.xklib.ui.backend.OpenGLRenderPipelinesSource;
import com.xkball.xklib.ui.widget.GuiSystem;
import com.xkball.xklib.utils.TickHelper;

import java.util.Locale;

public class XKLib {
    public static final IResourceManager resourceManager = new ClasspathResourceManager();
    public static final TickHelper tickHelper = new TickHelper(20);
    public static final IRenderPipelineSource renderPipelineSource = new OpenGLRenderPipelinesSource();
    public static final GuiSystem gui = new GuiSystem();
    public static final boolean ON_MAC = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");
}
