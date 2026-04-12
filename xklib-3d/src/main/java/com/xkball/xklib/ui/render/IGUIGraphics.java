package com.xkball.xklib.ui.render;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.api.render.ITextureAtlasSprite;
import com.xkball.xklib.resource.ResourceLocation;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;

public interface IGUIGraphics {
    
    IFont defaultFont();
    
    ITextureAtlasSprite getSprite(ResourceLocation location);
    
    ITexture getTexture(ResourceLocation location);
    
    void enableScissor(float x0, float y0, float x1, float y1);
    
    void disableScissor();
    
    void submitColoredRectangle(IRenderPipeline pipeline,
                                float minX, float minY,
                                float maxX, float maxY,
                                int colorFrom, int colorTo);
    
    void submitColoredRoundedRectangle(
                                float minX, float minY,
                                float maxX, float maxY,
                                int colorFrom, int colorTo, float radius);
    
    void renderLine(
            float x0, float y0,
            float x1, float y1,
            int colorFrom, int colorTo);
    
    void drawString(IFont font, IComponent text, float x, float y, int color, boolean drawShadow);
    
    void submitBlit(
            IRenderPipeline pipeline,
            ITexture textureView,
            float x0, float y0, float x1, float y1,
            float u0, float u1, float v0, float v1,
            int color
    );
    
    void draw();
    
    Matrix3x2fStack getPose();
    
    void layerUp();
    
    void layerDown();
    
    default void renderLine(float x0, float y0, float x1, float y1, int color){
        this.renderLine(x0, y0, x1, y1, color, color);
    }
    
    default void renderOutline(float x, float y, float width, float height, int color) {
        this.fill(x, y, x + width, y + 1, color);
        this.fill(x, y + height - 1, x + width, y + height, color);
        this.fill(x, y + 1, x + 1, y + height - 1, color);
        this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }
    
    default void fillRounded(float minX, float minY, float maxX, float maxY, int color, float radius){
        this.submitColoredRoundedRectangle(minX, minY, maxX, maxY, color, color, radius);
    }
    
    default void fillGradientRounded(float minX, float minY, float maxX, float maxY, int colorFrom, int colorTo, float radius){
        this.submitColoredRoundedRectangle(minX, minY, maxX, maxY, colorFrom, colorTo, radius);
    }
    
    default void fill(float minX, float minY, float maxX, float maxY, int color) {
        this.fill(XKLib.RENDER_CONTEXT.get().getPipelineSource().getGui(), minX, minY, maxX, maxY, color);
    }
    
    default void fill(IRenderPipeline pipeline, float minX, float minY, float maxX, float maxY, int color) {
        if (minX < maxX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }
        
        if (minY < maxY) {
            float j = minY;
            minY = maxY;
            maxY = j;
        }
        
        this.submitColoredRectangle(pipeline, minX, minY, maxX, maxY, color, color);
    }
    
    default void fill(IRenderPipeline pipeline, float minX, float minY, float maxX, float maxY) {
        this.submitColoredRectangle(pipeline, minX, minY, maxX, maxY, -1, -1);
    }
    
    default void fillGradient(float minX, float minY, float maxX, float maxY, int colorFrom, int colorTo) {
        this.submitColoredRectangle(XKLib.RENDER_CONTEXT.get().getPipelineSource().getGui(), minX, minY, maxX, maxY, colorFrom, colorTo);
    }
    
    default void hLine(float minX, float maxX, float y, int color) {
        if (maxX < minX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }
        
        this.fill(minX, y, maxX + 1, y + 1, color);
    }
    

    default void vLine(float x, float minY, float maxY, int color) {
        if (maxY < minY) {
            float i = minY;
            minY = maxY;
            maxY = i;
        }
        
        this.fill(x, minY + 1, x + 1, maxY, color);
    }
    
    default void drawCenteredString(IFont font, String text, float x, float y, int color) {
        this.drawString(font, text, x - font.width(text) / 2, y, color);
    }
    
    default void drawCenteredString(String text, float x, float y, int color) {
        var font = this.defaultFont();
        this.drawString(font, text, x - font.width(text) / 2, y, color);
    }
    
    default void drawCenteredString(String text, float x, float y, int color, float height) {
        var font = this.defaultFont();
        var scale = height / (float)font.lineHeight();
        this.drawString(font, text, x - font.width(text) * scale / 2, y, color, height);
    }
    
    default void drawCenteredString(IComponent text, float x, float y, int color, float height) {
        var font = this.defaultFont();
        var scale = height / (float)font.lineHeight();
        this.drawString(font, text, x - font.width(text) * scale / 2, y, color, height);
    }
    
    default void drawCenteredString(IComponent text, float x, float y, int color, boolean dropShadow, float height) {
        var font = this.defaultFont();
        var scale = height / (float)font.lineHeight();
        this.drawString(font, text, x - font.width(text) * scale / 2, y, color, dropShadow, height);
    }
    
    default void drawString(IFont font, @Nullable IComponent text, float x, float y, int color, float height){
        this.getPose().pushMatrix();
        var scale = height / (float)font.lineHeight();
        this.getPose().scale(scale,scale);
        this.drawString(font, text, x/scale, y/scale, color);
        this.getPose().popMatrix();
    }
    
    default void drawString(IFont font, @Nullable IComponent text, float x, float y, int color, boolean dropShadow, float height){
        this.getPose().pushMatrix();
        var scale = height / (float)font.lineHeight();
        this.getPose().scale(scale,scale);
        this.drawString(font, text, x/scale, y/scale, color, dropShadow);
        this.getPose().popMatrix();
    }
    
    default void drawString(IFont font, @Nullable String text, float x, float y, int color, float height){
        this.getPose().pushMatrix();
        var scale = height / (float)font.lineHeight();
        this.getPose().scale(scale,scale);
        this.drawString(font, text, x/scale, y/scale, color);
        this.getPose().popMatrix();
    }
    
    default void drawString(IFont font, @Nullable String text, float x, float y, int color, boolean dropShadow, float height){
        this.getPose().pushMatrix();
        var scale = height / (float)font.lineHeight();
        this.getPose().scale(scale,scale);
        this.drawString(font, text, x/scale, y/scale, color,dropShadow);
        this.getPose().popMatrix();
    }
    
    default void drawString(@Nullable String text, float x, float y, int color, float height){
        this.drawString(this.defaultFont(), text, x, y, color, height);
    }
    
    default void drawString(@Nullable String text, float x, float y, int color) {
        this.drawString(this.defaultFont(), text, x, y, color, false);
    }
    
    default void drawString(@Nullable IComponent text, float x, float y, int color, float height){
        this.drawString(this.defaultFont(), text, x, y, color, height);
    }
    
    default void drawString(@Nullable IComponent text, float x, float y, int color, boolean dropShadow, float height){
        this.drawString(this.defaultFont(), text, x, y, color,dropShadow, height);
    }
    
    default void drawString(@Nullable IComponent text, float x, float y, int color) {
        this.drawString(this.defaultFont(), text, x, y, color, false);
    }
    
    default void drawString(IFont font, @Nullable String text, float x, float y, int color) {
        this.drawString(font, text, x, y, color, false);
    }
    
    default void drawString(IFont font, @Nullable String text, float x, float y, int color, boolean drawShadow) {
        if (text != null) {
            this.drawString(font, IComponent.literal(text), x, y, color, drawShadow);
        }
    }
    
    default void drawString(IFont font, IComponent text, float x, float y, int color) {
        this.drawString(font, text, x, y, color, false);
    }
    
    default void blitSprite(ResourceLocation location, float x, float y, float width, float height, int color) {
        ITextureAtlasSprite sprite = this.getSprite(location);
        this.blitSprite(XKLib.RENDER_CONTEXT.get().getPipelineSource().getGuiTextured(), sprite, x, y, width, height, color);
    }
    
    default void blitSprite(IRenderPipeline renderPipeline, ResourceLocation location, float x, float y, float width, float height, int color) {
        ITextureAtlasSprite sprite = this.getSprite(location);
        this.blitSprite(renderPipeline, sprite, x, y, width, height, color);
    }
    
    default void blitSprite(
            IRenderPipeline renderPipeline, ResourceLocation location, float spriteWidth, float spriteHeight, float textureX, float textureY, float x, float y, float width, float height
    ) {
        this.blitSprite(renderPipeline, location, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, -1);
    }
    
    default void blitSprite(
            IRenderPipeline renderPipeline,
            ResourceLocation location,
            float spriteWidth,
            float spriteHeight,
            float textureX,
            float textureY,
            float x,
            float y,
            float width,
            float height,
            int color
    ) {
        ITextureAtlasSprite sprite = this.getSprite(location);
        this.enableScissor(x, y, x + width, y + height);
        this.blitSprite(renderPipeline, location, x - textureX, y - textureY, spriteWidth, spriteHeight, color);
        this.disableScissor();
    }
    
    default void blitSprite(IRenderPipeline renderPipeline, ITextureAtlasSprite sprite, float x, float y, float width, float height) {
        this.blitSprite(renderPipeline, sprite, x, y, width, height, -1);
    }
    
    default void blitSprite(IRenderPipeline renderPipeline, ITextureAtlasSprite sprite, float x, float y, float width, float height, int color) {
        if (width != 0 && height != 0) {
            this.innerBlit(
                    renderPipeline, sprite.atlasLocation(), x, x + width, y, y + height, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), color
            );
        }
    }
    
    default void blitSprite(
            IRenderPipeline renderPipeline,
            ITextureAtlasSprite sprite,
            float spriteWidth,
            float spriteHeight,
            float textureX,
            float textureY,
            float x,
            float y,
            float width,
            float height,
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
                    sprite.getU(textureX / spriteWidth),
                    sprite.getU((textureX + width) / spriteWidth),
                    sprite.getV(textureY / spriteHeight),
                    sprite.getV((textureY + height) / spriteHeight),
                    color
            );
        }
    }
    
    default void blit(
            IRenderPipeline renderPipeline,
            ResourceLocation texture,
            float x,
            float y,
            float u,
            float v,
            float width,
            float height,
            float textureWidth,
            float textureHeight,
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
            float x,
            float y,
            float u,
            float v,
            float width,
            float height,
            float srcWidth,
            float srcHeight,
            float textureWidth,
            float textureHeight
    ) {
        this.blit(renderPipeline, texture, x, y, u, v, width, height, srcWidth, srcHeight, textureWidth, textureHeight, -1);
    }
    
    default void blit(
            IRenderPipeline renderPipeline,
            ResourceLocation texture,
            float x,
            float y,
            float u,
            float v,
            float width,
            float height,
            float srcWidth,
            float srcHeight,
            float textureWidth,
            float textureHeight,
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
    
    default void blit(ResourceLocation location, float x0, float y0, float x1, float y1, float u0, float u1, float v0, float v1) {
        this.innerBlit(XKLib.RENDER_CONTEXT.get().getPipelineSource().getGuiTextured(), location, x0, x1, y0, y1, u0, u1, v0, v1, -1);
    }
    
    default void innerBlit(
            IRenderPipeline renderPipeline, ResourceLocation location, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1, int color
    ) {
        ITexture texture = this.getTexture(location);
        this.submitBlit(renderPipeline, texture, x0, y0, x1, y1, u0, u1, v0, v1, color);
    }
    

    
}
