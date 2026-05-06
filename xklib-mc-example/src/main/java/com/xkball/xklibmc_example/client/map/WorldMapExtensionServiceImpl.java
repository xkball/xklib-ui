package com.xkball.xklibmc_example.client.map;

import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.WindowedContainer;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionService;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import com.xkball.xklibmc_example.client.terrain.LevelChunkStorage;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc_example.ui.widget.WorldTerrainWidget;
import com.xkball.xklibmc_example.ui.widget.WorldTerrainWidgetInner;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class WorldMapExtensionServiceImpl implements WorldMapExtensionService {

    private final WorldTerrainWidget widget;
    private final String extensionId;

    public WorldMapExtensionServiceImpl(WorldTerrainWidget widget, String extensionId) {
        this.widget = widget;
        this.extensionId = extensionId;
    }

    @Override
    public String extensionId() {
        return this.extensionId;
    }

    @Override
    public WorldMapExtensionService scope(String extensionId) {
        return new WorldMapExtensionServiceImpl(this.widget, extensionId);
    }

    @Override
    public void addLeftBarWidget(Widget widget) {
        this.widget.addExtensionLeftBarWidget(widget);
    }

    @Override
    public void addTopBar1Widget(Widget widget) {
        this.widget.addExtensionTopBar1Widget(widget);
    }

    @Override
    public void addTopBar2Widget(Widget widget) {
        this.widget.addExtensionTopBar2Widget(widget);
    }

    @Override
    public WindowedContainer.SubWindow addSubWindow(Widget content, float width, float height) {
        return this.widget.addMapSubWindow(content, width, height);
    }

    @Override
    public WindowedContainer.SubWindow addSubWindow(Widget content, float x, float y, float width, float height) {
        return this.widget.addMapSubWindow(content, x, y, width, height);
    }

    @Override
    public WindowedContainer.SubWindow addSubWindow(Widget content, String title, boolean resizable, float width, float height) {
        return this.widget.addMapSubWindow(content, title, resizable, width, height);
    }

    @Override
    public WindowedContainer.SubWindow addSubWindow(Widget content, String title, boolean resizable, float x, float y, float width, float height) {
        return this.widget.addMapSubWindow(content, title, resizable, x, y, width, height);
    }

    @Override
    public void registerPipLayer(PictureInPictureRenderLayer<WorldTerrainPipRenderer, WorldTerrainPipRenderer.WorldTerrainState> layer) {
        WorldTerrainPipRenderer.regRenderLayers(layer);
    }

    @Override
    public void addEnabledLayer(String layerName) {
        this.widget.inner.addExtensionEnabledLayer(this.extensionId, layerName);
    }

    @Override
    public void setInnerOverlayProvider(Supplier<Widget> provider) {
        this.widget.inner.setExtensionOverlayProvider(this.extensionId, provider);
    }

    @Override
    public void refreshInnerOverlay() {
        this.widget.inner.refreshExtensionOverlay(this.extensionId);
    }

    @Override
    public @Nullable Vector3f projScreen2World(double screenX, double screenY) {
        return this.widget.inner.projScreen2World(screenX, screenY);
    }

    @Override
    public @Nullable Vector2f projWorld2Screen(Vector3f worldPos) {
        return this.widget.inner.projWorld2Screen(worldPos);
    }

    @Override
    public @Nullable LevelChunkStorage currentStorage() {
        return TerrainChunkManager.INSTANCE.getCurrentLevelChunkStorage();
    }

    @Override
    public WorldTerrainWidgetInner inner() {
        return this.widget.inner;
    }
}
