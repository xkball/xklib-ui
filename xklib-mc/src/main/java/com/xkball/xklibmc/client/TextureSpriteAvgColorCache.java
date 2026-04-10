package com.xkball.xklibmc.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TextureSpriteAvgColorCache {
    
    public final Map<Identifier, Integer> colorCache = new HashMap<>();
    
    public int getAvgColor(TextureAtlasSprite sprite) {
        var key = sprite.contents().name();
        var cached = colorCache.get(key);
        if (cached != null) {
            return cached;
        }
        var content = sprite.contents().getOriginalImage();
        long rSum = 0;
        long gSum = 0;
        long bSum = 0;
        long aSum = 0;
        long count = 0;
        for (int abgr : content.getPixelsABGR()) {
            int a = (abgr >>> 24) & 255;
            if (a == 0) {
                continue;
            }
            int b = (abgr >>> 16) & 255;
            int g = (abgr >>> 8) & 255;
            int r = abgr & 255;
            aSum += a;
            rSum += r;
            gSum += g;
            bSum += b;
            count++;
        }
        int argb;
        if (count == 0) {
            argb = 0;
        } else {
            int a = (int) (aSum / count);
            int r = (int) (rSum / count);
            int g = (int) (gSum / count);
            int b = (int) (bSum / count);
            argb = (a << 24) | (r << 16) | (g << 8) | b;
        }
        colorCache.put(key, argb);
        return argb;
    }
}
