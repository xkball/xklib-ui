package com.xkball.xklib.ui.backend.window;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.widgets.Button;
import com.xkball.xklib.ui.widget.GuiSystem;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.layout.GridLayout;

import java.util.function.Supplier;

/**
 * 创建后直接调用run即可
 */
public class WidgetTestFrame extends WindowAppBase{
    
    private final Supplier<AbstractWidget> widgetSupplier;
    private final GuiSystem guiSystem = XKLib.gui;
    
    public WidgetTestFrame(Supplier<AbstractWidget> widgetSupplier){
        this.widgetSupplier = widgetSupplier;
    }
    
    @Override
    public void init() {
        super.init();
        guiSystem.addScreenLayer(widgetSupplier.get());
    }
    
    @Override
    public void render() {
        super.render();
        guiSystem.render();
    }
    
    public static WidgetTestFrame gridTest = new WidgetTestFrame(() -> {
        var grid = new GridLayout();
        grid.setGridParam(new GridParam.Builder()
                .addCol("100px")
                .addCol("1")
                .addCol("30%")
                .addCol("2")
                .addRow("50px")
                .addRow("1")
                .addRow("20%")
                .addRow("1")
                .build());
        
        var btn1 = new Button("(0,0)", () -> System.out.println("btn1"));
        btn1.setMargin("5%");
        grid.addChild(btn1, new GridElementParam(0, 0, 1, 1));
        
        var btn2 = new Button("(0,1) colspan=2", () -> System.out.println("btn2"));
        btn2.setMargin("5%");
        grid.addChild(btn2, new GridElementParam(0, 1, 1, 2));
        
        var btn3 = new Button("(0,3)", () -> System.out.println("btn3"));
        btn3.setMargin("5%");
        grid.addChild(btn3, new GridElementParam(0, 3, 1, 1));
        
        var btn4 = new Button("(1,0) rowspan=3", () -> System.out.println("btn4"));
        btn4.setMargin("5%");
        grid.addChild(btn4, new GridElementParam(1, 0, 3, 1));
        
        var btn5 = new Button("(1,1) 2x2", () -> System.out.println("btn5"));
        btn5.setMargin("5%");
        grid.addChild(btn5, new GridElementParam(1, 1, 2, 2));
        
        var btn6 = new Button("(1,3) rowspan=2", () -> System.out.println("btn6"));
        btn6.setMargin("5%");
        grid.addChild(btn6, new GridElementParam(1, 3, 2, 1));
        
        var btn7 = new Button("(3,1) colspan=2", () -> System.out.println("btn7"));
        btn7.setMargin("5%");
        grid.addChild(btn7, new GridElementParam(3, 1, 1, 2));
        
        var btn8 = new Button("(3,3)", () -> System.out.println("btn8"));
        btn8.setMargin("5%");
        grid.addChild(btn8, new GridElementParam(3, 3, 1, 1));
        
        return grid;
    });
    
    public static WidgetTestFrame flexTest1 = new WidgetTestFrame(() -> {
        var rootFlex = new FlexLayout(new FlexParam.Builder()
                .direction(FlexParam.Direction.ROW)
                .justify(FlexParam.Align.START)
                .align(FlexParam.Align.CENTER)
                .overflow(false)
                .build()) {
            @Override
            protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
                this.offsetX += (int)(scrollY * 20);
                this.markDirty();
                return true;
            }
        };
        
        var leftPanel = new FlexLayout(new FlexParam.Builder()
                .direction(FlexParam.Direction.COL)
                .justify(FlexParam.Align.START)
                .align(FlexParam.Align.CENTER)
                .overflow(false)
                .build()) {
            @Override
            protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
                this.offsetY += (int)(scrollY * 20);
                this.markDirty();
                return true;
            }
        };
        leftPanel.setMargin(5, 5, 5, 5);
        
        for (int i = 0; i < 40; i++) {
            int finalI = i;
            var btn = new Button("Left " + i, () -> System.out.println("Left button clicked" + finalI));
            btn.setMargin(2, 2, 2, 2);
            leftPanel.addChild(btn, FlexElementParam.of(i, "100%", "50px"));
        }
        
        var centerPanel = new FlexLayout(new FlexParam.Builder()
                .direction(FlexParam.Direction.COL)
                .justify(FlexParam.Align.SPACE_AROUND)
                .align(FlexParam.Align.CENTER)
                .overflow(true)
                .build());
        centerPanel.setMargin(5, 5, 5, 5);
        
        var btn1 = new Button("Center Top", () -> System.out.println("Center Top"));
        btn1.setMargin(2, 2, 2, 2);
        centerPanel.addChild(btn1, FlexElementParam.of(0, "80%", "1"));
        
        var btn2 = new Button("Center Mid", () -> System.out.println("Center Mid"));
        btn2.setMargin(2, 2, 2, 2);
        centerPanel.addChild(btn2, FlexElementParam.of(1, "80%", "2"));
        
        var btn3 = new Button("Center Bot", () -> System.out.println("Center Bot"));
        btn3.setMargin(2, 2, 2, 2);
        centerPanel.addChild(btn3, FlexElementParam.of(2, "80%", "1"));
        
        var rightPanel = new FlexLayout(new FlexParam.Builder()
                .direction(FlexParam.Direction.COL_REVERSE)
                .justify(FlexParam.Align.END)
                .align(FlexParam.Align.END)
                .overflow(true)
                .build());
        rightPanel.setMargin(5, 5, 5, 5);
        
        var innerRow = new FlexLayout(new FlexParam.Builder()
                .direction(FlexParam.Direction.ROW)
                .justify(FlexParam.Align.SPACE_BETWEEN)
                .align(FlexParam.Align.CENTER)
                .overflow(true)
                .build());
        innerRow.setMargin(2, 2, 2, 2);
        
        var innerBtn1 = new Button("R1", () -> System.out.println("R1"));
        innerBtn1.setMargin(2, 2, 2, 2);
        innerRow.addChild(innerBtn1, FlexElementParam.of(0, "30%", "100%"));
        
        var innerBtn2 = new Button("R2", () -> System.out.println("R2"));
        innerBtn2.setMargin(2, 2, 2, 2);
        innerRow.addChild(innerBtn2, FlexElementParam.of(1, "30%", "100%"));
        
        var innerBtn3 = new Button("R3", () -> System.out.println("R3"));
        innerBtn3.setMargin(2, 2, 2, 2);
        innerRow.addChild(innerBtn3, FlexElementParam.of(2, "30%", "100%"));
        
        rightPanel.addChild(innerRow, FlexElementParam.of(0, "100%", "80px"));
        
        var rightBtn = new Button("Right Bottom", () -> System.out.println("Right Bottom"));
        rightBtn.setMargin(2, 2, 2, 2);
        rightPanel.addChild(rightBtn, FlexElementParam.of(1, "90%", "60px"));
        
        rootFlex.addChild(leftPanel, FlexElementParam.of(0, "150px", "100%"));
        rootFlex.addChild(centerPanel, FlexElementParam.of(1, "1", "100%"));
        rootFlex.addChild(rightPanel, FlexElementParam.of(2, "200px", "100%"));
        
        return rootFlex;
    });
}
