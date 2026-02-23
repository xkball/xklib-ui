package com.xkball.xklib.ui;

import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.layout.GridLayout;

public class XKLibUI {

    public static final String NAME = "xklib";
    
    public static void main(String[] args) {
        Thread.currentThread().setName("xklib-ui-test-main");
        new WidgetTestFrame(() -> {
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
            btn1.setMarginPercent(0.05f);
            grid.addChild(btn1, new GridElementParam(0, 0, 1, 1));
            
            var btn2 = new Button("(0,1) colspan=2", () -> System.out.println("btn2"));
            btn2.setMarginPercent(0.05f);
            grid.addChild(btn2, new GridElementParam(0, 1, 1, 2));
            
            var btn3 = new Button("(0,3)", () -> System.out.println("btn3"));
            btn3.setMarginPercent(0.05f);
            grid.addChild(btn3, new GridElementParam(0, 3, 1, 1));
            
            var btn4 = new Button("(1,0) rowspan=3", () -> System.out.println("btn4"));
            btn4.setMarginPercent(0.05f);
            grid.addChild(btn4, new GridElementParam(1, 0, 3, 1));
            
            var btn5 = new Button("(1,1) 2x2", () -> System.out.println("btn5"));
            btn5.setMarginPercent(0.05f);
            grid.addChild(btn5, new GridElementParam(1, 1, 2, 2));
            
            var btn6 = new Button("(1,3) rowspan=2", () -> System.out.println("btn6"));
            btn6.setMarginPercent(0.05f);
            grid.addChild(btn6, new GridElementParam(1, 3, 2, 1));
            
            var btn7 = new Button("(3,1) colspan=2", () -> System.out.println("btn7"));
            btn7.setMarginPercent(0.05f);
            grid.addChild(btn7, new GridElementParam(3, 1, 1, 2));
            
            var btn8 = new Button("(3,3)", () -> System.out.println("btn8"));
            btn8.setMarginPercent(0.05f);
            grid.addChild(btn8, new GridElementParam(3, 3, 1, 1));
            
            return grid;
        }).run();
    }
}