package com.xkball.xklib.ui.layout;

import com.xkball.xklib.api.gui.widget.ILayoutParma;

/**
 * row,col应该支持负数 此时如果span过大则clamp到边缘
 */
public record GridElementParam(int row, int col, int rowspan, int colspan) implements ILayoutParma {

}
