package com.xkball.xklibmc.client.b3d.texture;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import org.lwjgl.opengl.ARBSparseTexture;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class GLSparseTexture extends GpuTexture {
    
    public final int id;
    private int views;
    protected boolean closed;
    
    private final int pageSizeX;
    private final int pageSizeY;
    
    private final int pagesX;
    private final int pagesY;
    
    private final boolean[][] committed;
    
    
    public GLSparseTexture(@Usage int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels, int id, int pageSizeX, int pageSizeY) {
        super(usage, label, format, width, height, depthOrLayers, mipLevels);
        this.id = id;
        this.pageSizeX = pageSizeX;
        this.pageSizeY = pageSizeY;
        this.pagesX = (int)Math.ceil((double)width / pageSizeX);
        this.pagesY = (int)Math.ceil((double)height / pageSizeY);
        this.committed = new boolean[pagesX][pagesY];
    }
    
    private void commitPage(int px, int py) {
        if (committed[px][py]) return;
        int x = px * pageSizeX;
        int y = py * pageSizeY;
        ARBSparseTexture.glTexPageCommitmentARB(GL11.GL_TEXTURE_2D, 0, x, y, 0, pageSizeX, pageSizeY, 1, true);
        committed[px][py] = true;
    }
    
    public void upload(int x, int y, int w, int h, int component, ByteBuffer data) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        
        int startPageX = x / pageSizeX;
        int endPageX = (x + w - 1) / pageSizeX;
        
        int startPageY = y / pageSizeY;
        int endPageY = (y + h - 1) / pageSizeY;
        
        for (int px = startPageX; px <= endPageX; px++) {
            for (int py = startPageY; py <= endPageY; py++) {
                commitPage(px, py);
            }
        }
        
        GlStateManager._pixelStore(GL11.GL_UNPACK_ROW_LENGTH, width);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_ALIGNMENT, component);
        
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        
        GL11.glTexSubImage2D(
                GL11.GL_TEXTURE_2D, 0, x, y, w, h,
                GlConst.toGlInternalId(this.getFormat()),
                GL11.GL_UNSIGNED_BYTE, data
        );
    }
    
    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            if (this.views == 0) {
                this.destroyImmediately();
            }
        }
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    public int glId() {
        return this.id;
    }
    
    public void addViews() {
        this.views++;
    }
    
    public void removeViews() {
        this.views--;
        if (this.closed && this.views == 0) {
            this.destroyImmediately();
        }
    }
    
    private void destroyImmediately() {
        GlStateManager._deleteTexture(this.id);
    }
}
