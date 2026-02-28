package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.widget.layout.GridLayout;

import java.util.ArrayList;
import java.util.List;

public class GridParam {
    
    private final List<SizeParam> col;
    private final List<SizeParam> row;
    
    public GridParam(List<SizeParam> col, List<SizeParam> row) {
        this.col = col;
        this.row = row;
    }
    
    public List<SizeParam> getCols() {
        return this.col;
    }
    
    public List<SizeParam> getRows() {
        return this.row;
    }
    
    public int getColCount() {
        return this.col.size();
    }
    
    public int getRowCount() {
        return this.row.size();
    }
    

    
    public static class Builder{
        private final List<SizeParam> col = new ArrayList<>();
        private final List<SizeParam> row = new ArrayList<>();
        
        public Builder addCol(SizeParam param){
            this.col.add(param);
            return this;
        }
        
        public Builder addRow(SizeParam param){
            this.row.add(param);
            return this;
        }
        
        /**
         * @param value px结尾为像素,%结尾为百分比,其他为权重
         */
        public Builder addCol(String value){
            if(value.endsWith("px")){
                this.col.add(new SizeParam.Pixel(Integer.parseInt(value.substring(0, value.length() - 2))));
            }else if(value.endsWith("%")){
                this.col.add(new SizeParam.Percent(Float.parseFloat(value.substring(0, value.length() - 1))));
            }else{
                this.col.add(new SizeParam.Weight(Integer.parseInt(value)));
            }
            return this;
        }
        
        public Builder addRow(String value){
            this.row.add(SizeParam.parse(value));
            return this;
        }
        
        public GridParam build(){
            return new GridParam(this.col, this.row);
        }
        
        public GridLayout newLayout(){
            return new GridLayout(this.build());
        }
    }
    
    @Override
    public String toString() {
        return "GridParam{" +
                "col=" + col +
                ", row=" + row +
                '}';
    }
}
