package com.xkball.xklib.api.resource;

import com.xkball.xklib.resource.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface IResourceManager {

    IResource getResource(ResourceLocation location);

    List<IResource> getResourceStack(ResourceLocation location);

    Map<ResourceLocation, List<IResource>> listResourceStacks(ResourceLocation location);
}
