package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.widget.layout.SplitLayout;

public class SplitParam {
    
    ScreenAxis axis;
    SizeParam firstSize;
    SizeParam secondSize;
    
    public SplitParam(ScreenAxis axis, SizeParam firstSize, SizeParam secondSize) {
        this.axis = axis;
        this.firstSize = firstSize;
        this.secondSize = secondSize;
    }
    
    public ScreenAxis getAxis() {
        return this.axis;
    }
    
    public SizeParam getFirstSize() {
        return this.firstSize;
    }
    
    public SizeParam getSecondSize() {
        return this.secondSize;
    }
    
    public static class Builder {
        private ScreenAxis axis = ScreenAxis.HORIZONTAL;
        private SizeParam firstSize;
        private SizeParam secondSize;
        
        public Builder axis(ScreenAxis axis){
            this.axis = axis;
            return this;
        }
        
        public Builder firstSize(SizeParam firstSize){
            this.firstSize = firstSize;
            return this;
        }
        
        public Builder secondSize(SizeParam secondSize){
            this.secondSize = secondSize;
            return this;
        }
        
        public SplitParam build(){
            return new SplitParam(this.axis, this.firstSize, this.secondSize);
        }
        
        public SplitLayout newSplitLayout(){
            return new SplitLayout(this.build());
        }
    }
}
