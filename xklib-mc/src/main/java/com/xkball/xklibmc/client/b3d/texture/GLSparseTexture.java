package com.xkball.xklibmc.client.b3d.texture;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.TextureFormat;
import org.lwjgl.opengl.ARBSparseTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class GLSparseTexture extends GlTexture {
    
    private final int pageSizeX;
    private final int pageSizeY;
    
    private final int pagesX;
    private final int pagesY;
    private final int clearColor;
    private final boolean[][] committed;
    
    
    public GLSparseTexture(@Usage int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels, int id, int pageSizeX, int pageSizeY, int clearColor) {
        super(usage, label, format, width, height, depthOrLayers, mipLevels, id);
        this.pageSizeX = pageSizeX;
        this.pageSizeY = pageSizeY;
        this.clearColor = clearColor;
        this.pagesX = (int)Math.ceil((double)width / pageSizeX);
        this.pagesY = (int)Math.ceil((double)height / pageSizeY);
        this.committed = new boolean[pagesX][pagesY];
    }
    
    private void commitPage(int px, int py) {
        if (committed[px][py]) return;
        int x = px * pageSizeX;
        int y = py * pageSizeY;
        ARBSparseTexture.glTexPageCommitmentARB(GL11.GL_TEXTURE_2D, 0, x, y, 0, pageSizeX, pageSizeY, 1, true);
        var buffer = MemoryUtil.memAlloc(pagesX * pagesY * 4);
        for (int i = 0; i < pageSizeX * pageSizeY; i++) {
            buffer.putInt(clearColor);
        }
        buffer.flip();
        var format = NativeImage.Format.RGBA;
        var rowLengthOld = GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH);
        var skipRowsOld = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS);
        var skipPixelsOld = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS);
        var alignmentOld = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        GlStateManager._pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_ALIGNMENT, format.components());
        GL11.glTexSubImage2D(
                GL11.GL_TEXTURE_2D, 0, x, y, pagesX, pagesY,
                GlConst.toGl(format),
                GL11.GL_UNSIGNED_BYTE, buffer
        );
        MemoryUtil.memFree(buffer);
        committed[px][py] = true;
        GlStateManager._pixelStore(GL11.GL_UNPACK_ROW_LENGTH, rowLengthOld);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_ROWS, skipRowsOld);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, skipPixelsOld);
        GlStateManager._pixelStore(GL11.GL_UNPACK_ALIGNMENT, alignmentOld);
    }
    
    public void upload(int x, int y, int w, int h, NativeImage.Format format, ByteBuffer data) {
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
        
        GlStateManager._pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_ALIGNMENT, format.components());
        
        GL11.glTexSubImage2D(
                GL11.GL_TEXTURE_2D, 0, x, y, w, h,
                GlConst.toGl(format),
                GL11.GL_UNSIGNED_BYTE, data
        );
    }
    
}
