package com.xkball.xklibmc.ui.screen;

import com.xkball.xklib.ui.screen.CSSStyleDisplayScreen;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklibmc.ui.widget.WidgetWrapper;
import net.minecraft.client.gui.components.EditBox;

public class MCCssStyleDisplayScreen extends CSSStyleDisplayScreen {
    
    
    @Override
    protected SearchInput createSearchInput(String cssClassName, SearchTextListener listener) {
        return new SearchInput() {
            
            private WidgetWrapper editBox = WidgetWrapper.editBox("", 100).inlineStyle("""
                    height: 20rpx;
                    flex-shrink: 0;
                    """);
            
            {
                ((EditBox)editBox.getWidget()).setResponder(listener::onSearchTextChanged);
            }
            
            @Override
            public Widget widget() {
                return editBox;
            }
            
            @Override
            public String text() {
                return ((EditBox)editBox.getWidget()).getValue();
            }
        };
    }
}
