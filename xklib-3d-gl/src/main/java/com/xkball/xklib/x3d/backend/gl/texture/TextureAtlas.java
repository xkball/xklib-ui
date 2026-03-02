package com.xkball.xklib.x3d.backend.gl.texture;

import com.xkball.xklib.x3d.api.resource.IResource;
import com.xkball.xklib.resource.ResourceLocation;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
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
        int atlasSize = calculateAtlasSize(resourceMap.size());
        
        id = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(id, 1, GL_RGBA8, atlasSize, atlasSize);
        
        this.width = atlasSize;
        this.height = atlasSize;
        
        int x = 0, y = 0;
        int maxHeightInRow = 0;
        
        for (Map.Entry<ResourceLocation, List<IResource>> entry : resourceMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            IResource resource = entry.getValue().getFirst();
            
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthBuf = stack.mallocInt(1);
                IntBuffer heightBuf = stack.mallocInt(1);
                IntBuffer channelsBuf = stack.mallocInt(1);
                
                byte[] data = resource.open().readAllBytes();
                ByteBuffer imageBuffer = stack.malloc(data.length);
                imageBuffer.put(data);
                imageBuffer.flip();
                
                STBImage.stbi_set_flip_vertically_on_load(false);
                ByteBuffer pixels = STBImage.stbi_load_from_memory(
                    imageBuffer, widthBuf, heightBuf, channelsBuf, 4
                );
                
                if (pixels == null) {
                    continue;
                }
                
                int w = widthBuf.get(0);
                int h = heightBuf.get(0);
                
                if (x + w > atlasSize) {
                    x = 0;
                    y += maxHeightInRow;
                    maxHeightInRow = 0;
                }
                
                if (y + h <= atlasSize) {
                    glTextureSubImage2D(id, 0, x, y, w, h, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
                    
                    float u0 = (float)x / atlasSize;
                    float v0 = (float)y / atlasSize;
                    float u1 = (float)(x + w) / atlasSize;
                    float v1 = (float)(y + h) / atlasSize;
                    
                    sprites.put(location, new TextureAtlasSprite(location, x, y, u0, v0, u1, v1));
                    
                    x += w;
                    maxHeightInRow = Math.max(maxHeightInRow, h);
                }
                
                STBImage.stbi_image_free(pixels);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load texture atlas", e);
            }
        }
    }
    
    private int calculateAtlasSize(int count) {
        int size = 256;
        while (size * size / (64 * 64) < count && size < 4096) {
            size *= 2;
        }
        return size;
    }
    
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return sprites.get(location);
    }
}
