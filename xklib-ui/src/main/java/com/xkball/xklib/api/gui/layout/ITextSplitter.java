package com.xkball.xklib.api.gui.layout;

import com.xkball.xklib.ui.render.IFont;

import java.util.List;

public interface ITextSplitter {
    
    List<String> split(IFont font, String text, float width);
    
}
