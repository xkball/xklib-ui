package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.widget.AbstractContainerWidget;
import com.xkball.xklib.ui.widget.AbstractWidget;

import java.util.List;

public class GridLayout extends AbstractContainerWidget<GridLayout, GridElementParam> {
    
    private GridParam gridParam;
    private int[] colOffsets;
    private int[] rowOffsets;
    private int[] colSizes;
    private int[] rowSizes;
    
    public GridLayout() {
        super();
    }
    
    public GridLayout(GridParam param) {
        super();
        this.setGridParam(param);
    }
    
    public GridLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public GridLayout setGridParam(GridParam param) {
        this.gridParam = param;
        this.markDirty();
        return this;
    }
    
    public GridParam getGridParam() {
        return this.gridParam;
    }
    
    @Override
    public void resize() {
        super.resize();
        
        if (this.gridParam == null) {
            return;
        }
        
        int colCount = this.gridParam.getColCount();
        int rowCount = this.gridParam.getRowCount();
        
        if (colCount == 0 || rowCount == 0) {
            return;
        }
        
        this.colSizes = calculateSizes(this.gridParam.getCols(), this.contentWidth);
        this.rowSizes = calculateSizes(this.gridParam.getRows(), this.contentHeight);
        
        this.colOffsets = calculateOffsets(this.colSizes, this.contentX);
        this.rowOffsets = calculateOffsets(this.rowSizes, this.contentY);
        
        for (var entry : this.children.entrySet()) {
            AbstractWidget child = entry.getKey();
            GridElementParam param = entry.getValue();
            
            int row = normalizeIndex(param.row(), rowCount);
            int col = normalizeIndex(param.col(), colCount);
            int rowspan = clampSpan(param.rowspan(), row, rowCount);
            int colspan = clampSpan(param.colspan(), col, colCount);
            
            int cellX = this.colOffsets[col];
            int cellY = this.rowOffsets[row];
            int cellWidth = calculateSpanSize(this.colSizes, col, colspan);
            int cellHeight = calculateSpanSize(this.rowSizes, row, rowspan);
            
            int marginL = child.marginLeftPercent ? (int)(child.marginLeft * cellWidth) : (int)child.marginLeft;
            int marginR = child.marginRightPercent ? (int)(child.marginRight * cellWidth) : (int)child.marginRight;
            int marginT = child.marginTopPercent ? (int)(child.marginTop * cellHeight) : (int)child.marginTop;
            int marginB = child.marginBottomPercent ? (int)(child.marginBottom * cellHeight) : (int)child.marginBottom;
            
            child.setX(cellX + marginL);
            child.setY(cellY + marginT);
            child.setWidth(cellWidth - marginL - marginR);
            child.setHeight(cellHeight - marginT - marginB);
            child.markDirty();
        }
    }
    
    private int[] calculateSizes(List<SizeParam> params, int totalSize) {
        int count = params.size();
        int[] sizes = new int[count];
        
        int usedSize = 0;
        int totalWeight = 0;
        
        for (int i = 0; i < count; i++) {
            SizeParam param = params.get(i);
            if (param instanceof SizeParam.Weight(int value)) {
                totalWeight += value;
                sizes[i] = -value;
            } else {
                int size = param.calculateSize(totalSize, 0);
                sizes[i] = size;
                usedSize += size;
            }
        }
        
        int remainingSize = Math.max(0, totalSize - usedSize);
        int baseSize = totalWeight > 0 ? remainingSize / totalWeight : 0;
        
        for (int i = 0; i < count; i++) {
            if (sizes[i] < 0) {
                int weight = -sizes[i];
                sizes[i] = weight * baseSize;
            }
        }
        
        return sizes;
    }
    
    private int[] calculateOffsets(int[] sizes, int startOffset) {
        int[] offsets = new int[sizes.length];
        int currentOffset = startOffset;
        for (int i = 0; i < sizes.length; i++) {
            offsets[i] = currentOffset;
            currentOffset += sizes[i];
        }
        return offsets;
    }
    
    private int normalizeIndex(int index, int count) {
        if (index < 0) {
            return Math.max(0, count + index);
        }
        return Math.min(index, count - 1);
    }
    
    private int clampSpan(int span, int startIndex, int count) {
        return Math.min(Math.max(1, span), count - startIndex);
    }
    
    private int calculateSpanSize(int[] sizes, int startIndex, int span) {
        int totalSize = 0;
        for (int i = startIndex; i < startIndex + span && i < sizes.length; i++) {
            totalSize += sizes[i];
        }
        return totalSize;
    }
    
    @Override
    public void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
        super.renderDebug(graphics, mouseX, mouseY);
        
        if (this.gridParam == null || this.colOffsets == null || this.rowOffsets == null) {
            return;
        }
        
        int debugColor = 0x80FF0000;
        
        for (int i = 0; i < this.colOffsets.length; i++) {
            int x = this.colOffsets[i];
            graphics.vLine(x, this.contentY, this.contentY + this.contentHeight, debugColor);
        }
        if (this.colSizes.length > 0) {
            int lastX = this.colOffsets[this.colOffsets.length - 1] + this.colSizes[this.colSizes.length - 1];
            graphics.vLine(lastX, this.contentY, this.contentY + this.contentHeight, debugColor);
        }
        
        for (int i = 0; i < this.rowOffsets.length; i++) {
            int y = this.rowOffsets[i];
            graphics.hLine(this.contentX, this.contentX + this.contentWidth, y, debugColor);
        }
        if (this.rowSizes.length > 0) {
            int lastY = this.rowOffsets[this.rowOffsets.length - 1] + this.rowSizes[this.rowSizes.length - 1];
            graphics.hLine(this.contentX, this.contentX + this.contentWidth, lastY, debugColor);
        }
    }
}
