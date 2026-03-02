package com.xkball.xklib.x3d.backend.gl.state;

import com.xkball.xklib.ui.layout.ScreenRectangle;
import org.jetbrains.annotations.Nullable;

public interface IScreenArea {
    @Nullable
    ScreenRectangle bounds();
}