package com.xkball.xklib.api.gui.render;

import com.xkball.xklib.api.render.IFont;
import com.xkball.xklib.api.render.IRenderPipelineSource;
import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.api.render.ITexture;
import com.xkball.xklib.api.render.ITextureAtlasSprite;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.gl.pipeline.RenderPipeline;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;

public interface IGUIGraphics {
    
    IFont defaultFont();
    
    ITextureAtlasSprite getSprite(ResourceLocation location);
    
    ITexture getTexture(ResourceLocation location);
    
    void enableScissor(int x0, int y0, int x1, int y1);
    
    void disableScissor();
    
    void submitColoredRectangle(IRenderPipeline pipeline,
                                int minX, int minY,
                                int maxX, int maxY,
                                int colorFrom, int colorTo);
    
    void submitColoredRoundedRectangle(
                                int minX, int minY,
                                int maxX, int maxY,
                                int colorFrom, int colorTo, int radius);
    
    void drawString(IFont font, IComponent text, int x, int y, int color, boolean drawShadow);
    
    void submitBlit(
            IRenderPipeline pipeline,
            ITexture textureView,
            int x0, int y0, int x1, int y1,
            float u0, float u1, float v0, float v1,
            int color
    );
    
    void draw();
    
    Matrix3x2fStack getPose();
    
    void layerUp();
    
    void layerDown();
    
    default void renderOutline(int x, int y, int width, int height, int color) {
        this.fill(x, y, x + width, y + 1, color);
        this.fill(x, y + height - 1, x + width, y + height, color);
        this.fill(x, y + 1, x + 1, y + height - 1, color);
        this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }
    
    default void fillRounded(int minX, int minY, int maxX, int maxY, int color, int radius){
        this.submitColoredRoundedRectangle(minX, minY, maxX, maxY, color, color, radius);
    }
    
    default void fillGradientRounded(int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo, int radius){
        this.submitColoredRoundedRectangle(minX, minY, maxX, maxY, colorFrom, colorTo, radius);
    }
    
    default void fill(int minX, int minY, int maxX, int maxY, int color) {
        this.fill(IRenderPipelineSource.getInstance().getGui(), minX, minY, maxX, maxY, color);
    }
    
    default void fill(IRenderPipeline pipeline, int minX, int minY, int maxX, int maxY, int color) {
        if (minX < maxX) {
            int i = minX;
            minX = maxX;
            maxX = i;
        }
        
        if (minY < maxY) {
            int j = minY;
            minY = maxY;
            maxY = j;
        }
        
        this.submitColoredRectangle(pipeline, minX, minY, maxX, maxY, color, color);
    }
    
    default void fill(IRenderPipeline pipeline, int minX, int minY, int maxX, int maxY) {
        this.submitColoredRectangle(pipeline, minX, minY, maxX, maxY, -1, -1);
    }
    
    default void fillGradient(int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
        this.submitColoredRectangle(IRenderPipelineSource.getInstance().getGui(), minX, minY, maxX, maxY, colorFrom, colorTo);
    }
    
    default void hLine(int minX, int maxX, int y, int color) {
        if (maxX < minX) {
            int i = minX;
            minX = maxX;
            maxX = i;
        }
        
        this.fill(minX, y, maxX + 1, y + 1, color);
    }
    

    default void vLine(int x, int minY, int maxY, int color) {
        if (maxY < minY) {
            int i = minY;
            minY = maxY;
            maxY = i;
        }
        
        this.fill(x, minY + 1, x + 1, maxY, color);
    }
    
    default void drawCenteredString(IFont font, String text, int x, int y, int color) {
        this.drawString(font, text, x - font.width(text) / 2, y, color);
    }
    
    default void drawCenteredString(String text, int x, int y, int color) {
        var font = this.defaultFont();
        this.drawString(font, text, x - font.width(text) / 2, y, color);
    }
    
    default void drawCenteredString(String text, int x, int y, int color, int height) {
        var font = this.defaultFont();
        var scale = height / (float)font.lineHeight();
        this.drawString(font, text, (int) (x - font.width(text) * scale / 2), y, color, height);
    }
    
    default void drawString(IFont font, @Nullable String text, int x, int y, int color, int height){
        this.getPose().pushMatrix();
        var scale = height / (float)font.lineHeight();
        this.getPose().scale(scale,scale);
        this.drawString(font, text, (int) (x/scale), (int) (y/scale), color);
        this.getPose().popMatrix();
    }
    
    default void drawString(@Nullable String text, int x, int y, int color, int height){
        this.drawString(this.defaultFont(), text, x, y, color, height);
    }
    
    default void drawString(@Nullable String text, int x, int y, int color) {
        this.drawString(this.defaultFont(), text, x, y, color, false);
    }
    
    default void drawString(IFont font, @Nullable String text, int x, int y, int color) {
        this.drawString(font, text, x, y, color, false);
    }
    
    default void drawString(IFont font, @Nullable String text, int x, int y, int color, boolean drawShadow) {
        if (text != null) {
            this.drawString(font, IComponent.literal(text), x, y, color, drawShadow);
        }
    }
    
    default void drawString(IFont font, IComponent text, int x, int y, int color) {
        this.drawString(font, text, x, y, color, false);
    }
    
    default void blitSprite(IRenderPipeline renderPipeline, ResourceLocation location, int x, int y, int width, int height, int color) {
        ITextureAtlasSprite sprite = this.getSprite(location);
        this.blitSprite(renderPipeline, sprite, x, y, width, height, color);
    }
    
    default void blitSprite(
            IRenderPipeline renderPipeline, ResourceLocation location, int spriteWidth, int spriteHeight, int textureX, int textureY, int x, int y, int width, int height
    ) {
        this.blitSprite(renderPipeline, location, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, -1);
    }
    
    default void blitSprite(
            IRenderPipeline renderPipeline,
            ResourceLocation location,
            int spriteWidth,
            int spriteHeight,
            int textureX,
            int textureY,
            int x,
            int y,
            int width,
            int height,
            int color
    ) {
        ITextureAtlasSprite sprite = this.getSprite(location);
        this.enableScissor(x, y, x + width, y + height);
        this.blitSprite(renderPipeline, location, x - textureX, y - textureY, spriteWidth, spriteHeight, color);
        this.disableScissor();
    }
    
    default void blitSprite(IRenderPipeline renderPipeline, ITextureAtlasSprite sprite, int x, int y, int width, int height) {
        this.blitSprite(renderPipeline, sprite, x, y, width, height, -1);
    }
    
    default void blitSprite(IRenderPipeline renderPipeline, ITextureAtlasSprite sprite, int x, int y, int width, int height, int color) {
        if (width != 0 && height != 0) {
            this.innerBlit(
                    renderPipeline, sprite.atlasLocation(), x, x + width, y, y + height, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), color
            );
        }
    }
    
    private void blitSprite(
            RenderPipeline renderPipeline,
            ITextureAtlasSprite sprite,
            int spriteWidth,
            int spriteHeight,
            int textureX,
            int textureY,
            int x,
            int y,
            int width,
            int height,
            int color
    ) {
        if (width != 0 && height != 0) {
            this.innerBlit(
                    renderPipeline,
                    sprite.atlasLocation(),
                    x,
                    x + width,
                    y,
                    y + height,
                    sprite.getU((float)textureX / spriteWidth),
                    sprite.getU((float)(textureX + width) / spriteWidth),
                    sprite.getV((float)textureY / spriteHeight),
                    sprite.getV((float)(textureY + height) / spriteHeight),
                    color
            );
        }
    }
    
    default void blit(
            IRenderPipeline renderPipeline,
            ResourceLocation texture,
            int x,
            int y,
            float u,
            float v,
            int width,
            int height,
            int textureWidth,
            int textureHeight,
            int color
    ) {
        this.blit(renderPipeline, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight, color);
    }
    
    default void blit(
            IRenderPipeline renderPipeline, ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight
    ) {
        this.blit(renderPipeline, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
    }
    
    default void blit(
            IRenderPipeline renderPipeline,
            ResourceLocation texture,
            int x,
            int y,
            float u,
            float v,
            int width,
            int height,
            int srcWidth,
            int srcHeight,
            int textureWidth,
            int textureHeight
    ) {
        this.blit(renderPipeline, texture, x, y, u, v, width, height, srcWidth, srcHeight, textureWidth, textureHeight, -1);
    }
    
    default void blit(
            IRenderPipeline renderPipeline,
            ResourceLocation texture,
            int x,
            int y,
            float u,
            float v,
            int width,
            int height,
            int srcWidth,
            int srcHeight,
            int textureWidth,
            int textureHeight,
            int color
    ) {
        this.innerBlit(
                renderPipeline,
                texture,
                x,
                x + width,
                y,
                y + height,
                (u + 0.0F) / textureWidth,
                (u + srcWidth) / textureWidth,
                (v + 0.0F) / textureHeight,
                (v + srcHeight) / textureHeight,
                color
        );
    }
    
    default void blit(ResourceLocation location, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1) {
        this.innerBlit(IRenderPipelineSource.getInstance().getGuiTextured(), location, x0, x1, y0, y1, u0, u1, v0, v1, -1);
    }
    
    default void innerBlit(
            IRenderPipeline renderPipeline, ResourceLocation location, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color
    ) {
        ITexture texture = this.getTexture(location);
        this.submitBlit(renderPipeline, texture, x0, y0, x1, y1, u0, u1, v0, v1, color);
    }
    

    
}
