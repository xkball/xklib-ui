package com.xkball.xklib.ui.layout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public sealed interface SizeParam permits SizeParam.Max, SizeParam.Min, SizeParam.Percent, SizeParam.Pixel, SizeParam.Weight {
    
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
    
    /**
     * @param fullSize 100%的大小
     * @param baseSize 权重为1时的大小
     */
    int calculateSize(int fullSize, int baseSize);
    
    default boolean isWeight() {
        return false;
    }
    
    default int getWeight() {
        return 0;
    }

    final class Max implements SizeParam {
        private final SizeParam a;
        private final SizeParam b;

        public Max(SizeParam a, SizeParam b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int calculateSize(int fullSize, int baseSize) {
            return Math.max(a.calculateSize(fullSize, baseSize), b.calculateSize(fullSize, baseSize));
        }
    }
    
    final class Min implements SizeParam {
        private final SizeParam a;
        private final SizeParam b;

        public Min(SizeParam a, SizeParam b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int calculateSize(int fullSize, int baseSize) {
            return Math.min(a.calculateSize(fullSize, baseSize), b.calculateSize(fullSize, baseSize));
        }
    }
    
    record Pixel(int value) implements SizeParam {
        @Override
        public int calculateSize(int fullSize, int baseSize) {
            return value;
        }
    }
    
    record Percent(float value) implements SizeParam {
        @Override
        public int calculateSize(int fullSize, int baseSize) {
            return (int)(value / 100.0f * fullSize);
        }
    }
    
    record Weight(int value) implements SizeParam {
        @Override
        public int calculateSize(int fullSize, int baseSize) {
            return baseSize * value;
        }
        
        @Override
        public boolean isWeight() {
            return true;
        }
        
        @Override
        public int getWeight() {
            return value;
        }
    }
}
