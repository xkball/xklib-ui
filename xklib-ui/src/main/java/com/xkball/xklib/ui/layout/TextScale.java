package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.render.IFont;
import com.xkball.xklib.ui.system.GuiSystem;
import dev.vfyjxf.taffy.geometry.FloatSize;
import dev.vfyjxf.taffy.util.MeasureFunc;

public enum TextScale {
    FIXED{
        @Override
        public float getTextHeight(IFont font, String text, float spaceWidth, float spaceHeight) {
            return Math.min(font.lineHeight(), FIT_TO_MAX.getTextHeight(font, text, spaceWidth, spaceHeight));
        }
    },
    FIT_TO_MAX{
        @Override
        public float getTextHeight(IFont font, String text, float spaceWidth, float spaceHeight) {
            var w = font.width(text);
            var r = spaceWidth/w;
            return Math.min(font.lineHeight() * r,spaceHeight);
        }
    },
    EXPAND_WIDTH{
        @Override
        public float getTextHeight(IFont font, String text, float spaceWidth, float spaceHeight) {
            return FIT_TO_MAX.getTextHeight(font, text, spaceWidth, spaceHeight);
        }
        
        @Override
        public MeasureFunc getMeasureFunc(IFont font, String text) {
            return (kn,av) -> {
                var h = Float.isNaN(kn.height) ? av.getHeight().getValue() : kn.height;
                if(h == 0) h = font.lineHeight();
                var w = font.width(text, (int) h-4);
                return FloatSize.of(w,h);
            };
        }
    };
    
    public float getTextHeight(IFont font, String text, float spaceWidth, float spaceHeight) {
        return 0;
    }
    
    public MeasureFunc getMeasureFunc(IFont font, String text){
        return null;
    }
}
