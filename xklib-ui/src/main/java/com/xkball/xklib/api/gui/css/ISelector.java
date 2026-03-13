package com.xkball.xklib.api.gui.css;

import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.List;

public interface ISelector {

    List<IGuiWidget> select(IGuiWidget widget);
    
}
