package com.xkball.xklib.ui.layout;

import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.LengthPercentageAuto;
import dev.vfyjxf.taffy.style.TaffyDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaffySizeParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TaffySizeParser.class);
    
    private final Type type;
    private final float value;
    
    public TaffySizeParser(Type type, float value) {
        this.type = type;
        this.value = value;
    }
    
    public static TaffySizeParser of(String string){
        switch (string) {
            case "auto" -> {
                return new TaffySizeParser(Type.AUTO, 0);
            }
            case "content" -> {
                return new TaffySizeParser(Type.CONTENT, 0);
            }
            case "max-content" -> {
                return new TaffySizeParser(Type.MAX_CONTENT, 0);
            }
            case "min-content" -> {
                return new TaffySizeParser(Type.MIN_CONTENT, 0);
            }
        }
        float v = 0;
        if(string.endsWith("%")){
            try {
                v = Float.parseFloat(string.substring(0, string.length()-1));
            }catch (NumberFormatException e){
                LOGGER.warn("Cannot parse number: {}",string,e);
            }
            return new TaffySizeParser(Type.PERCENTAGE,v);
        }
        else {
            try {
                v = Float.parseFloat(string);
            }catch (NumberFormatException e){
                LOGGER.warn("Cannot parse number: {}",string,e);
            }
            return new TaffySizeParser(Type.LENGTH,v);
        }
    }
    
    public TaffyDimension toDimension(){
        return switch (type){
            case LENGTH -> TaffyDimension.length(value);
            case PERCENTAGE -> TaffyDimension.percent(value/100f);
            case AUTO -> TaffyDimension.AUTO;
            case CONTENT -> TaffyDimension.CONTENT;
            case MAX_CONTENT -> TaffyDimension.MAX_CONTENT;
            case MIN_CONTENT -> TaffyDimension.MIN_CONTENT;
        };
    }
    
    public LengthPercentage toLengthPercentage(){
        if(type == Type.PERCENTAGE){
            return LengthPercentage.percent(this.value/100);
        }
        if(type != Type.LENGTH){
            LOGGER.warn("Not a length or percentage: {}",type);
        }
        return LengthPercentage.length(value);
    }
    
    public LengthPercentageAuto toLengthPercentageAuto(){
        return switch (type){
            case LENGTH -> LengthPercentageAuto.length(this.value);
            case PERCENTAGE -> LengthPercentageAuto.percent(this.value/100f);
            case AUTO -> LengthPercentageAuto.AUTO;
            case MIN_CONTENT, CONTENT -> LengthPercentageAuto.MIN_CONTENT;
            case MAX_CONTENT -> LengthPercentageAuto.MAX_CONTENT;
        };
    }
    
    private enum Type{
        LENGTH,
        PERCENTAGE,
        AUTO,
        MIN_CONTENT,
        MAX_CONTENT,
        CONTENT;
    }
}
