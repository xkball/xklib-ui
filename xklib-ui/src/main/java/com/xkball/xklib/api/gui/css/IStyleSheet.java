package com.xkball.xklib.api.gui.css;

import javax.annotation.Nullable;

public interface IStyleSheet {
    
    @Nullable
    IStyleProperty<?> getProperty(String key);
    
}
