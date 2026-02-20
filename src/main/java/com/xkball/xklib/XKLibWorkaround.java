package com.xkball.xklib;

import com.xkball.xklib.api.render.IRenderPipelineSource;
import com.xkball.xklib.api.resource.IResourceManager;
import com.xkball.xklib.resource.ClasspathResourceManager;
import com.xkball.xklib.ui.backend.OpenGLRenderPipelinesSource;
import com.xkball.xklib.ui.widget.GuiSystem;
import com.xkball.xklib.utils.TickHelper;

public class XKLibWorkaround {
    public static final IResourceManager resourceManager = new ClasspathResourceManager();
    public static final TickHelper tickHelper = new TickHelper(20);
    public static final IRenderPipelineSource renderPipelineSource = new OpenGLRenderPipelinesSource();
    public static final GuiSystem gui = new GuiSystem();
}
