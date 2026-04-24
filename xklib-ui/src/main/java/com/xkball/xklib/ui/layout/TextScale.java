package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IFont;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Label;
import dev.vfyjxf.taffy.geometry.FloatSize;
import dev.vfyjxf.taffy.util.MeasureFunc;

import java.util.function.IntSupplier;

public enum TextScale {
    FIXED{
        @Override
        public float getTextHeight(int lineHeight, IFont font, IComponent text, float spaceWidth, float spaceHeight) {
            return lineHeight;
        }
    },
    FIT_TO_MAX{
        @Override
        public float getTextHeight(int lineHeight, IFont font, IComponent text, float spaceWidth, float spaceHeight) {
            var w = font.width(text);
            var r = spaceWidth/w;
            return Math.min(font.lineHeight() * r,spaceHeight);
        }
    },
    EXPAND_WIDTH{
        @Override
        public float getTextHeight(int lineHeight, IFont font, IComponent text, float spaceWidth, float spaceHeight) {
            if(lineHeight > 0) return lineHeight;
            return FIT_TO_MAX.getTextHeight(lineHeight,font, text, spaceWidth, spaceHeight);
        }
        
        @Override
        public MeasureFunc getMeasureFunc(int lineHeight, float extraWidth, IFont font, IComponent text) {
            return (kn,av) -> {
                var h = Float.isNaN(kn.height) ? av.getHeight().getValue() : kn.height;
                if(h == 0) h = font.lineHeight();
                var lh = h-4;
                if(lineHeight != -1) lh = lineHeight;
                var w = font.width(text, (int) lh) + 10 + extraWidth;
                return FloatSize.of(w,h);
            };
        }
    };
    
    public float getTextHeight(int lineHeight, IFont font, IComponent text, float spaceWidth, float spaceHeight) {
        return 0;
    }
    
    public float getTextHeight(int lineHeight, IFont font, String text, float spaceWidth, float spaceHeight) {
        return this.getTextHeight(lineHeight, font, IComponent.literal(text), spaceWidth, spaceHeight);
    }
    
    public MeasureFunc getMeasureFunc(int lineHeight, float extraWidth, IFont font, IComponent text){
        return null;
    }
    
    public MeasureFunc getMeasureFunc(int lineHeight, float extraWidth, IFont font, String text){
        return this.getMeasureFunc(lineHeight, extraWidth, font, IComponent.literal(text));
    }
}
