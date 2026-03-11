package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.layout.SimpleTextSplitter;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.CalcExpression;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;

public class TextDisplay extends ContainerWidget {
    private final String text;
    private int lastWidth = -1;
    
    public TextDisplay(String text) {
        this.text = text;
    }
    
    @Override
    public void init() {
        super.init();
        this.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.justifyContent = AlignContent.START;
            s.alignItems = AlignItems.STRETCH;
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.calc(CalcExpression.percentMinusLength(1, 30)));
        });
        this.setYScrollEnable(true);
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        super.resize(offsetX, offsetY);
        int currentWidth = (int) this.width;
        if (currentWidth > 0 && currentWidth != lastWidth) {
            lastWidth = currentWidth;
            this.submitTreeUpdate(this::rebuildLines);
        }
    }
    
    private void rebuildLines() {
        this.clearChildren();
        
        var graphics = GuiSystem.INSTANCE.get().getGuiGraphics();
        if (graphics != null && lastWidth > 0) {
            var font = graphics.defaultFont();
            var splitter = new SimpleTextSplitter();
            var lines = splitter.split(font, this.text, lastWidth);
            
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                var lineLabel = new Label(line, TextAlign.LEFT, 0xFFE2E8F0);
                lineLabel.addDecoration(new Background(i % 2 == 0 ? 0xFF475569 : 0xFF3F4A5B));
                lineLabel.setStyle(s -> {
                    s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(32 * (1 / 0.9f)));
                    s.flexShrink = 0;
                });
                this.addChild(lineLabel);
            }
        }
    }
}
