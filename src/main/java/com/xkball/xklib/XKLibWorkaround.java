package com.xkball.xklib;

import com.xkball.xklib.api.resource.IResourceManager;
import com.xkball.xklib.resource.ClasspathResourceManager;
import com.xkball.xklib.utils.TickHelper;

public class XKLibWorkaround {
    public static final IResourceManager resourceManager = new ClasspathResourceManager();
    public static final TickHelper tickHelper = new TickHelper(20);
}
