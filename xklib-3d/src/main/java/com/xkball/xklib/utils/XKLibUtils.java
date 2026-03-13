package com.xkball.xklib.utils;

public class XKLibUtils {
    
    public static String objClassName(Object obj){
        var clazzName = obj.getClass().getName();
        return clazzName.substring(clazzName.lastIndexOf(".")+1);
    }
    
    public static String objName(Object obj){
        var clazzName = obj.getClass().getName();
        return clazzName.substring(clazzName.lastIndexOf(".")+1) +  "@" + Integer.toHexString(System.identityHashCode(obj));
    }
}
