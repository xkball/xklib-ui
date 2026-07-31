package com.xkball.xklibmc.ui.screen;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuTexture;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.ScalableContainer;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.client.b3d.texture.B3dTextureList;
import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklibmc.ui.widget.TexturePreviewWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

@NonNullByDefault
public class TexturePreviewScreen extends XKLibBaseScreen {

    private final ContainerWidget textureTable = new ContainerWidget();
    private final ContainerWidget textureList = new ContainerWidget();
    private final TextureViewport viewport = new TextureViewport();
    private Set<GpuTexture> knownTextures = Set.of();
    private @Nullable GpuTexture selectedTexture;

    public TexturePreviewScreen() {
        super(Component.empty());
        this.textureTable.inlineStyle("""
                flex-direction: column;
                size: 100% 100%;
                min-size: 0 0;
                background-color: 0xEE171A1F;
                """);
        this.textureTable.addChild(this.createTableHeader());
        this.textureTable.addChild(this.textureList);
        this.textureList.inlineStyle("""
                flex-direction: column;
                width: 100%;
                flex-grow: 1;
                min-size: 0 0;
                overflow-y: scroll;
                scrollbar-width: 6;
                """);
        this.addScreenLayer(XKLibBaseScreen.biPanelFrame(
                IComponent.literal("Texture Preview"),
                this.textureTable,
                this.viewport
        ));
        this.refreshTextureList(B3dTextureList.getChecked());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        var textures = B3dTextureList.getChecked();
        if (!textures.equals(this.knownTextures)) {
            this.refreshTextureList(textures);
        }
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void removed() {
        this.viewport.closeTextureView();
        super.removed();
    }

    private void refreshTextureList(Set<GpuTexture> textures) {
        this.knownTextures = Set.copyOf(textures);
        if (this.selectedTexture != null && (this.selectedTexture.isClosed() || !textures.contains(this.selectedTexture))) {
            this.selectedTexture = null;
            this.viewport.setTexture(null);
        }
        this.textureList.clearChildren();
        if (textures.isEmpty()) {
            this.textureList.addChild(new Label("No textures", 0xFFB8BDC7).inlineStyle("""
                    size: 100% 12rpx;
                    flex-shrink: 0;
                    text-align: center;
                    text-height: 8rpx;
                    """));
            return;
        }
        var sortedTextures = new ArrayList<>(textures);
        sortedTextures.sort(Comparator.comparingLong(TexturePreviewScreen::textureSize).reversed()
                .thenComparing(GpuTexture::getLabel)
                .thenComparingInt(TexturePreviewScreen::textureId));
        for (var texture : sortedTextures) {
            this.textureList.addChild(this.createTextureRow(texture));
        }
    }

    private ContainerWidget createTableHeader() {
        var header = new ContainerWidget();
        header.inlineStyle("""
                flex-direction: row;
                size: 100% 14rpx;
                flex-shrink: 0;
                background-color: 0xFF20252B;
                border-bottom: 1rpx;
                border-color: 0xFF454C55;
                """);
        header.addChild(this.createHeaderCell("Name", "40%"));
        header.addChild(this.createHeaderCell("ID", "10%"));
        header.addChild(this.createHeaderCell("Size", "25%"));
        header.addChild(this.createHeaderCell("Format", "16%"));
        header.addChild(this.createHeaderCell("L/M", "9%"));
        return header;
    }

    private Label createHeaderCell(String text, String width) {
        var label = new Label(text, 0xFFB8BDC7);
        label.inlineStyle("""
                """ + "width: " + width + ";\n" + """
                height: 100%;
                flex-shrink: 0;
                text-scale: fit-to-max;
                text-align: left;
                padding-left: 3rpx;
                padding-right: 3rpx;
                border-right: 1rpx;
                border-color: 0xFF454C55;
                """);
        label.setOverflow(false);
        return label;
    }

    private ContainerWidget createTextureRow(GpuTexture texture) {
        var row = new TextureTableRow(texture);
        row.inlineStyle("""
                flex-direction: row;
                size: 100% 12rpx;
                flex-shrink: 0;
                border-bottom: 1rpx;
                border-color: 0xFF171A1F;
                """);
        Runnable callback = () -> this.selectTexture(texture);
        row.addChild(this.createTableCell(textureName(texture), "40%", callback));
        row.addChild(this.createTableCell(Integer.toString(textureId(texture)), "10%", callback));
        row.addChild(this.createTableCell(texture.getWidth(0) + "x" + texture.getHeight(0), "25%", callback));
        row.addChild(this.createTableCell(texture.getFormat().toString(), "16%", callback));
        row.addChild(this.createTableCell(texture.getDepthOrLayers() + "/" + texture.getMipLevels(), "9%", callback));
        return row;
    }

    private Button createTableCell(String text, String width, Runnable callback) {
        var button = new Button(text, callback);
        button.inlineStyle("""
                """ + "width: " + width + ";\n" + """
                height: 100%;
                flex-shrink: 0;
                text-scale: fit-to-max;
                text-align: left;
                text-color: 0xFFF1F3F5;
                background-color: 0;
                button-hover-color: 0x334F91BD;
                padding-left: 3rpx;
                padding-right: 3rpx;
                border-right: 1rpx;
                border-color: 0xFF3A4048;
                """);
        button.setOverflow(false);
        return button;
    }

    private void selectTexture(GpuTexture texture) {
        var textures = B3dTextureList.getChecked();
        if (texture.isClosed() || !textures.contains(texture)) {
            this.refreshTextureList(textures);
            return;
        }
        this.selectedTexture = texture;
        this.viewport.setTexture(texture);
    }

    private static String textureName(GpuTexture texture) {
        var label = texture.getLabel();
        return label.isBlank() ? "<unnamed>" : label;
    }

    private static int textureId(GpuTexture texture) {
        return texture instanceof GlTexture glTexture ? glTexture.glId() : -1;
    }

    private static long textureSize(GpuTexture texture) {
        return (long) texture.getWidth(0) * texture.getHeight(0) * texture.getDepthOrLayers();
    }

    private final class TextureTableRow extends ContainerWidget {

        private final GpuTexture texture;

        private TextureTableRow(GpuTexture texture) {
            this.texture = texture;
        }

        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            var color = this.texture == TexturePreviewScreen.this.selectedTexture ? 0xFF376F92 : 0xFF292E35;
            graphics.fill(this.x, this.y, this.getMaxX(), this.getMaxY(), color);
            super.doRender(graphics, mouseX, mouseY, a);
        }
    }

    private static final class TextureViewport extends ScalableContainer {

        private final TexturePreviewWidget preview = new TexturePreviewWidget();
        private boolean fitPending;

        private TextureViewport() {
            this.setMinScale(0.01f);
            this.setMaxScale(64f);
            this.setGridEnabled(true);
            this.inlineStyle("size: 100% 100%; background-color: 0xFF101317;");
            this.addChild(this.preview);
        }

        private void setTexture(@Nullable GpuTexture texture) {
            this.preview.setTexture(texture);
            this.fitPending = texture != null;
        }

        private void closeTextureView() {
            this.preview.closeTextureView();
        }

        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            if (this.fitPending) {
                this.fitTexture();
            }
            super.doRender(graphics, mouseX, mouseY, a);
        }

        private void fitTexture() {
            var texture = this.preview.getTexture();
            if (texture == null || this.width <= 0 || this.height <= 0) {
                return;
            }
            var textureWidth = texture.getWidth(0);
            var textureHeight = texture.getHeight(0);
            if (textureWidth <= 0 || textureHeight <= 0) {
                this.fitPending = false;
                return;
            }
            var availableWidth = Math.max(this.width - 32, 1);
            var availableHeight = Math.max(this.height - 32, 1);
            this.scale = this.clampScale(Math.min(availableWidth / textureWidth, availableHeight / textureHeight));
            this.xOffset = (this.width - textureWidth * this.scale) / 2;
            this.yOffset = (this.height - textureHeight * this.scale) / 2;
            this.fitPending = false;
        }
    }
}
