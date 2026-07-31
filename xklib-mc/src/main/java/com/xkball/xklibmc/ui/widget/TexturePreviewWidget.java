package com.xkball.xklibmc.ui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.logging.LogUtils;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.api.client.b3d.SamplerCacheCache;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.TaffyDimension;
import net.minecraft.client.renderer.RenderPipelines;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@NonNullByDefault
public class TexturePreviewWidget extends Widget {

    private static final Logger LOGGER = LogUtils.getLogger();

    private @Nullable GpuTexture texture;
    private @Nullable GpuTextureView textureView;

    public TexturePreviewWidget() {
        this.inlineStyle("background-color: 0xFF20242A;");
    }

    public void setTexture(@Nullable GpuTexture texture) {
        this.closeTextureView();
        this.texture = texture;
        if (texture == null || texture.isClosed() || (texture.usage() & GpuTexture.USAGE_TEXTURE_BINDING) == 0) {
            this.setTextureSize(0, 0);
            return;
        }
        this.setTextureSize(texture.getWidth(0), texture.getHeight(0));
        try {
            this.textureView = RenderSystem.getDevice().createTextureView(texture);
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to create texture preview view for {}", texture.getLabel(), exception);
        }
    }

    public @Nullable GpuTexture getTexture() {
        return this.texture;
    }

    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        var currentTexture = this.texture;
        var currentView = this.textureView;
        if (currentTexture == null || currentView == null || currentTexture.isClosed() || currentView.isClosed()) {
            return;
        }
        if (graphics instanceof B3dGuiGraphics b3dGraphics) {
            var flipped = (currentTexture.usage() & GpuTexture.USAGE_RENDER_ATTACHMENT) != 0;
            b3dGraphics.innerBlit(
                    (IRenderPipeline) RenderPipelines.GUI_TEXTURED,
                    currentView,
                    SamplerCacheCache.NEAREST_CLAMP,
                    this.x,
                    this.getMaxX(),
                    this.y,
                    this.getMaxY(),
                    0,
                    1,
                    flipped ? 1 : 0,
                    flipped ? 0 : 1,
                    -1
            );
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        this.closeTextureView();
    }

    public void closeTextureView() {
        if (this.textureView != null) {
            this.textureView.close();
            this.textureView = null;
        }
    }

    private void setTextureSize(int width, int height) {
        this.setAbsoluteLayout(0, 0, width, height);
        this.setStyle(style -> style.size = TaffySize.of(TaffyDimension.length(width), TaffyDimension.length(height)));
        this.markDirty();
    }
}
