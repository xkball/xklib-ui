package com.xkball.xklib.ui;

import com.xkball.xklib.ui.screen.CSSStyleDisplayScreen;

public class XKLibUI {

    public static final String NAME = "xklib";
    
    public static void main(String[] args) throws Exception {
        Thread.currentThread().setName("xklib-ui-test-main");
        try (var frame = new WidgetTestFrame(CSSStyleDisplayScreen::new)){
            frame.run();
        }
    }
}