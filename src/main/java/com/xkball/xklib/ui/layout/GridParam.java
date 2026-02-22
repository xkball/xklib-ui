package com.xkball.xklib.ui.layout;

import java.util.ArrayList;
import java.util.List;

public class GridParam {
    
    private final List<Param> col;
    private final List<Param> row;
    
    public GridParam(List<Param> col, List<Param> row) {
        this.col = col;
        this.row = row;
    }
    
    public static class Builder{
        private final List<Param> col = new ArrayList<>();
        private final List<Param> row = new ArrayList<>();
        
        public Builder addCol(Param param){
            this.col.add(param);
            return this;
        }
        
        public Builder addRow(Param param){
            this.row.add(param);
            return this;
        }
        
        /**
         * @param value px结尾为像素,%结尾为百分比,其他为权重
         */
        public Builder addCol(String value){
            if(value.endsWith("px")){
                this.col.add(new Pixel(Integer.parseInt(value.substring(0, value.length() - 2))));
            }else if(value.endsWith("%")){
                this.col.add(new Percent(Float.parseFloat(value.substring(0, value.length() - 1))));
            }else{
                this.col.add(new Weight(Integer.parseInt(value)));
            }
            return this;
        }
        
        public Builder addRow(String value){
            if(value.endsWith("px")){
                this.row.add(new Pixel(Integer.parseInt(value.substring(0, value.length() - 2))));
            }else if(value.endsWith("%")){
                this.row.add(new Percent(Float.parseFloat(value.substring(0, value.length() - 1))));
            }else{
                this.row.add(new Weight(Integer.parseInt(value)));
            }
            return this;
        }
        
        public GridParam build(){
            return new GridParam(this.col, this.row);
        }
    }
    
    public sealed interface Param permits Pixel, Percent, Weight {
    }
    
    public record Pixel(int value) implements Param{}
    
    public record Percent(float value) implements Param{}
    
    public record Weight(int value) implements Param{}
}
