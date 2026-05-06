package com.xkball.xklibmc_example.api.client.map;

import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.WindowedContainer;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import com.xkball.xklibmc_example.client.terrain.LevelChunkStorage;
import com.xkball.xklibmc_example.ui.widget.WorldTerrainWidgetInner;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public interface WorldMapExtensionService {

    String extensionId();

    WorldMapExtensionService scope(String extensionId);

    void addLeftBarWidget(Widget widget);

    void addTopBar1Widget(Widget widget);

    void addTopBar2Widget(Widget widget);

    WindowedContainer.SubWindow addSubWindow(Widget content, float width, float height);

    WindowedContainer.SubWindow addSubWindow(Widget content, float x, float y, float width, float height);

    WindowedContainer.SubWindow addSubWindow(Widget content, String title, boolean resizable, float width, float height);

    WindowedContainer.SubWindow addSubWindow(Widget content, String title, boolean resizable, float x, float y, float width, float height);

    void registerPipLayer(PictureInPictureRenderLayer<WorldTerrainPipRenderer, WorldTerrainPipRenderer.WorldTerrainState> layer);

    void addEnabledLayer(String layerName);

    void setInnerOverlayProvider(Supplier<Widget> provider);

    void refreshInnerOverlay();

    @Nullable Vector3f projScreen2World(double screenX, double screenY);

    @Nullable Vector2f projWorld2Screen(Vector3f worldPos);

    @Nullable LevelChunkStorage currentStorage();

    WorldTerrainWidgetInner inner();
}
