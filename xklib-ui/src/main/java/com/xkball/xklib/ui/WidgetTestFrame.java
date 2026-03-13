package com.xkball.xklib.ui;

import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.x3d.backend.window.WindowAppBase;


import java.util.function.Supplier;

/**
 * 使用try with resource创建后直接调用run即可
 */
public class WidgetTestFrame extends WindowAppBase {
    
    private final Supplier<Widget> widgetSupplier;
    public Widget widget;
    private GuiSystem guiSystem ;
    
    public WidgetTestFrame(Supplier<Widget> widgetSupplier){
        this.widgetSupplier = widgetSupplier;
    }
    
    @Override
    public void close() throws Exception {
        this.guiSystem.close();
        super.close();
    }
    
    @Override
    public void init() {
        super.init();
        this.guiSystem = GuiSystem.INSTANCE.get();
        this.widget = widgetSupplier.get();
        guiSystem.addScreenLayer(this.widget);
    }
    
    @Override
    public void render() {
        super.render();
        guiSystem.render();
    }
}
