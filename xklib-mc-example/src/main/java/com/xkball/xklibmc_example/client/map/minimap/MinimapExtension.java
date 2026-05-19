package com.xkball.xklibmc_example.client.map.minimap;

import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.widget.CheckBox;
import com.xkball.xklib.ui.widget.IconButton;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.WindowedContainer;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtension;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionContext;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionService;
import com.xkball.xklibmc_example.ui.widget.IntSliderWidget;
import org.jspecify.annotations.Nullable;

public class MinimapExtension implements WorldMapExtension {

    public static final String EXTENSION_ID = "minimap";
    public static final String KEY_RENDER_RANGE = "render_range_chunks";
    public static final String KEY_HIGH_DETAIL_RANGE = "high_detail_range_chunks";
    public static final String KEY_ROTATE_WITH_PLAYER = "rotate_with_player";

    private final IntLayoutVariable renderRange = new IntLayoutVariable(16);
    private final IntLayoutVariable highDetailRange = new IntLayoutVariable(8);
    private final BooleanLayoutVariable rotateWithPlayer = new BooleanLayoutVariable(false);
    private float camXRot = 89.0f;
    private float camYRot = 0.0f;
    private float camFov = 60.0f;
    private float camCameraLength = 0.0f;
    private WindowedContainer.@Nullable SubWindow configWindow;

    public static @Nullable MinimapExtension INSTANCE;

    @Override
    public String id() {
        return EXTENSION_ID;
    }

    @Override
    public void init(WorldMapExtensionContext context) {
        INSTANCE = this;
    }

    @Override
    public void onMapOpened(WorldMapExtensionService service) {
        this.load(service);
        this.bindPersistence(service);
        service.addTopBar2Widget(new IconButton(VanillaUtils.modrl("icon/map"), () -> this.openConfig(service))
                .withTooltip(IComponent.literal("Open minimap settings.")));
        service.addTopBar2Widget(new Widget().setCSSClassName("splitter"));
    }

    @Override
    public void onMapClosed(WorldMapExtensionService service) {
        this.configWindow = null;
    }

    public int renderRange() {
        return Math.clamp(this.renderRange.get(), 4, 64);
    }

    public int highDetailRange() {
        return Math.min(Math.clamp(this.highDetailRange.get(), 4, 64), this.renderRange());
    }

    public boolean rotateWithPlayer() {
        return this.rotateWithPlayer.get();
    }

    public IntLayoutVariable renderRangeVariable() {
        return renderRange;
    }

    public IntLayoutVariable highDetailRangeVariable() {
        return highDetailRange;
    }

    public BooleanLayoutVariable rotateWithPlayerVariable() {
        return rotateWithPlayer;
    }

    public float camXRot() {
        return camXRot;
    }

    public void setCamXRot(float value) {
        this.camXRot = Math.clamp(value, -89.9f, 89.9f);
    }

    public float camFov() {
        return camFov;
    }

    public void setCamFov(float value) {
        this.camFov = Math.clamp(value, 5, 90);
    }

    public float camCameraLength() {
        return camCameraLength;
    }

    public void setCamCameraLength(float value) {
        this.camCameraLength = Math.max(value, 0);
    }

    private void openConfig(WorldMapExtensionService service) {
        if(this.configWindow != null && this.configWindow.visible()) return;
        this.load(service);
        this.configWindow = service.addSubWindow(this.createConfigContent(service), "Minimap", false, 140 * CssLengthUnit.rpxScaleWorkaround, 240 * CssLengthUnit.rpxScaleWorkaround);
    }

    private Widget createConfigContent(WorldMapExtensionService service) {
        var preview = new MinimapPreviewWidget(renderRange, highDetailRange, rotateWithPlayer)
                .inlineStyle("size: 116rpx 116rpx; flex-shrink: 0; margin: 5rpx; border: 2rpx; border-color: 0xCCAAAAAA;");
        return new ContainerWidget()
                .inlineStyle("""
                        flex-direction: column;
                        size: 100% 100%;
                        padding: 6rpx;
                        scrollbar-width: 0;
                        overflow-y: scroll;
                        """)
                .asRootStyle("""
                        .minimap_row {
                            height: 12rpx;
                            flex-direction: row;
                            align-items: center;
                            margin-top: 2rpx;
                            flex-shrink: 0;
                        }
                        .minimap_label {
                            size: 50% 12rpx;
                            text-color: -1;
                            text-scale: expand-width;
                            flex-shrink: 0;
                        }
                        CheckBox {
                            size: 34rpx 12rpx;
                            flex-shrink: 0;
                            margin-left: auto;
                        }
                        """)
                .addChild(preview)
                .addChild(this.sliderRow("Render Range", this.renderRange))
                .addChild(this.sliderRow("High Detail", this.highDetailRange))
                .addChild(new ContainerWidget()
                        .setCSSClassName("minimap_row")
                        .addChild(new Label("Rotate Map").setCSSClassName("minimap_label"))
                        .addChild(new CheckBox().bind(this.rotateWithPlayer)));
    }

    private Widget sliderRow(String label, IntLayoutVariable variable) {
        return new ContainerWidget()
                .setCSSClassName("minimap_row")
                .addChild(new Label(label).setCSSClassName("minimap_label"))
                .addChild(new IntSliderWidget(4, 64, variable.get()).bind(variable));
    }

    private void load(WorldMapExtensionService service) {
        this.renderRange.set(Math.clamp(service.getIntState(KEY_RENDER_RANGE, this.renderRange.get()), 4, 64));
        this.highDetailRange.set(Math.clamp(service.getIntState(KEY_HIGH_DETAIL_RANGE, this.highDetailRange.get()), 4, 64));
        this.rotateWithPlayer.set(service.getBooleanState(KEY_ROTATE_WITH_PLAYER, this.rotateWithPlayer.get()));
    }

    private void bindPersistence(WorldMapExtensionService service) {
        this.renderRange.addCallback(value -> service.setIntState(KEY_RENDER_RANGE, Math.clamp(value, 4, 64)));
        this.highDetailRange.addCallback(value -> service.setIntState(KEY_HIGH_DETAIL_RANGE, Math.clamp(value, 4, 64)));
        this.rotateWithPlayer.addCallback(value -> service.setBooleanState(KEY_ROTATE_WITH_PLAYER, value));
    }
}
