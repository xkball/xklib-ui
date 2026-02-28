package com.xkball.xklib.ui.screen;

import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.FloatLayoutVariable;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.layout.ScreenBase;
import com.xkball.xklib.ui.widget.widgets.DragBox;
import com.xkball.xklib.ui.widget.widgets.Label;

public class DemoScreen extends ScreenBase {
    
    FloatLayoutVariable scale = new FloatLayoutVariable(1);
    
    public DemoScreen() {
        super("全息告示牌");
    }
    
    @Override
    public AbstractWidget initLeft() {
        var panel = new FlexParam.Builder()
                .direction(FlexParam.Direction.COL).newScrollable();
        panel.setYScrollable();
        var scaleLabel = new Label("缩放");
        scaleLabel.setPadding("10%");
        panel.addChild(scaleLabel, FlexElementParam.of("100%","16px"));
        var scaleBar = new DragBox(0.1,10,1);
        scaleBar.setPadding("10%");
        scaleBar.setOnChange(d -> scale.setAsFloat(d.floatValue()));
        var scaleDisplay = new Label("1");
        scaleDisplay.setPadding("10%");
        scale.addCallback(v -> scaleDisplay.setText(String.format("%.2f",v)));
        var scalePanel = new GridParam.Builder().addRow("1").addCol("80%").addCol("20%").newLayout();
        scalePanel.addChild(scaleBar, GridElementParam.of(0,0));
        scalePanel.addChild(scaleDisplay, GridElementParam.of(0,1));
        panel.addChild(scalePanel,FlexElementParam.of("100%","1"));
        return panel;
    }
    
    @Override
    public AbstractWidget initRight() {
        return new Label("2222222222222222222222");
    }
    
    public static void main(String[] args) {
        new WidgetTestFrame(DemoScreen::new).run();
    }
}
