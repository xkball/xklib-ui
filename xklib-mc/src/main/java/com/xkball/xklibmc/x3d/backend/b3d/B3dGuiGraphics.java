package com.xkball.xklibmc.x3d.backend.b3d;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.logging.LogUtils;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IFont;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.api.render.ITextureAtlasSprite;
import com.xkball.xklib.x3d.api.resource.ITextureManager;
import com.xkball.xklibmc.x3d.backend.b3d.gui.ComponentConverter;
import com.xkball.xklibmc.x3d.backend.b3d.gui.element.LineRenderState;
import com.xkball.xklibmc.x3d.backend.b3d.gui.element.RoundedRectangleRenderState;
import com.xkball.xklibmc.x3d.backend.b3d.gui.element.XKLibBlitRenderState;
import com.xkball.xklibmc.x3d.backend.b3d.gui.element.XKLibColoredRectangleRenderState;
import com.xkball.xklibmc.x3d.backend.b3d.gui.element.XKLibTiledBliRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.slf4j.Logger;

public class B3dGuiGraphics implements IGUIGraphics {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    private GuiGraphicsExtractor inner;
    private final ITextureManager textureManager;
    public float scale = 1;
    
    public B3dGuiGraphics(ITextureManager textureManager){
        this.textureManager = textureManager;
    }
    
    public void setInner(GuiGraphicsExtractor inner){
        this.inner = inner;
    }
    
    public GuiGraphicsExtractor getInner() {
        return inner;
    }
    
    @Override
    public IFont defaultFont() {
        return (IFont) Minecraft.getInstance().font;
    }
    
    @Override
    public ITextureAtlasSprite getSprite(ResourceLocation location) {
        return textureManager.getSprite(location);
    }
    
    @Override
    public ITexture getTexture(ResourceLocation location) {
        return textureManager.getTexture(location);
    }
    
    @Override
    public void enableScissor(float x0, float y0, float x1, float y1) {
        ScreenRectangle rectangle = new ScreenRectangle((int) (x0 - scale), (int) (y0 - scale), (int) (x1 - x0 + scale * 2), (int) (y1 - y0 + scale * 2)).transformAxisAligned(this.getPose());
        inner.scissorStack.push(rectangle);
    }
    
    @Override
    public void disableScissor() {
        inner.disableScissor();
    }
    
    @Override
    public void submitColoredRectangle(IRenderPipeline pipeline, float minX, float minY, float maxX, float maxY, int colorFrom, int colorTo) {
        inner.submitGuiElementRenderState(
            new XKLibColoredRectangleRenderState(
                    (RenderPipeline) pipeline, TextureSetup.noTexture(), new Matrix3x2f(this.getPose()), minX, minY, maxX, maxY, colorFrom, colorTo, inner.scissorStack.peek()
                    )
        );
    }
    
    @Override
    public void submitColoredRoundedRectangle(float minX, float minY, float maxX, float maxY, int colorFrom, int colorTo, float radius) {
        inner.submitGuiElementRenderState(
                new RoundedRectangleRenderState(
                        TextureSetup.noTexture(), new Matrix3x2f(this.getPose()), minX, minY, maxX, maxY, colorFrom, colorTo, radius, inner.scissorStack.peek()
                )
        );
    }
    
    @Override
    public void renderLine(float x0, float y0, float x1, float y1, int colorFrom, int colorTo) {
        inner.submitGuiElementRenderState(
                        new LineRenderState(
                                new Matrix3x2f(this.getPose()), x0, y0, x1, y1, colorFrom, colorTo, inner.scissorStack.peek()
                        )
        );
    }
    
    @Override
    public void drawString(IFont font, IComponent text, float x, float y, int color, boolean drawShadow) {
        inner.text((Font) font, ComponentConverter.toComponent(text), (int) x, (int) y,color,drawShadow);
    }
    
    @Override
    public void innerBlit(IRenderPipeline renderPipeline, ResourceLocation location, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1, int color) {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(VanillaUtils.convertRL(location));
        inner.submitGuiElementRenderState(
                new XKLibBlitRenderState(
                        (RenderPipeline) renderPipeline,
                        TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()),
                        new Matrix3x2f(this.getPose()),
                        x0,
                        y0,
                        x1,
                        y1,
                        u0,
                        u1,
                        v0,
                        v1,
                        color,
                        inner.scissorStack.peek()
                )
        );
    }
    
    public void innerBlit(IRenderPipeline renderPipeline, Identifier location, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1, int color) {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(location);
        inner.submitGuiElementRenderState(
                new XKLibBlitRenderState(
                        (RenderPipeline) renderPipeline,
                        TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()),
                        new Matrix3x2f(this.getPose()),
                        x0,
                        y0,
                        x1,
                        y1,
                        u0,
                        u1,
                        v0,
                        v1,
                        color,
                        inner.scissorStack.peek()
                )
        );
    }
    
    @Override
    public void blitSprite(IRenderPipeline renderPipeline, ResourceLocation location, float x, float y, float width, float height, int color) {
        TextureAtlasSprite sprite = this.inner.guiSprites.getSprite(VanillaUtils.convertRL(location));
        GuiSpriteScaling scaling = GuiGraphicsExtractor.getSpriteScaling(sprite);
        switch (scaling) {
            case GuiSpriteScaling.Stretch stretch:
                this.blitSprite(renderPipeline, ITextureAtlasSprite.cast(sprite), x, y, width, height, color);
                break;
            case GuiSpriteScaling.Tile tile:
                this.blitTiledSprite(renderPipeline, sprite, x, y, width, height, 0, 0, tile.width(), tile.height(), tile.width(), tile.height(), color);
                break;
            case GuiSpriteScaling.NineSlice nineSlice:
                this.blitNineSlicedSprite(renderPipeline, sprite, nineSlice, x, y, width, height, color);
                break;
            default:
        }
    }
    
    @Override
    public void submitBlit(IRenderPipeline pipeline, ITexture textureView, float x0, float y0, float x1, float y1, float u0, float u1, float v0, float v1, int color) {
        LOGGER.warn("Not supported operation: submitBlit.");
    }
    
    @Override
    public void draw() {
    }
    
    @Override
    public Matrix3x2fStack getPose() {
        return inner.pose();
    }
    
    @Override
    public void layerUp() {
        inner.nextStratum();
    }
    
    @Override
    public void layerDown() {
        LOGGER.warn("Not supported operation: layerDown.");
    }
    
    private void blitTiledSprite(
            IRenderPipeline renderPipeline,
            TextureAtlasSprite sprite,
            float x,
            float y,
            float width,
            float height,
            float textureX,
            float textureY,
            float tileWidth,
            float tileHeight,
            float spriteWidth,
            float spriteHeight,
            int color
    ) {
        if (width > 0 && height > 0) {
            if (tileWidth > 0 && tileHeight > 0) {
                AbstractTexture spriteTexture = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation());
                GpuTextureView texture = spriteTexture.getTextureView();
                this.innerTiledBlit(
                        renderPipeline,
                        texture,
                        spriteTexture.getSampler(),
                        tileWidth,
                        tileHeight,
                        x,
                        y,
                        x + width,
                        y + height,
                        sprite.getU(textureX / spriteWidth),
                        sprite.getU((textureX + tileWidth) / spriteWidth),
                        sprite.getV(textureY / spriteHeight),
                        sprite.getV((textureY + tileHeight) / spriteHeight),
                        color
                );
            } else {
                throw new IllegalArgumentException("Tile size must be positive, got " + tileWidth + "x" + tileHeight);
            }
        }
    }
    
    public void blitSprite(
            IRenderPipeline renderPipeline,
            TextureAtlasSprite sprite,
            float spriteWidth,
            float spriteHeight,
            float textureX,
            float textureY,
            float x,
            float y,
            float width,
            float height,
            int color
    ){
        this.blitSprite(renderPipeline, ITextureAtlasSprite.cast(sprite), spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, color);
    }
    
    private void innerTiledBlit(
            IRenderPipeline pipeline,
            GpuTextureView textureView,
            GpuSampler sampler,
            float tileWidth,
            float tileHeight,
            float x0,
            float y0,
            float x1,
            float y1,
            float u0,
            float u1,
            float v0,
            float v1,
            int color
    ) {
        inner.submitGuiElementRenderState(
                        new XKLibTiledBliRenderState(
                                (RenderPipeline) pipeline,
                                TextureSetup.singleTexture(textureView, sampler),
                                new Matrix3x2f(this.getPose()),
                                tileWidth,
                                tileHeight,
                                x0,
                                y0,
                                x1,
                                y1,
                                u0,
                                u1,
                                v0,
                                v1,
                                color,
                                inner.scissorStack.peek()
                        )
                );
    }
    
    private void blitNineSlicedSprite(
            IRenderPipeline renderPipeline,
            TextureAtlasSprite sprite,
            GuiSpriteScaling.NineSlice nineSlice,
            float x,
            float y,
            float width,
            float height,
            int color
    ) {
        GuiSpriteScaling.NineSlice.Border border = nineSlice.border();
        var borderLeft = Math.min(border.left(), width / 2);
        var borderRight = Math.min(border.right(), width / 2);
        var borderTop = Math.min(border.top(), height / 2);
        var borderBottom = Math.min(border.bottom(), height / 2);
        if (width == nineSlice.width() && height == nineSlice.height()) {
            this.blitSprite(renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0f, 0f, x, y, width, height, color);
        } else if (height == nineSlice.height()) {
            this.blitSprite(renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, borderLeft, height, color);
            this.blitNineSliceInnerSegment(
                    renderPipeline,
                    nineSlice,
                    sprite,
                    x + borderLeft,
                    y,
                    width - borderRight - borderLeft,
                    height,
                    borderLeft,
                    0,
                    nineSlice.width() - borderRight - borderLeft,
                    nineSlice.height(),
                    nineSlice.width(),
                    nineSlice.height(),
                    color
            );
            this.blitSprite(
                    renderPipeline,
                    sprite,
                    nineSlice.width(),
                    nineSlice.height(),
                    nineSlice.width() - borderRight,
                    0,
                    x + width - borderRight,
                    y,
                    borderRight,
                    height,
                    color
            );
        } else if (width == nineSlice.width()) {
            this.blitSprite(renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, borderTop, color);
            this.blitNineSliceInnerSegment(
                    renderPipeline,
                    nineSlice,
                    sprite,
                    x,
                    y + borderTop,
                    width,
                    height - borderBottom - borderTop,
                    0,
                    borderTop,
                    nineSlice.width(),
                    nineSlice.height() - borderBottom - borderTop,
                    nineSlice.width(),
                    nineSlice.height(),
                    color
            );
            this.blitSprite(
                    renderPipeline,
                    sprite,
                    nineSlice.width(),
                    nineSlice.height(),
                    0,
                    nineSlice.height() - borderBottom,
                    x,
                    y + height - borderBottom,
                    width,
                    borderBottom,
                    color
            );
        } else {
            this.blitSprite(renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, borderLeft, borderTop, color);
            this.blitNineSliceInnerSegment(
                    renderPipeline,
                    nineSlice,
                    sprite,
                    x + borderLeft,
                    y,
                    width - borderRight - borderLeft,
                    borderTop,
                    borderLeft,
                    0,
                    nineSlice.width() - borderRight - borderLeft,
                    borderTop,
                    nineSlice.width(),
                    nineSlice.height(),
                    color
            );
            this.blitSprite(
                    renderPipeline,
                    sprite,
                    nineSlice.width(),
                    nineSlice.height(),
                    nineSlice.width() - borderRight,
                    0,
                    x + width - borderRight,
                    y,
                    borderRight,
                    borderTop,
                    color
            );
            this.blitSprite(
                    renderPipeline,
                    sprite,
                    nineSlice.width(),
                    nineSlice.height(),
                    0,
                    nineSlice.height() - borderBottom,
                    x,
                    y + height - borderBottom,
                    borderLeft,
                    borderBottom,
                    color
            );
            this.blitNineSliceInnerSegment(
                    renderPipeline,
                    nineSlice,
                    sprite,
                    x + borderLeft,
                    y + height - borderBottom,
                    width - borderRight - borderLeft,
                    borderBottom,
                    borderLeft,
                    nineSlice.height() - borderBottom,
                    nineSlice.width() - borderRight - borderLeft,
                    borderBottom,
                    nineSlice.width(),
                    nineSlice.height(),
                    color
            );
            this.blitSprite(
                    renderPipeline,
                    sprite,
                    nineSlice.width(),
                    nineSlice.height(),
                    nineSlice.width() - borderRight,
                    nineSlice.height() - borderBottom,
                    x + width - borderRight,
                    y + height - borderBottom,
                    borderRight,
                    borderBottom,
                    color
            );
            this.blitNineSliceInnerSegment(
                    renderPipeline,
                    nineSlice,
                    sprite,
                    x,
                    y + borderTop,
                    borderLeft,
                    height - borderBottom - borderTop,
                    0,
                    borderTop,
                    borderLeft,
                    nineSlice.height() - borderBottom - borderTop,
                    nineSlice.width(),
                    nineSlice.height(),
                    color
            );
            this.blitNineSliceInnerSegment(
                    renderPipeline,
                    nineSlice,
                    sprite,
                    x + borderLeft,
                    y + borderTop,
                    width - borderRight - borderLeft,
                    height - borderBottom - borderTop,
                    borderLeft,
                    borderTop,
                    nineSlice.width() - borderRight - borderLeft,
                    nineSlice.height() - borderBottom - borderTop,
                    nineSlice.width(),
                    nineSlice.height(),
                    color
            );
            this.blitNineSliceInnerSegment(
                    renderPipeline,
                    nineSlice,
                    sprite,
                    x + width - borderRight,
                    y + borderTop,
                    borderRight,
                    height - borderBottom - borderTop,
                    nineSlice.width() - borderRight,
                    borderTop,
                    borderRight,
                    nineSlice.height() - borderBottom - borderTop,
                    nineSlice.width(),
                    nineSlice.height(),
                    color
            );
        }
    }
    
    private void blitNineSliceInnerSegment(
            IRenderPipeline renderPipeline,
            GuiSpriteScaling.NineSlice nineSlice,
            TextureAtlasSprite sprite,
            float x,
            float y,
            float width,
            float height,
            float textureX,
            float textureY,
            float textureWidth,
            float textureHeight,
            float spriteWidth,
            float spriteHeight,
            int color
    ) {
        if (width > 0 && height > 0) {
            if (nineSlice.stretchInner()) {
                this.innerBlit(
                        renderPipeline,
                        sprite.atlasLocation(),
                        x,
                        x + width,
                        y,
                        y + height,
                        sprite.getU(textureX / spriteWidth),
                        sprite.getU((textureX + textureWidth) / spriteWidth),
                        sprite.getV(textureY / spriteHeight),
                        sprite.getV((textureY + textureHeight) / spriteHeight),
                        color
                );
            } else {
                this.blitTiledSprite(
                        renderPipeline, sprite, x, y, width, height, textureX, textureY, textureWidth, textureHeight, spriteWidth, spriteHeight, color
                );
            }
        }
    }
}
