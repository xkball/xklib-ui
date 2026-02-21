package com.xkball.xklib.ui;

import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.widget.Button;

public class XKLibUI {

    public static final String NAME = "xklib";
    
    public static void main(String[] args) {
        Thread.currentThread().setName("xklib-ui-test-main");
        new WidgetTestFrame(() -> {
            var result = new Button("Click me", () -> System.out.println("Button clicked!"));
            result.setFixSize(200, 200);
            return result;
        }).run();
    }
}