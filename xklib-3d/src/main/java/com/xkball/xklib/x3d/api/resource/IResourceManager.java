package com.xkball.xklib.x3d.api.resource;

import com.xkball.xklib.resource.ClasspathResourceManager;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.utils.Lazy;

import java.util.List;
import java.util.Map;

public interface IResourceManager {
    
    Lazy<IResourceManager> INSTANCE = Lazy.of(ClasspathResourceManager::new);

    static IResourceManager getInstance(){
        return INSTANCE.get();
    }
    
    IResource getResource(ResourceLocation location);

    List<IResource> getResourceStack(ResourceLocation location);

    Map<ResourceLocation, List<IResource>> listResourceStacks(ResourceLocation location);
}
