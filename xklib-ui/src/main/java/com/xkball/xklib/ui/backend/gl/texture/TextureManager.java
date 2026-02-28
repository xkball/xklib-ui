package com.xkball.xklib.ui.backend.gl.texture;

import com.xkball.xklib.api.resource.IResource;
import com.xkball.xklib.api.resource.IResourceManager;
import com.xkball.xklib.resource.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureManager {
    
    private final Logger LOGGER = LoggerFactory.getLogger(TextureManager.class);
    private final Map<ResourceLocation, AbstractTexture> byPath = new HashMap<>();
    private final Map<ResourceLocation, TextureAtlas> atlasMap = new HashMap<>();
    private final IResourceManager resourceManager;
    
    public TextureManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    
    @Nullable
    public TextureAtlasSprite getSprite(ResourceLocation location){
        var texture = atlasMap.get(location);
        return texture.getSprite(location);
    }
    
    public AbstractTexture getTexture(ResourceLocation path) {
        return byPath.computeIfAbsent(path, this::load);
    }
    
    private AbstractTexture load(ResourceLocation path){
        LOGGER.info("Creating texture: {}", path);
        Map<ResourceLocation, List<IResource>> resourceStacks = resourceManager.listResourceStacks(path);
        
        if (resourceStacks.isEmpty()) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        
        if (resourceStacks.size() == 1) {
            IResource resource = resourceStacks.values().iterator().next().getFirst();
            return new SimpleTexture(resource);
        } else {
            var result = new TextureAtlas(resourceStacks);
            for(var entry : resourceStacks.entrySet()){
                atlasMap.put(entry.getKey(), result);
            }
            return result;
        }
    }
    
    public void destroy() {
        byPath.values().forEach(AbstractTexture::destroy);
        byPath.clear();
    }
}
