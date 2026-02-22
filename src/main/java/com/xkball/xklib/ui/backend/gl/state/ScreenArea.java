package com.xkball.xklib.ui.backend.gl.state;

import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;

public interface ScreenArea {
    @Nullable
    ScreenRectangle bounds();
}