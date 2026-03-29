package com.xkball.xklib.x3d.backend.gl.texture;

import com.xkball.xklib.x3d.api.resource.IResource;
import com.xkball.xklib.resource.ResourceLocation;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL45.*;

public class TextureAtlas extends AbstractTexture{
    
    private final Map<ResourceLocation, TextureAtlasSprite> sprites = new HashMap<>();
    
    public TextureAtlas(Map<ResourceLocation, List<IResource>> resourceMap) {
        load(resourceMap);
        setMinFilter(GL_NEAREST);
        setMagFilter(GL_NEAREST);
        setWrapS(GL_CLAMP_TO_EDGE);
        setWrapT(GL_CLAMP_TO_EDGE);
    }
    
    private void load(Map<ResourceLocation, List<IResource>> resourceMap) {
        Map<ResourceLocation, ImageSource> imageSources = readImageSources(resourceMap);
        int atlasSize = calculateAtlasSize(imageSources);
        
        id = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(id, 1, GL_RGBA8, atlasSize, atlasSize);
        this.width = atlasSize;
        this.height = atlasSize;
        
        int x = 0, y = 0;
        int maxHeightInRow = 0;
        
        for (Map.Entry<ResourceLocation, ImageSource> entry : imageSources.entrySet()) {
            ResourceLocation location = entry.getKey();
            ImageSource imageSource = entry.getValue();
            
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthBuf = stack.mallocInt(1);
                IntBuffer heightBuf = stack.mallocInt(1);
                IntBuffer channelsBuf = stack.mallocInt(1);
                
                ByteBuffer imageBuffer = stack.malloc(imageSource.data.length);
                imageBuffer.put(imageSource.data);
                imageBuffer.flip();
                
                STBImage.stbi_set_flip_vertically_on_load(false);
                ByteBuffer pixels = STBImage.stbi_load_from_memory(
                    imageBuffer, widthBuf, heightBuf, channelsBuf, 4
                );
                
                if (pixels == null) {
                    throw new IllegalStateException("Failed to decode atlas sprite " + location + ": " + STBImage.stbi_failure_reason());
                }
                
                int w = widthBuf.get(0);
                int h = heightBuf.get(0);
                
                if (x + w > atlasSize) {
                    x = 0;
                    y += maxHeightInRow;
                    maxHeightInRow = 0;
                }
                
                if (y + h > atlasSize) {
                    STBImage.stbi_image_free(pixels);
                    throw new IllegalStateException("Atlas overflow when placing sprite " + location + " (" + w + "x" + h + "), atlas=" + atlasSize);
                }
                
                glTextureSubImage2D(id, 0, x, y, w, h, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
                
                float u0 = (float)x / atlasSize;
                float v0 = (float)y / atlasSize;
                float u1 = (float)(x + w) / atlasSize;
                float v1 = (float)(y + h) / atlasSize;
                
                sprites.put(location, new TextureAtlasSprite(location, this,x, y, u0, v0, u1, v1));
                
                x += w;
                maxHeightInRow = Math.max(maxHeightInRow, h);
                
                STBImage.stbi_image_free(pixels);
            }
        }
    }
    
    private Map<ResourceLocation, ImageSource> readImageSources(Map<ResourceLocation, List<IResource>> resourceMap) {
        Map<ResourceLocation, ImageSource> result = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, List<IResource>> entry : resourceMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            IResource resource = entry.getValue().getFirst();
            try (InputStream stream = resource.open(); MemoryStack stack = MemoryStack.stackPush()) {
                byte[] data = stream.readAllBytes();
                ByteBuffer imageBuffer = stack.malloc(data.length);
                imageBuffer.put(data);
                imageBuffer.flip();
                IntBuffer widthBuf = stack.mallocInt(1);
                IntBuffer heightBuf = stack.mallocInt(1);
                IntBuffer channelsBuf = stack.mallocInt(1);
                if (!STBImage.stbi_info_from_memory(imageBuffer, widthBuf, heightBuf, channelsBuf)) {
                    throw new IllegalStateException("Failed to read image info for " + location + ": " + STBImage.stbi_failure_reason());
                }
                result.put(location, new ImageSource(data, widthBuf.get(0), heightBuf.get(0)));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load texture atlas", e);
            }
        }
        return result;
    }

    private int calculateAtlasSize(Map<ResourceLocation, ImageSource> images) {
        int maxSide = 1;
        long totalArea = 0;
        for (ImageSource image : images.values()) {
            maxSide = Math.max(maxSide, Math.max(image.width, image.height));
            totalArea += (long) image.width * image.height;
        }
        int size = 1;
        while (size < maxSide) {
            size <<= 1;
        }
        if (size < 256) {
            size = 256;
        }
        while ((long) size * size < totalArea && size < 4096) {
            size *= 2;
        }
        if ((long) size * size < totalArea) {
            throw new IllegalStateException("Atlas too large, total area=" + totalArea + ", max size=4096x4096");
        }
        return size;
    }
    
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return sprites.get(location);
    }

    private record ImageSource(byte[] data, int width, int height) {
    }
}
