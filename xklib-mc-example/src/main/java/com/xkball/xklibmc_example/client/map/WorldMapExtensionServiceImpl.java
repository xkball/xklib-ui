package com.xkball.xklibmc_example.client.map;

import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.WindowedContainer;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionService;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import com.xkball.xklibmc_example.client.terrain.LevelChunkStorage;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc_example.client.map.uistate.WorldMapUiStateStorage;
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
    public WindowedContainer.SubWindow addBlockingSubWindow(Widget content, float width, float height) {
        return this.addBlockingSubWindow(content, "", true, width, height);
    }

    @Override
    public WindowedContainer.SubWindow addBlockingSubWindow(Widget content, String title, boolean resizable, float width, float height) {
        var parent = this.widget.windowLayer();
        var x = Math.max(0f, (parent.getWidth() - width) / 2f);
        var y = Math.max(0f, (parent.getHeight() - height) / 2f);
        return this.addBlockingSubWindow(content, title, resizable, x, y, width, height);
    }

    @Override
    public WindowedContainer.SubWindow addBlockingSubWindow(Widget content, String title, boolean resizable, float x, float y, float width, float height) {
        var layer = new WindowedContainer();
        layer.setAutoRemoveFromGuiSystemWhenEmpty(true);
        layer.inlineStyle("size: 100% 100%; background-color: 0x55000000;");
        layer.setBlockInput(true);
        var parent = this.widget.windowLayer();
        var window = layer.addSubWindow(content, title, resizable, x, y, width, height);
        GuiSystem.INSTANCE.get().insertLayerAfter(layer, parent);
        return window;
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
    public boolean containsState(String key) {
        var storage = this.uiStateStorage();
        return storage != null && storage.contains(this.stateKey(key));
    }

    @Override
    public void removeState(String key) {
        var storage = this.uiStateStorage();
        if (storage != null) {
            storage.remove(this.stateKey(key));
        }
    }

    @Override
    public boolean getBooleanState(String key, boolean defaultValue) {
        var storage = this.uiStateStorage();
        return storage == null ? defaultValue : storage.getBoolean(this.stateKey(key), defaultValue);
    }

    @Override
    public void setBooleanState(String key, boolean value) {
        var storage = this.uiStateStorage();
        if (storage != null) {
            storage.setBoolean(this.stateKey(key), value);
        }
    }

    @Override
    public int getIntState(String key, int defaultValue) {
        var storage = this.uiStateStorage();
        return storage == null ? defaultValue : storage.getInt(this.stateKey(key), defaultValue);
    }

    @Override
    public void setIntState(String key, int value) {
        var storage = this.uiStateStorage();
        if (storage != null) {
            storage.setInt(this.stateKey(key), value);
        }
    }

    @Override
    public float getFloatState(String key, float defaultValue) {
        var storage = this.uiStateStorage();
        return storage == null ? defaultValue : storage.getFloat(this.stateKey(key), defaultValue);
    }

    @Override
    public void setFloatState(String key, float value) {
        var storage = this.uiStateStorage();
        if (storage != null) {
            storage.setFloat(this.stateKey(key), value);
        }
    }

    @Override
    public String getStringState(String key, String defaultValue) {
        var storage = this.uiStateStorage();
        return storage == null ? defaultValue : storage.getString(this.stateKey(key), defaultValue);
    }

    @Override
    public void setStringState(String key, String value) {
        var storage = this.uiStateStorage();
        if (storage != null) {
            storage.setString(this.stateKey(key), value);
        }
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

    private @Nullable WorldMapUiStateStorage uiStateStorage() {
        var storage = this.currentStorage();
        if (storage == null) {
            return null;
        }
        var extensionStorage = storage.getExtensionStorage(WorldMapUiStateStorage.EXTENSION_ID);
        if (extensionStorage instanceof WorldMapUiStateStorage uiStateStorage) {
            return uiStateStorage;
        }
        var uiStateStorage = new WorldMapUiStateStorage();
        storage.registerExtensionStorage(uiStateStorage);
        return uiStateStorage;
    }

    private String stateKey(String key) {
        if (this.extensionId == null || this.extensionId.isEmpty()) {
            return key;
        }
        return this.extensionId + ":" + key;
    }
}
