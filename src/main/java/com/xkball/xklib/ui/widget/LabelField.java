package com.xkball.xklib.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.render.IFont;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.widget.layout.ScrollableFlexLayout;

import java.util.ArrayList;
import java.util.List;

public class LabelField extends ScrollableFlexLayout {
    
    protected String text;
    protected int lineHeight = 20;
    protected int textColor = 0xFF000000;
    protected List<Label> labels = new ArrayList<>();
    private int lastWidth = 0;
    
    public LabelField() {
        super(new FlexParam.Builder()
                .direction(FlexParam.Direction.COL)
                .justify(FlexParam.Align.START)
                .align(FlexParam.Align.START)
                .overflow(false)
                .build());
        this.yScrollable = true;
    }
    
    public LabelField(String text) {
        this();
        this.text = text;
    }
    
    public LabelField(String text, int lineHeight) {
        this();
        this.text = text;
        this.lineHeight = lineHeight;
    }
    
    public LabelField(String text, int lineHeight, int textColor) {
        this();
        this.text = text;
        this.lineHeight = lineHeight;
        this.textColor = textColor;
    }
    
    public String getText() {
        return this.text;
    }
    
    public LabelField setText(String text) {
        this.text = text;
        this.lastWidth = 0;
        this.markDirty();
        return this;
    }
    
    public int getLineHeight() {
        return this.lineHeight;
    }
    
    public LabelField setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        this.lastWidth = 0;
        this.markDirty();
        return this;
    }
    
    public int getTextColor() {
        return this.textColor;
    }
    
    public LabelField setTextColor(int textColor) {
        this.textColor = textColor;
        for (Label label : this.labels) {
            label.setTextColor(textColor);
        }
        return this;
    }
    
    @Override
    public void resize() {
        super.resize();
        int currentWidth = this.inner.getWidth();
        if (this.text != null && !this.text.isEmpty() && currentWidth > 0 && currentWidth != this.lastWidth) {
            this.lastWidth = currentWidth;
            this.submitTreeUpdate(this::rebuildLabels);
        }
    }
    
    private void rebuildLabels() {
        if (this.text == null || this.text.isEmpty() || this.inner.getWidth() <= 0) {
            return;
        }
        
        this.labels.clear();
        this.inner.getChildren().forEach(this.inner::removeChild);
        
        List<String> lines = wrapText(this.text, this.inner.getWidth(), this.lineHeight);
        
        for (int i = 0; i < lines.size(); i++) {
            Label label = new Label(lines.get(i), this.lineHeight, this.textColor);
            this.labels.add(label);
            this.inner.addChild(label, FlexElementParam.of(i, SizeParam.parse("100%"), new SizeParam.Pixel(this.lineHeight)));
        }
        
        this.inner.markDirty();
        this.markDirty();
    }
    
    private List<String> wrapText(String text, int maxWidth, int lineHeight) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty() || maxWidth <= 0) {
            return lines;
        }
        
        IGUIGraphics graphics = XKLib.gui.getGuiGraphics();
        if (graphics == null) {
            lines.add(text);
            return lines;
        }
        
        IFont font = graphics.defaultFont();
        float scale = lineHeight / (float) font.lineHeight();
        
        String[] paragraphs = text.split("\n");
        
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }
            
            StringBuilder currentLine = new StringBuilder();
            
            for (int i = 0; i < paragraph.length(); i++) {
                char c = paragraph.charAt(i);
                String testLine = currentLine.toString() + c;
                int testWidth = (int) (font.width(testLine) * scale);
                
                if (testWidth > maxWidth && !currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    currentLine.append(c);
                } else {
                    currentLine.append(c);
                }
            }
            
            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toString());
            }
        }
        
        return lines;
    }
}
