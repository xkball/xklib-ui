package com.xkball.xklib.ui.layout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public sealed interface SizeParam permits SizeParam.Pixel, SizeParam.Percent, SizeParam.Weight {
    
    Logger LOGGER = LoggerFactory.getLogger(SizeParam.class);
    
    static SizeParam parse(String value){
        SizeParam result;
        try{
            if(value.endsWith("px")){
                result = new SizeParam.Pixel(Integer.parseInt(value.substring(0, value.length() - 2)));
            }else if(value.endsWith("%")){
                result = new SizeParam.Percent(Float.parseFloat(value.substring(0, value.length() - 1)));
            }else{
                result = new SizeParam.Weight(Integer.parseInt(value));
            }
        }catch (NumberFormatException e){
            LOGGER.warn("Invalid size value: {}", value);
            result = new SizeParam.Pixel(0);
        }
        return result;
    }
    
    record Pixel(int value) implements SizeParam {}
    
    record Percent(float value) implements SizeParam {}
    
    record Weight(int value) implements SizeParam {}
}
