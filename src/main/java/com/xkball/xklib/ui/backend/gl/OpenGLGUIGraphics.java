package com.xkball.xklib.ui.backend.gl;

import com.xkball.xklib.api.gui.IComponent;
import com.xkball.xklib.api.gui.IGUIGraphics;
import com.xkball.xklib.api.render.IFont;
import com.xkball.xklib.api.render.IRenderPipeline;
import com.xkball.xklib.api.render.ITexture;
import com.xkball.xklib.api.render.ITextureAtlasSprite;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.gl.state.ColoredRectangleRenderState;
import com.xkball.xklib.ui.backend.gl.state.GuiRenderState;
import com.xkball.xklib.ui.navigation.ScreenRectangle;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class OpenGLGUIGraphics implements IGUIGraphics {
    
    private final Matrix3x2fStack pose = new Matrix3x2fStack();
    private final ScissorStack scissorStack = new ScissorStack();
    private final GuiRenderState guiRenderState;
    
    public OpenGLGUIGraphics(GuiRenderState guiRenderState) {
        this.guiRenderState = guiRenderState;
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
                                pipeline, () -> null, new Matrix3x2f(this.pose), x0, y0, x1, y1, colorFrom, colorTo, this.scissorStack.peek()
                        )
                );
    }
    
    @Override
    public void drawString(IFont font, IComponent text, int x, int y, int color, boolean drawShadow) {
    
    }
    
    @Override
    public void submitBlit(IRenderPipeline pipeline, ITexture textureView, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color) {
    
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
