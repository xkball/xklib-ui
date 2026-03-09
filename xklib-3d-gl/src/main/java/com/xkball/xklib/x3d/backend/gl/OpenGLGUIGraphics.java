package com.xkball.xklib.x3d.backend.gl;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.render.IFont;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.IRenderPipelineSource;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.api.render.ITextureAtlasSprite;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.x3d.backend.gl.font.Font;
import com.xkball.xklib.x3d.backend.gl.pipeline.RenderPipelines;
import com.xkball.xklib.x3d.backend.gl.state.BlitRenderState;
import com.xkball.xklib.x3d.backend.gl.state.ColoredRectangleRenderState;
import com.xkball.xklib.x3d.backend.gl.state.GuiRenderState;
import com.xkball.xklib.x3d.backend.gl.state.LineRenderState;
import com.xkball.xklib.x3d.backend.gl.state.RoundedRectangleRenderState;
import com.xkball.xklib.x3d.backend.gl.state.TextRenderState;
import com.xkball.xklib.x3d.backend.gl.state.TextureSetup;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;

public class OpenGLGUIGraphics implements IGUIGraphics {
    
    private final Matrix3x2fStack pose = new Matrix3x2fStack(32);
    private final ScissorStack scissorStack = new ScissorStack();
    private final GuiRenderState guiRenderState;
    private final IFont defaultFont;
    private final Consumer<Void> drawCall;
    
    public OpenGLGUIGraphics(GuiRenderState guiRenderState, IFont defaultFont, Consumer<Void> drawCall) {
        this.guiRenderState = guiRenderState;
        this.defaultFont = defaultFont;
        this.drawCall = drawCall;
    }
    
    @Override
    public IFont defaultFont() {
        return defaultFont;
    }
    
    @Override
    public ITextureAtlasSprite getSprite(ResourceLocation location) {
        return Objects.requireNonNull(XKLib.RENDER_CONTEXT.get().getTextureManager().getSprite(location));
    }
    
    @Override
    public ITexture getTexture(ResourceLocation location) {
        return XKLib.RENDER_CONTEXT.get().getTextureManager().getTexture(location);
    }
    
    @Override
    public Matrix3x2fStack getPose() {
        return pose;
    }
    
    @Override
    public void layerUp() {
        this.guiRenderState.up();
    }
    
    @Override
    public void layerDown() {
        this.guiRenderState.down();
    }
    
    @Override
    public void enableScissor(float x0, float y0, float x1, float y1) {
        var rectangle = new ScreenRectangle((int) x0, (int) y0, (int) (x1 - x0), (int) (y1 - y0)).transformAxisAligned(this.pose);
        this.scissorStack.push(rectangle);
    }
    
    @Override
    public void disableScissor() {
        this.scissorStack.pop();
    }
    
    @Override
    public void submitColoredRectangle(IRenderPipeline pipeline, float x0, float y0, float x1, float y1, int colorFrom, int colorTo) {
        this.guiRenderState
                .submitGuiElement(
                        new ColoredRectangleRenderState(
                                pipeline, TextureSetup.EMPTY, new Matrix3x2f(this.pose), x0, y0, x1, y1, colorFrom, colorTo, this.scissorStack.peek()
                        )
                );
    }
    
    @Override
    public void submitColoredRoundedRectangle(float minX, float minY, float maxX, float maxY, int colorFrom, int colorTo, float radius) {
        this.guiRenderState
                .submitGuiElement(
                        new RoundedRectangleRenderState(
                                IRenderPipelineSource.getInstance().getGuiRoundedRect(), TextureSetup.EMPTY, new Matrix3x2f(this.pose), minX, minY, maxX, maxY, colorFrom, colorTo, radius, this.scissorStack.peek()
                        )
                );
    }
    
    @Override
    public void renderLine(float x0, float y0, float x1, float y1, int colorFrom, int colorTo) {
        this.guiRenderState
                .submitGuiElement(
                        new LineRenderState(
                                new Matrix3x2f(this.pose), x0, y0, x1, y1, colorFrom, colorTo, this.scissorStack.peek()
                        )
                );
    }
    
    @Override
    public void drawString(IFont font, IComponent text, float x, float y, int color, boolean drawShadow) {
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
                                RenderPipelines.FONT.get(),
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
    public void submitBlit(IRenderPipeline pipeline, ITexture textureView, float x0, float y0, float x1, float y1, float u0, float u1, float v0, float v1, int color) {
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
        drawCall.accept(null);
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
