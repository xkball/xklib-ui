package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.widget.AbstractContainerWidget;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.utils.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FlexLayout extends AbstractContainerWidget<FlexLayout, FlexElementParam> {
    
    public FlexParam flexParam;
    public int offsetX = 0;
    public int offsetY = 0;
    public int actualWidth = 0;
    public int actualHeight = 0;
    
    public FlexLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public FlexLayout(FlexParam param) {
        super();
        this.setOverflow(param.overflow);
        this.flexParam = param;
    }
    
    public FlexLayout() {
        super();
    }
    
    public FlexLayout setFlexParam(FlexParam param) {
        this.flexParam = param;
        this.setOverflow(param.overflow);
        this.markDirty();
        return this;
    }
    
    public void setOffsetX(int offsetX) {
        if(this.offsetX == offsetX) return;
        this.offsetX = offsetX;
        this.markDirty();
    }
    
    public void setOffsetY(int offsetY) {
        if(this.offsetY == offsetY) return;
        this.offsetY = offsetY;
        this.markDirty();
    }
    
    @Override
    public void resize() {
        super.resize();
        
        if (this.flexParam == null || this.children.isEmpty()) {
            return;
        }
        
        List<Pair<AbstractWidget, FlexElementParam>> entries = new ArrayList<>();
        for (var entry : this.children.entrySet()) {
            entries.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        final int[] indices = new int[entries.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        entries.sort(Comparator.comparingInt((Pair<AbstractWidget, FlexElementParam> a) -> a.second.order())
                .thenComparingInt(a -> indices[entries.indexOf(a)]));
        
        boolean isRow = this.flexParam.direction == FlexParam.Direction.ROW
                     || this.flexParam.direction == FlexParam.Direction.ROW_REVERSE;
        boolean isReverse = this.flexParam.direction == FlexParam.Direction.ROW_REVERSE
                         || this.flexParam.direction == FlexParam.Direction.COL_REVERSE;
        
        int mainAxisSize = isRow ? this.contentWidth : this.contentHeight;
        int crossAxisSize = isRow ? this.contentHeight : this.contentWidth;
        int mainAxisStart = isRow ? this.contentX : this.contentY;
        int crossAxisStart = isRow ? this.contentY : this.contentX;
        
        int[] mainSizes = new int[entries.size()];
        int[] crossSizes = new int[entries.size()];
        int totalFixedMainSize = 0;
        int totalMainWeight = 0;
        int totalCrossWeight = 0;
        
        for (int i = 0; i < entries.size(); i++) {
            Pair<AbstractWidget, FlexElementParam> entry = entries.get(i);
            SizeParam mainParam = isRow ? entry.second.width() : entry.second.height();
            SizeParam crossParam = isRow ? entry.second.height() : entry.second.width();
            
            if (mainParam.isWeight()) {
                totalMainWeight += mainParam.getWeight();
                mainSizes[i] = -mainParam.getWeight();
            } else {
                int size = mainParam.calculateSize(mainAxisSize, 0);
                mainSizes[i] = size;
                totalFixedMainSize += size;
            }
            
            if (crossParam.isWeight()) {
                totalCrossWeight += crossParam.getWeight();
                crossSizes[i] = -crossParam.getWeight();
            } else {
                crossSizes[i] = crossParam.calculateSize(crossAxisSize, 0);
            }
        }
        
        int remainingMainSize = Math.max(0, mainAxisSize - totalFixedMainSize);
        int mainBaseSize = totalMainWeight > 0 ? remainingMainSize / totalMainWeight : 0;
        int crossBaseSize = totalCrossWeight > 0 ? crossAxisSize / totalCrossWeight : 0;
        int totalMainSize = totalFixedMainSize;
        
        for (int i = 0; i < entries.size(); i++) {
            if (mainSizes[i] < 0) {
                int weight = -mainSizes[i];
                int size = weight * mainBaseSize;
                mainSizes[i] = size;
                totalMainSize += size;
            }
            if (crossSizes[i] < 0) {
                int weight = -crossSizes[i];
                crossSizes[i] = weight * crossBaseSize;
            }
        }
        
        int spacing = 0;
        int startOffset = 0;
        
        switch (this.flexParam.justify) {
            case START -> {}
            case CENTER -> startOffset = (mainAxisSize - totalMainSize) / 2;
            case END -> startOffset = mainAxisSize - totalMainSize;
            case SPACE_BETWEEN -> {
                if (entries.size() > 1) {
                    spacing = (mainAxisSize - totalMainSize) / (entries.size() - 1);
                }
            }
            case SPACE_AROUND -> {
                if (!entries.isEmpty()) {
                    spacing = (mainAxisSize - totalMainSize) / entries.size();
                    startOffset = spacing / 2;
                }
            }
        }
        
        int mainPos = isReverse ? (mainAxisStart + mainAxisSize - startOffset) : (mainAxisStart + startOffset);
        mainPos += isRow ? this.offsetX : this.offsetY;
        
        for (int i = 0; i < entries.size(); i++) {
            int index = isReverse ? (entries.size() - 1 - i) : i;
            Pair<AbstractWidget, FlexElementParam> entry = entries.get(index);
            AbstractWidget child = entry.first;
            
            int mainSize = mainSizes[index];
            int crossSize = crossSizes[index];
            
            int crossPos = switch (this.flexParam.align) {
                case START -> crossAxisStart;
                case CENTER, SPACE_BETWEEN, SPACE_AROUND -> crossAxisStart + (crossAxisSize - crossSize) / 2;
                case END -> crossAxisStart + crossAxisSize - crossSize;
            };
            crossPos += isRow ? this.offsetY : this.offsetX;
            
            if (isReverse) {
                mainPos -= mainSize;
            }
            
            int marginL, marginR, marginT, marginB;
            if (isRow) {
                marginL = child.marginLeftPercent ? (int)(child.marginLeft * mainSize) : (int)child.marginLeft;
                marginR = child.marginRightPercent ? (int)(child.marginRight * mainSize) : (int)child.marginRight;
                marginT = child.marginTopPercent ? (int)(child.marginTop * crossSize) : (int)child.marginTop;
                marginB = child.marginBottomPercent ? (int)(child.marginBottom * crossSize) : (int)child.marginBottom;
                
                child.setX(mainPos + marginL);
                child.setY(crossPos + marginT);
                child.setWidth(mainSize - marginL - marginR);
                child.setHeight(crossSize - marginT - marginB);
            } else {
                marginL = child.marginLeftPercent ? (int)(child.marginLeft * crossSize) : (int)child.marginLeft;
                marginR = child.marginRightPercent ? (int)(child.marginRight * crossSize) : (int)child.marginRight;
                marginT = child.marginTopPercent ? (int)(child.marginTop * mainSize) : (int)child.marginTop;
                marginB = child.marginBottomPercent ? (int)(child.marginBottom * mainSize) : (int)child.marginBottom;
                
                child.setX(crossPos + marginL);
                child.setY(mainPos + marginT);
                child.setWidth(crossSize - marginL - marginR);
                child.setHeight(mainSize - marginT - marginB);
            }
            child.markDirty();
            
            if (isReverse) {
                mainPos -= spacing;
            } else {
                mainPos += mainSize + spacing;
            }
        }
        
        if (isRow) {
            this.actualWidth = totalMainSize;
            this.actualHeight = crossAxisSize;
        } else {
            this.actualWidth = crossAxisSize;
            this.actualHeight = totalMainSize;
        }
        
        for (var entry : this.children.entrySet()) {
            AbstractWidget child = entry.getKey();
            if (child instanceof FlexLayout fl && !fl.overflow()) {
                if (isRow) {
                    this.actualHeight = Math.max(this.actualHeight, fl.actualHeight);
                } else {
                    this.actualWidth = Math.max(this.actualWidth, fl.actualWidth);
                }
            }
        }
    }
    
    @Override
    public void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
        super.renderDebug(graphics, mouseX, mouseY);
        this.renderInScissor(graphics, () -> {
            if(this.hovered){
                int borderColor = 0x80FF00FF;
                for (var entry : this.children.entrySet()) {
                    AbstractWidget child = entry.getKey();
                    if (child.visible()) {
                        graphics.renderOutline(child.getX(), child.getY(), child.getWidth(), child.getHeight(), borderColor);
                    }
                }
            }
        });
    }
}