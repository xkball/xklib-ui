package com.xkball.xklib.ui.backend.gl.texture;

import com.xkball.xklib.api.resource.IResource;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL45.*;

public class SimpleTexture extends AbstractTexture{
    
    public SimpleTexture(IResource resource) {
        load(resource);
        setMinFilter(GL_NEAREST);
        setMagFilter(GL_NEAREST);
        setWrapS(GL_CLAMP_TO_EDGE);
        setWrapT(GL_CLAMP_TO_EDGE);
    }
    
    private void load(IResource resource) {
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
                throw new IllegalStateException("Failed to load texture: " + STBImage.stbi_failure_reason());
            }
            
            this.width = widthBuf.get(0);
            this.height = heightBuf.get(0);
            
            id = glCreateTextures(GL_TEXTURE_2D);
            glTextureStorage2D(id, 1, GL_RGBA8, width, height);
            glTextureSubImage2D(id, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            
            STBImage.stbi_image_free(pixels);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture", e);
        }
    }
}
