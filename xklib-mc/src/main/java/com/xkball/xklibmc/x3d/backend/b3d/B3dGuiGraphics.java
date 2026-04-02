package com.xkball.xklibmc.x3d.backend.b3d;

import com.mojang.blaze3d.pipeline.RenderPipeline;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.slf4j.Logger;

public class B3dGuiGraphics implements IGUIGraphics {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    private GuiGraphicsExtractor inner;
    private ITextureManager textureManager;
    
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
        inner.enableScissor((int) x0, (int) y0, (int) x1, (int) y1);
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
}
