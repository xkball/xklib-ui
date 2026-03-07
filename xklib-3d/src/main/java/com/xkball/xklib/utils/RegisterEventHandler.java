package com.xkball.xklib.utils;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ap.annotation.RegisterEvent;

import java.io.IOException;

public class RegisterEventHandler {
    
    public static void runRegisterEvent() {
        try(var input = RegisterEventHandler.class.getClassLoader().getResourceAsStream("META-INF/services/" + RegisterEvent.class.getName())){
            if (input == null) return;
            var strs = new String(input.readAllBytes()).split("\n");
            for(var str : strs) {
                var clazz = Class.forName(str);
                XKLib.EVENT_BUS.register(clazz);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
