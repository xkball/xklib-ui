package com.xkball.xklib.ui.css;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.css.IStyleProperty;

import java.util.List;

public record StyleSheetUnit(int weight, ISelector selector, List<IStyleProperty> properties) {



    
}
