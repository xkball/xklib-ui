package com.xkball.xklib.ui.backend.gl;

import com.xkball.xklib.api.gui.render.IComponent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.render.IFont;
import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.api.render.IRenderPipelineSource;
import com.xkball.xklib.api.render.ITexture;
import com.xkball.xklib.api.render.ITextureAtlasSprite;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.gl.font.Font;
import com.xkball.xklib.ui.backend.gl.pipeline.RenderPipelines;
import com.xkball.xklib.ui.backend.gl.state.BlitRenderState;
import com.xkball.xklib.ui.backend.gl.state.ColoredRectangleRenderState;
import com.xkball.xklib.ui.backend.gl.state.GuiRenderState;
import com.xkball.xklib.ui.backend.gl.state.RoundedRectangleRenderState;
import com.xkball.xklib.ui.backend.gl.state.TextRenderState;
import com.xkball.xklib.ui.backend.gl.state.TextureSetup;
import com.xkball.xklib.ui.navigation.ScreenRectangle;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class OpenGLGUIGraphics implements IGUIGraphics {
    
    private final Matrix3x2fStack pose = new Matrix3x2fStack(32);
    private final ScissorStack scissorStack = new ScissorStack();
    private final GuiRenderState guiRenderState;
    
    public OpenGLGUIGraphics(GuiRenderState guiRenderState) {
        this.guiRenderState = guiRenderState;
    }
    
    @Override
    public IFont defaultFont() {
        return OpenGLWorkaround.font;
    }
    
    @Override
    public ITextureAtlasSprite getSprite(ResourceLocation location) {
        return Objects.requireNonNull(OpenGLWorkaround.textureManager.getSprite(location));
    }
    
    @Override
    public ITexture getTexture(ResourceLocation location) {
        return OpenGLWorkaround.textureManager.getTexture(location);
    }
    
    @Override
    public Matrix3x2fStack getPose() {
        return pose;
    }
    
    @Override
    public void enableScissor(int x0, int y0, int x1, int y1) {
        var rectangle = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformAxisAligned(this.pose);
        this.scissorStack.push(rectangle);
    }
    
    @Override
    public void disableScissor() {
        this.scissorStack.pop();
    }
    
    @Override
    public void submitColoredRectangle(IRenderPipeline pipeline, int x0, int y0, int x1, int y1, int colorFrom, int colorTo) {
        this.guiRenderState
                .submitGuiElement(
                        new ColoredRectangleRenderState(
                                pipeline, TextureSetup.EMPTY, new Matrix3x2f(this.pose), x0, y0, x1, y1, colorFrom, colorTo, this.scissorStack.peek()
                        )
                );
    }
    
    @Override
    public void submitColoredRoundedRectangle(int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo, int radius) {
        this.guiRenderState
                .submitGuiElement(
                        new RoundedRectangleRenderState(
                                IRenderPipelineSource.getInstance().getGuiRoundedRect(), TextureSetup.EMPTY, new Matrix3x2f(this.pose), minX, minY, maxX, maxY, colorFrom, colorTo, radius, this.scissorStack.peek()
                        )
                );
    }
    
    @Override
    public void drawString(IFont font, IComponent text, int x, int y, int color, boolean drawShadow) {
        if (!(font instanceof Font glFont)) {
            return;
        }
        String textContent = text.visit();
        if (textContent == null || textContent.isEmpty()) {
            return;
        }
        this.guiRenderState
                .submitGuiElement(
                        new TextRenderState(
                                RenderPipelines.FONT,
                                TextureSetup.singleTexture(glFont.getAtlas()),
                                new Matrix3x2f(this.pose),
                                glFont,
                                textContent,
                                x, y,
                                color,
                                drawShadow,
                                this.scissorStack.peek()
                        )
                );
    }
    
    @Override
    public void submitBlit(IRenderPipeline pipeline, ITexture textureView, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color) {
        this.guiRenderState
                .submitGuiElement(
                        new BlitRenderState(
                                pipeline,
                                TextureSetup.singleTexture(textureView),
                                new Matrix3x2f(this.pose),
                                x0, y0, x1, y1,
                                u0, u1, v0, v1,
                                color, this.scissorStack.peek()
                        )
                );
    }
    
    @Override
    public void draw() {
        OpenGLWorkaround.guiRenderer.draw();
    }
    
    public static class ScissorStack {
        private final Deque<ScreenRectangle> stack = new ArrayDeque<>();
        
        public ScreenRectangle push(ScreenRectangle rectangle) {
            ScreenRectangle lastRectangle = this.stack.peekLast();
            if (lastRectangle != null) {
                ScreenRectangle intersection = Objects.requireNonNullElse(rectangle.intersection(lastRectangle), ScreenRectangle.empty());
                this.stack.addLast(intersection);
                return intersection;
            } else {
                this.stack.addLast(rectangle);
                return rectangle;
            }
        }
        
        public @Nullable ScreenRectangle pop() {
            if (this.stack.isEmpty()) {
                throw new IllegalStateException("Scissor stack underflow");
            } else {
                this.stack.removeLast();
                return this.stack.peekLast();
            }
        }
        
        public @Nullable ScreenRectangle peek() {
            return this.stack.peekLast();
        }
        
        public boolean containsPoint(int x, int y) {
            return this.stack.isEmpty() || this.stack.peek().containsPoint(x, y);
        }
    }
}
