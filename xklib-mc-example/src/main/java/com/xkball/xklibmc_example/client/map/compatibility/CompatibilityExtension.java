package com.xkball.xklibmc_example.client.map.compatibility;

import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.CheckBox;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.WindowedContainer;
import com.xkball.xklibmc_example.ClientConfig;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtension;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionContext;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionService;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;

import java.util.ArrayList;

public class CompatibilityExtension implements WorldMapExtension {

    @Override
    public String id() {
        return "compatibility";
    }

    @Override
    public void init(WorldMapExtensionContext context) {
    }

    @Override
    public void onMapOpened(WorldMapExtensionService service) {
    }

    public static void initCompatibilityMode() {
        var reasons = new ArrayList<String>();

        if (ClientConfig.FORCE_COMPATIBILITY_MODE.get()) {
            reasons.add("Config: forceCompatibilityMode is enabled");
        }

        var missingExtensions = GLCompatibilityChecker.checkMissingExtensions();
        for (var ext : missingExtensions) {
            reasons.add("Missing GL extension: " + ext);
        }

        if (!reasons.isEmpty()) {
            TerrainChunkManager.INSTANCE.compatibleMode = true;
            TerrainChunkManager.INSTANCE.compatibilityReasons = reasons;
        }
    }

    public static void showWarningIfNeeded(WorldMapExtensionService service) {
        if (!TerrainChunkManager.INSTANCE.compatibleMode) {
            return;
        }
        if (TerrainChunkManager.INSTANCE.compatibilityWarningSuppressed) {
            return;
        }

        var dontShowAgain = new BooleanLayoutVariable(false);

        var content = new ContainerWidget()
                .inlineStyle("flex-direction: column; padding: 8px; size: 100% 100%;");

        content.addChild(new Label("Compatibility mode is active."));
        content.addChild(new Label("Reasons:"));

        for (var reason : TerrainChunkManager.INSTANCE.compatibilityReasons) {
            content.addChild(new Label("  - " + reason));
        }

        var bottomRow = new ContainerWidget()
                .inlineStyle("flex-direction: row; align-items: center; margin-top: auto;");
        var cb = new CheckBox();
        cb.inlineStyle("size: 14px 14px; flex-shrink: 0;");
        cb.bind(dontShowAgain);
        bottomRow.addChild(cb);
        bottomRow.addChild(new Label("Don't show again")
                .inlineStyle("margin-left: 4px; text-scale: expand-width;"));

        var subWindowRef = new WindowedContainer.SubWindow[1];

        var okButton = new Button("OK", () -> {
            if (dontShowAgain.get()) {
                TerrainChunkManager.INSTANCE.compatibilityWarningSuppressed = true;
            }
            if (subWindowRef[0] != null) {
                subWindowRef[0].close();
            }
        });
        okButton.inlineStyle("margin-left: auto; size: 40px 18px; text-scale: expand-width; text-align: center;");
        bottomRow.addChild(okButton);

        var gui = GuiSystem.INSTANCE.get();
        var x = Math.max(0f, (gui.screenWidth - 380) / 2f);
        var y = Math.max(0f, (gui.screenHeight - 240) / 2f);
        subWindowRef[0] = service.addBlockingSubWindow(content, "Compatibility Mode Warning", false, x, y, 380, 240);
    }
}
