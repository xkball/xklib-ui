package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.render.IComponent;

@GuiWidgetClass
public class Button extends Label {
    
    private Runnable callback = () -> {};
    
    public Button(String text, Runnable callback){
        this.text = IComponent.literal(text);
        this.callback = callback;
    }
    
    public Button() {
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        callback.run();
        return true;
    }
    
    public Runnable getCallback() {
        return callback;
    }
    
    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
    
}
