package com.xkball.xklib.ui;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.GuiSystem;
import com.xkball.xklib.x3d.backend.window.WindowAppBase;


import java.util.function.Supplier;

/**
 * 创建后直接调用run即可
 */
public class WidgetTestFrame extends WindowAppBase {
    
    private final Supplier<AbstractWidget> widgetSupplier;
    private final GuiSystem guiSystem = XKLib.gui;
    
    public WidgetTestFrame(Supplier<AbstractWidget> widgetSupplier){
        this.widgetSupplier = widgetSupplier;
    }
    
    @Override
    public void init() {
        super.init();
        guiSystem.addScreenLayer(widgetSupplier.get());
    }
    
    @Override
    public void render() {
        super.render();
        guiSystem.render();
    }
}
