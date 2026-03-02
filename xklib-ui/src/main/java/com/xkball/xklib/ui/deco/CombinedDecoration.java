package com.xkball.xklib.ui.deco;

import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IDecoration;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.ArrayList;
import java.util.List;

public class CombinedDecoration implements IDecoration {
    
    private final List<IDecoration> decorations = new ArrayList<>();
    
    public void addDecoration(IDecoration deco) {
        this.decorations.add(deco);
    }
    
    @Override
    public void render(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        for (IDecoration deco : this.decorations) {
            deco.render(widget, graphics, mouseX, mouseY, a);
        }
    }
}
