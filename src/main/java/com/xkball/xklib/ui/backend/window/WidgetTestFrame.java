package com.xkball.xklib.ui.backend.window;

import com.xkball.xklib.XKLibWorkaround;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.GuiSystem;

import java.util.function.Supplier;

public class WidgetTestFrame extends WindowAppBase{
    
    private final Supplier<AbstractWidget> widgetSupplier;
    private final GuiSystem guiSystem = XKLibWorkaround.gui;
    
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
