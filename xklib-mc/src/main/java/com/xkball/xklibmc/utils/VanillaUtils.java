package com.xkball.xklibmc.utils;

import com.xkball.xklibmc.XKLibMC;
import com.xkball.xklib.resource.ResourceLocation;
import net.minecraft.resources.Identifier;

public class VanillaUtils {
    
    public static Identifier convertRL(ResourceLocation rl){
        return Identifier.fromNamespaceAndPath(rl.namespace(),rl.path());
    }
    
    public static ResourceLocation convertId(Identifier id){
        return new ResourceLocation(id.getNamespace(),id.getPath());
    }
    
    public static Identifier modRL(String path) {
        return resourceLocationOf(XKLibMC.MODID, path);
    }
    
    public static Identifier resourceLocationOf(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
}
