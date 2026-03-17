package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import dev.vfyjxf.taffy.geometry.TaffySize;

public class HeightProperty extends AbstractLengthProperty {
    
    public static final String NAME = "height";
    
    public HeightProperty(CssLengthUnit value) {
        super(value);
    }
    
    @Override
    public String propertyName() {
        return NAME;
    }
    
    @Override
    public void apply(IStyleSheet sheet, IGuiWidget widget) {
        widget.setStyle( s -> s.size = TaffySize.of(s.size.width,value.toDimension()));
    }
}
