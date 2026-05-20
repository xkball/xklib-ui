package com.xkball.xklibmc_example.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.IconButton;
import com.xkball.xklib.ui.widget.IconCheckBox;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.WindowedContainer;
import com.xkball.xklibmc.ui.widget.NumberInputWidget;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionService;
import com.xkball.xklibmc_example.ServerConfig;
import com.xkball.xklibmc_example.client.map.WorldMapExtensionServiceImpl;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc_example.network.c2s.RequestServerChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;

public class WorldTerrainWidget extends ContainerWidget {
    
    public final BooleanLayoutVariable terrain = new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable grid = new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable player = new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable debug = new BooleanLayoutVariable(false);
    public final BooleanLayoutVariable cameraTarget = new BooleanLayoutVariable(false);
    public final BooleanLayoutVariable compass = new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable depress_sphere = new BooleanLayoutVariable(false);
    public final IntLayoutVariable yMode = new IntLayoutVariable(1);
    public final IntLayoutVariable fixY = new IntLayoutVariable();
    public final IntLayoutVariable lodDistance = new IntLayoutVariable(512);
    public final IntLayoutVariable viewDistance = new  IntLayoutVariable(1024);
    public final WorldTerrainWidgetInner inner;
    private final ContainerWidget leftExtensionWidgets = new ContainerWidget();
    private final ContainerWidget top1ExtensionWidgets = new ContainerWidget();
    private final ContainerWidget top2ExtensionWidgets = new ContainerWidget();
    private final WindowedContainer windowLayer;
    private final WorldMapExtensionService service;
    
    public WorldTerrainWidget(WindowedContainer windowLayer, WorldMapExtensionServiceImpl service) {
        this.service = service;
        service.widget = this;
        this.windowLayer = windowLayer;
        var level = Minecraft.getInstance().level;
        var minY = level == null ? -64 : level.getMinY();
        var maxY = level == null ? 384 : level.getMaxY();
        fixY.set(level == null ? 64 : level.getSeaLevel());
        this.inner = new WorldTerrainWidgetInner(terrain, grid, player, cameraTarget, compass, depress_sphere, debug, yMode, fixY, lodDistance);
        this.initExtensions();
        this.leftExtensionWidgets.inlineStyle("""
                flex-direction: column;
                flex-shrink: 0;
                """);
        this.top1ExtensionWidgets.inlineStyle("flex-direction: row; flex-shrink: 0;");
        this.top2ExtensionWidgets.inlineStyle("flex-direction: row; flex-shrink: 0;");
        this.inlineStyle("""
                        flex-direction: row;
                        size: 100% 100%;
                        """)
                .asRootStyle("""
                        IconCheckBox {
                            size: 14rpx 14rpx;
                            margin-top: 1rpx;
                            margin-left: 1rpx;
                            iconcheckbox-bg-color: 0xAA666666;
                            flex-shrink: 0;
                        }
                        IconButton {
                            size: 14rpx 14rpx;
                            margin-top: 1rpx;
                            margin-left: 1rpx;
                            flex-shrink: 0;
                        }
                        .update_button {
                            size: content 14rpx;
                            margin-top: 1rpx;
                            margin-left: 2rpx;
                            text-align: center;
                            text-scale: expand-width;
                            button-shape: round-rect;
                            button-bg-color: rgb(229,233,239);
                            text-drop-shadow: false;
                            text-extra-width: 2rpx;
                        }
                        .property_label {
                            size: content 14rpx;
                            margin-top: 1rpx;
                            margin-left: 2rpx;
                            text-scale: expand-width;
                            text-color: -1;
                        }
                        .splitter {
                            size: 2px 100%;
                            background-color: 0xEEAAAAAA;
                            margin-left: 2rpx;
                            margin-right: 2rpx;
                            flex-shrink: 0;
                        }
                        NumberInputWidget {
                            size: 50rpx 14rpx;
                            margin-top: 1rpx;
                            margin-left: 1rpx;
                            margin-right: 1rpx;
                            flex-shrink: 0;
                        }
                        """)
                .addChild(this.createToolbarLeft())
                .addChild(new ContainerWidget()
                        .inlineStyle("""
                                flex-direction: column;
                                size: 100%-18rpx 100%;
                                """)
                        .addChild(this.createToolbarTop1(minY, maxY))
                        .addChild(this.createToolbarTop2())
                        .addChild(inner.inlineStyle("height: 100%-35rpx;"))
                );
        TerrainChunkManager.INSTANCE.worldMapExtensionRegistry.onMapOpened(this.service);
    }

    public void initExtensions() {
        this.inner.setExtensionService(this.service);
        this.loadPersistentUiState();
        this.bindPersistentUiState();
    }
    
    public Widget createToolbarLeft(){
        return new ContainerWidget()
                .inlineStyle("""
                        flex-direction: column;
                        size: 18rpx 100%;
                        border-left: 1rpx;
                        border-right: 1rpx;
                        border-color: 0xEEAAAAAA;
                        scrollbar-width: 0;
                        overflow-y: scroll;
                        """)
                .asRootStyle("""
                        .splitter_y{
                                size: 100% 1rpx;
                                background-color: 0xEEAAAAAA;
                        }
                        """)
                .addChild(new Widget().setCSSClassName("splitter_y"))
                .addChild(new IconButton(VanillaUtils.modrl("icon/locate"), inner::reLocateCamera)
                        .inlineStyle("""
                               size: 14rpx 14rpx;
                               margin: 1rpx;
                               flex-shrink: 0;
                               """)
                        .withTooltip(IComponent.literal("Focus camera on yourself.")))
                .addChild(new Widget().setCSSClassName("splitter_y"))
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/map")).bind(terrain).withTooltip(IComponent.literal("show terrain")))
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/compass")).bind(compass).withTooltip(IComponent.literal("show compass")))
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/grid")).bind(grid).withTooltip(IComponent.literal("show grid")))
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/player")).bind(player).withTooltip(IComponent.literal("show player")))
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/locate_camera")).bind(cameraTarget).withTooltip(IComponent.literal("show camera target")))
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/depress_sphere")).bind(depress_sphere).withTooltip(IComponent.literal("cull too near chunks")))
                .addChild(this.leftExtensionWidgets)
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/debug")).bind(debug).withTooltip(IComponent.literal("show debug info")));
    }
    
    public Widget createToolbarTop1(int minY, int maxY){
        return new ContainerWidget()
                .inlineStyle("""
                                        height: 18rpx;
                                        border-top: 1rpx;
                                        border-bottom: 1rpx;
                                        border-color: 0xEEAAAAAA;
                                        scrollbar-width: 0;
                                        overflow-x: scroll;
                                        """)
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/tracked_y")).bindInGroup(0,yMode).withTooltip(IComponent.literal("camera target y tracks to terrain")))
                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/fixed_y")).bindInGroup(1,yMode).withTooltip(IComponent.literal("camera target y is fixed")))
                .addChild(NumberInputWidget.ofInt(minY, maxY,1).bind(fixY))
                .addChild(new Widget().setCSSClassName("splitter"))
                .addChild(new Label("LOD Distance:").setCSSClassName("property_label").withTooltip(IComponent.literal("In blocks.")))
                .addChild(NumberInputWidget.ofInt(1,114514,16).bind(lodDistance))
                .addChild(new Label("View Distance:").setCSSClassName("property_label").withTooltip(IComponent.literal("In blocks.")))
                .addChild(NumberInputWidget.ofInt(256,1145141919,16).bind(viewDistance))
                .addChild(this.top1ExtensionWidgets)
                .addChild(new Button("Force Update",() -> {
                    var player = Minecraft.getInstance().player;
                    var viewDistance = Minecraft.getInstance().options.renderDistance().get();
                    if(player == null) return;
                    TerrainChunkManager.INSTANCE.submitUpdate(player.blockPosition(),viewDistance - 1, true);
                    })
                        .setCSSClassName("update_button")
                        .withTooltip(IComponent.literal("Update chunks in view distance."))
                        .inlineStyle("margin-left: auto;"))
                .addChild(new Button("Request Geomatics",() -> {
                    if (!ServerConfig.ALLOW_SERVER_SENT_CHUNK.get() && !XKLib.IS_DEBUG) {
                        return;
                    }
                    var player = Minecraft.getInstance().player;
                    if(player == null) return;
                    var centerChunk = ChunkPos.containing(player.blockPosition());
                    var range = 256;
                    var list = new ArrayList<ChunkPos>();
                    for(var dx = -range; dx <= range; dx++){
                        for(var dz = -range; dz <= range; dz++){
                            var p = new ChunkPos(centerChunk.x() + dx,centerChunk.z() + dz);
                            if(TerrainChunkManager.INSTANCE.getCurrentLevelChunkStorage().containsChunk(p)) continue;
                            list.add(p);
                        }
                    }
                    ClientPacketDistributor.sendToServer(new RequestServerChunk(list,false));
                }).setCSSClassName("update_button").withTooltip(IComponent.literal("Request Geomatics from Server(Requires permission from the server).")))
                .addChild(new Button("Delete",() -> {})
                        .setCSSClassName("update_button")
                        .inlineStyle("""
                                            button-bg-color: rgb(221,0,27);
                                            margin-right: 5rpx;
                                            text-color: -1;
                                        """));
    }
    
    public Widget createToolbarTop2(){
        return new ContainerWidget()
                .inlineStyle("""
                                        height: 17rpx;
                                        border-bottom: 1rpx;
                                        border-color: 0xEEAAAAAA;
                                        scrollbar-width: 0;
                                        overflow-x: scroll;
                                        """)
                .addChild(this.top2ExtensionWidgets);
    }

    public void addExtensionLeftBarWidget(Widget widget) {
        this.leftExtensionWidgets.addChild(widget);
    }

    public void addExtensionTopBar1Widget(Widget widget) {
        this.top1ExtensionWidgets.addChild(widget);
    }

    public void addExtensionTopBar2Widget(Widget widget) {
        this.top2ExtensionWidgets.addChild(widget);
    }

    public WindowedContainer.SubWindow addMapSubWindow(Widget content, float width, float height) {
        return this.windowLayer.addSubWindow(content, width, height);
    }

    public WindowedContainer.SubWindow addMapSubWindow(Widget content, float x, float y, float width, float height) {
        return this.windowLayer.addSubWindow(content, x, y, width, height);
    }

    public WindowedContainer.SubWindow addMapSubWindow(Widget content, String title, boolean resizable, float width, float height) {
        return this.windowLayer.addSubWindow(content, title, resizable, width, height);
    }

    public WindowedContainer.SubWindow addMapSubWindow(Widget content, String title, boolean resizable, float x, float y, float width, float height) {
        return this.windowLayer.addSubWindow(content, title, resizable, x, y, width, height);
    }

    public WindowedContainer windowLayer() {
        return this.windowLayer;
    }
    
    private void loadPersistentUiState() {
        this.terrain.set(this.service.getBooleanState("terrain", this.terrain.get()));
        this.grid.set(this.service.getBooleanState("grid", this.grid.get()));
        this.player.set(this.service.getBooleanState("player", this.player.get()));
        this.debug.set(this.service.getBooleanState("debug", this.debug.get()));
        this.cameraTarget.set(this.service.getBooleanState("camera_target", this.cameraTarget.get()));
        this.compass.set(this.service.getBooleanState("compass", this.compass.get()));
        this.depress_sphere.set(this.service.getBooleanState("depress_sphere", this.depress_sphere.get()));
        this.yMode.set(this.service.getIntState("y_mode", this.yMode.get()));
        this.fixY.set(this.service.getIntState("fix_y", this.fixY.get()));
        this.lodDistance.set(this.service.getIntState("lod_distance", this.lodDistance.get()));
        this.viewDistance.set(this.service.getIntState("view_distance", this.viewDistance.get()));
        TerrainChunkManager.INSTANCE.viewDistance = this.viewDistance.get();
    }

    private void bindPersistentUiState() {
        this.terrain.addCallback(value -> this.service.setBooleanState("terrain", value));
        this.grid.addCallback(value -> this.service.setBooleanState("grid", value));
        this.player.addCallback(value -> this.service.setBooleanState("player", value));
        this.debug.addCallback(value -> this.service.setBooleanState("debug", value));
        this.cameraTarget.addCallback(value -> this.service.setBooleanState("camera_target", value));
        this.compass.addCallback(value -> this.service.setBooleanState("compass", value));
        this.depress_sphere.addCallback(value -> this.service.setBooleanState("depress_sphere", value));
        this.yMode.addCallback(value -> this.service.setIntState("y_mode", value));
        this.fixY.addCallback(value -> this.service.setIntState("fix_y", value));
        this.lodDistance.addCallback(value -> this.service.setIntState("lod_distance", value));
        this.viewDistance.addCallback(value -> {
            this.service.setIntState("view_distance", value);
            TerrainChunkManager.INSTANCE.viewDistance = value;
        });
    }
    

    public void closeMapExtensions() {
        this.inner.onMapClosed(this.service);
        TerrainChunkManager.INSTANCE.worldMapExtensionRegistry.onMapClosed(this.service);
    }
    
}
