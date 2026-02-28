package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.layout.ScreenAxis;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.layout.SplitParam;
import com.xkball.xklib.ui.layout.VerticalAlign;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.widgets.Label;

public abstract class ScreenBase extends GridLayout{
    
    private final String title;
    
    public ScreenBase(String title){
        this.title = title;
        this.setGridParam(new GridParam.Builder()
                .addCol("1")
                .addRow(SizeParam.min("80px","10%"))
                .addRow("1")
                .build());
    }
    
    @Override
    public void init() {
        super.init();
        var titleL = new Label(title);
        titleL.setInnerVerticalAlign(VerticalAlign.CENTER);
        titleL.setMarginLeft("5px");
        this.addChild(titleL,GridElementParam.of(0,0));
        var split = new SplitLayout(new SplitParam(ScreenAxis.HORIZONTAL,SizeParam.parse("30%"),SizeParam.parse("70%")));
        split.setFirst(initLeft());
        split.setSecond(initRight());
        this.addChild(split,GridElementParam.of(1,0));
    }
    
    public abstract AbstractWidget initLeft();
    
    public abstract AbstractWidget initRight();
}
