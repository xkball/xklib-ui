package com.xkball.xklib.ui;

import com.xkball.xklib.ui.backend.window.DrawTestGuiGraphics;

public class XKLibUI {

    public static final String NAME = "xklib";
    
    public static void main(String[] args) {
        Thread.currentThread().setName("xklib-ui-test-main");
        new DrawTestGuiGraphics().run();
    }
}