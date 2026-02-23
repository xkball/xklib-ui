package com.xkball.xklib.ui.layout;

import com.xkball.xklib.api.gui.widget.ILayoutParma;

public class GridElementParam implements ILayoutParma {
    
    public int row;
    public int col;
    public int rowspan;
    public int colspan;
    
    public GridElementParam(int row, int col, int rowspan, int colspan) {
        this.row = row;
        this.col = col;
        this.rowspan = rowspan;
        this.colspan = colspan;
    }
    
    public int row() {
        return this.row;
    }
    
    public int col() {
        return this.col;
    }
    
    public int rowspan() {
        return this.rowspan;
    }
    
    public int colspan() {
        return this.colspan;
    }
}
