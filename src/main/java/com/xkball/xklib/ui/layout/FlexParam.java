package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.widget.layout.FlexLayout;

/**
 * 不提供换行
 */
public class FlexParam {
    
    public Direction direction = Direction.ROW;
    /*
    副轴的布局, 使用SPACE_XX时无效,(使效果等同于CENTER), 如果元素使用过大的w/h, 则允许溢出.
     */
    public Align align = Align.START;
    /*
    主轴的布局, 当为START,CENTER,END时, 允许子元素溢出父元素.
     */
    public Align justify = Align.START;
    /*
    控制是否显示溢出部分, 并不控制是否允许溢出.
     */
    public boolean overflow = false;
    
    /*
    如果子元素使用的size使用Weight, 则此值为一份权重的大小, 如果本身填入的就是Weight, 则等同于100%.
     */
    public SizeParam baseSize = SizeParam.parse("5%");
    
    public FlexParam() {
    }
    
    public FlexParam(Direction direction, Align align, Align justify, boolean overflow, SizeParam baseSize) {
        this.direction = direction;
        this.align = align;
        this.justify = justify;
        this.overflow = overflow;
        this.baseSize = baseSize;
    }
    
    public FlexLayout newFlexLayout(){
        return new FlexLayout(this);
    }
    
    public static class Builder {
        private Direction direction = Direction.ROW;
        private Align align = Align.START;
        private Align justify = Align.START;
        private boolean overflow = false;
        private SizeParam baseSize = SizeParam.parse("5%");
        
        public Builder direction(Direction direction){
            this.direction = direction;
            return this;
        }
        
        public Builder align(Align align){
            this.align = align;
            return this;
        }
        
        public Builder justify(Align justify){
            this.justify = justify;
            return this;
        }
        
        public Builder overflow(boolean overflow){
            this.overflow = overflow;
            return this;
        }
        
        public Builder baseSize(SizeParam baseSize){
            this.baseSize = baseSize;
            return this;
        }
        
        public FlexParam build(){
            return new FlexParam(direction, align, justify, overflow, baseSize);
        }
        
    }
    
    public enum Direction {
        ROW,
        COL,
        ROW_REVERSE,
        COL_REVERSE
    }
    
    public enum Align {
        START,
        CENTER,
        END,
        SPACE_BETWEEN,
        SPACE_AROUND
    }
    
    @Override
    public String toString() {
        return "FlexParam{" +
                "direction=" + direction +
                ", align=" + align +
                ", justify=" + justify +
                ", overflow=" + overflow +
                ", baseSize=" + baseSize +
                '}';
    }
}
