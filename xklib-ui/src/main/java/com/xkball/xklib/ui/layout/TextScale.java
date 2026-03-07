package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.render.IFont;

public enum TextScale {
    FIXED{
        @Override
        public float getTextHeight(IFont font, String text, float spaceWidth, float spaceHeight) {
            return Math.min(font.lineHeight(),spaceHeight);
        }
    },
    FIT_TO_MAX{
        @Override
        public float getTextHeight(IFont font, String text, float spaceWidth, float spaceHeight) {
            var w = font.width(text);
            var r = spaceWidth/w;
            return Math.min(font.lineHeight() * r,spaceHeight);
        }
    };
    
    public float getTextHeight(IFont font, String text, float spaceWidth, float spaceHeight) {
        return 0;
    }
}
