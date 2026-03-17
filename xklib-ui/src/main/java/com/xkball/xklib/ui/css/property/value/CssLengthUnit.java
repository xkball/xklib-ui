package com.xkball.xklib.ui.css.property.value;

import dev.vfyjxf.taffy.style.CalcExpression;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.LengthPercentageAuto;
import dev.vfyjxf.taffy.style.TaffyDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CssLengthUnit implements CalcExpression {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CssLengthUnit.class);
    private final Type type;
    private final float value;
    private final CalcExpression expr;
    
    public CssLengthUnit(Type type, float value) {
        this.type = type;
        this.value = value;
        this.expr = null;
    }
    
    public CssLengthUnit(CalcExpression expr){
        this.type = Type.EXPR;
        this.value = 0;
        this.expr = expr;
    }
    
    public static CssLengthUnit of(String string){
        switch (string) {
            case "auto" -> {
                return new CssLengthUnit(Type.AUTO, 0);
            }
            case "content" -> {
                return new CssLengthUnit(Type.CONTENT, 0);
            }
            case "max-content" -> {
                return new CssLengthUnit(Type.MAX_CONTENT, 0);
            }
            case "min-content" -> {
                return new CssLengthUnit(Type.MIN_CONTENT, 0);
            }
        }
        float v = 0;
        if(string.endsWith("%")){
            try {
                v = Float.parseFloat(string.substring(0, string.length()-1));
            }catch (NumberFormatException e){
                LOGGER.warn("Cannot parse number: {}",string,e);
                throw e;
            }
            return new CssLengthUnit(Type.PERCENTAGE,v);
        }
        else if(string.endsWith("em")){
            try {
                v = Float.parseFloat(string.substring(0, string.length()-2));
            }catch (NumberFormatException e){
                LOGGER.warn("Cannot parse number: {}",string,e);
                throw e;
            }
            return new CssLengthUnit(Type.LENGTH,v);
        }
        else {
            try {
                v = Float.parseFloat(string);
            }catch (NumberFormatException e){
                LOGGER.warn("Cannot parse number: {}",string,e);
                throw e;
            }
            return new CssLengthUnit(Type.LENGTH,v);
        }
    }
    
    public TaffyDimension toDimension(){
        return switch (type){
            case LENGTH -> TaffyDimension.length(value);
            case PERCENTAGE -> TaffyDimension.percent(value/100f);
            case EM -> TaffyDimension.length(value * 16f);
            case AUTO -> TaffyDimension.AUTO;
            case CONTENT -> TaffyDimension.CONTENT;
            case MAX_CONTENT -> TaffyDimension.MAX_CONTENT;
            case MIN_CONTENT -> TaffyDimension.MIN_CONTENT;
            case EXPR -> TaffyDimension.calc(expr);
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
            case EM -> LengthPercentageAuto.length(value * 16f);
            case AUTO -> LengthPercentageAuto.AUTO;
            case MIN_CONTENT, CONTENT -> LengthPercentageAuto.MIN_CONTENT;
            case MAX_CONTENT -> LengthPercentageAuto.MAX_CONTENT;
            case EXPR -> LengthPercentageAuto.calc(expr);
        };
    }
    
    @Override
    public String toString() {
        return switch (type){
            case LENGTH -> value + "px";
            case PERCENTAGE -> value + "%";
            case EM -> value + "em";
            case AUTO -> "auto";
            case MIN_CONTENT -> "min_content";
            case MAX_CONTENT -> "max_content";
            case CONTENT -> "content";
            case EXPR -> Objects.requireNonNull(expr).toString();
        };
    }
    
    @Override
    public float resolve(float basis) {
        return switch (type){
            case LENGTH -> this.value;
            case PERCENTAGE -> basis * this.value/100f;
            case EM -> this.value * 16f;
            case AUTO, CONTENT, MIN_CONTENT, MAX_CONTENT -> basis;
            case EXPR -> Objects.requireNonNull(this.expr).resolve(basis);
        };
    }
    
    public enum Type{
        LENGTH,
        PERCENTAGE,
        EM,
        AUTO,
        MIN_CONTENT,
        MAX_CONTENT,
        CONTENT,
        EXPR;
    }
    
    public static class Combination implements CalcExpression{
        
        public final CssLengthUnit left;
        public final CssLengthUnit right;
        public final boolean add;
        
        public Combination(CssLengthUnit left, CssLengthUnit right, boolean add) {
            this.left = left;
            this.right = right;
            this.add = add;
        }
        
        @Override
        public float resolve(float basis) {
            return left.resolve(basis) + right.resolve(basis) * (add ? 1 : -1);
        }
        
        @Override
        public String toString() {
            return left + (add ? "+" : "-") + right;
        }
    }
}
