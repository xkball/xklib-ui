package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.ui.css.property.value.CssLengthUnit;
import com.xkball.xklib.ui.layout.TextScale;

public interface ITextDisplayWidget {
    
    void setLineHeight(float height);
    void setTextColor(int color);
    void setTextScale(TextScale scale);
    void setDropShadow(boolean dropShadow);
    void setExtraWidth(float width);
    
    default void setExtraWidth(CssLengthUnit l){
        this.setExtraWidth(l.resolve(0));
    }
    
}
