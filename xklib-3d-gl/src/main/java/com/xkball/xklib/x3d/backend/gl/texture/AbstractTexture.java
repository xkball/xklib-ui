package com.xkball.xklib.x3d.backend.gl.texture;

import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.backend.gl.GLStateManager;

import static org.lwjgl.opengl.GL45.*;

public abstract class AbstractTexture implements ITexture {
    
    protected int id;
    protected int width;
    protected int height;
    protected boolean destroyed;
    
    @Override
    public int getId(){
        return id;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void bind() {
        if (destroyed) {
            throw new IllegalStateException("Texture already destroyed");
        }
        GLStateManager.bindTexture(GL_TEXTURE_2D, id);
    }
    
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    public void setMinFilter(int filter) {
        bind();
        glTextureParameteri(id, GL_TEXTURE_MIN_FILTER, filter);
    }
    
    public void setMagFilter(int filter) {
        bind();
        glTextureParameteri(id, GL_TEXTURE_MAG_FILTER, filter);
    }
    
    public void setWrapS(int wrap) {
        bind();
        glTextureParameteri(id, GL_TEXTURE_WRAP_S, wrap);
    }
    
    public void setWrapT(int wrap) {
        bind();
        glTextureParameteri(id, GL_TEXTURE_WRAP_T, wrap);
    }
    
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        if (id != 0) {
            glDeleteTextures(id);
            id = 0;
        }
    }
    
    protected void setId(int id) {
        this.id = id;
    }
}
