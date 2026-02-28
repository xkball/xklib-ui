package com.xkball.xklib.ui.widget.widgets;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.render.IFont;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.HorizontalAlign;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.layout.ScrollableFlexLayout;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class TextField extends ScrollableFlexLayout {
    
    protected int lineHeight = 20;
    protected int lineSpacing = 2;
    protected boolean showLineBottom = false;
    protected int lineBottomColor = 0x40000000;
    protected boolean allowXOverflow = true;
    protected boolean autoWrap = false;
    protected int maxLines = -1;
    protected boolean autoGrow = true;
    protected int defaultLineCount = 1;
    protected boolean useMinWidth = false;
    protected boolean useMinHeight = false;
    protected int lastExpectWidth = 0;
    protected int lastExpectHeight = 0;
    
    protected final TextFieldInner innerWidget;
    protected FlexElementParam innerParam;
    
    public TextField() {
        super(new FlexParam.Builder()
                .direction(FlexParam.Direction.COL)
                .justify(FlexParam.Align.START)
                .align(FlexParam.Align.START)
                .overflow(false)
                .build());
        this.innerWidget = new TextFieldInner();
        this.innerParam = FlexElementParam.of(0, SizeParam.parse("100%"), SizeParam.parse("100%"));
        this.inner.addChild(this.innerWidget, this.innerParam);
        this.xScrollable = true;
        this.yScrollable = true;
    }
    
    public TextField setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        this.innerWidget.markDirty();
        this.markDirty();
        return this;
    }
    
    public int getLineHeight() {
        return this.lineHeight;
    }
    
    public TextField setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
        this.innerWidget.markDirty();
        this.markDirty();
        return this;
    }
    
    public int getLineSpacing() {
        return this.lineSpacing;
    }
    
    public TextField setShowLineBottom(boolean showLineBottom) {
        this.showLineBottom = showLineBottom;
        return this;
    }
    
    public boolean isShowLineBottom() {
        return this.showLineBottom;
    }
    
    public TextField setLineBottomColor(int color) {
        this.lineBottomColor = color;
        return this;
    }
    
    public int getLineBottomColor() {
        return this.lineBottomColor;
    }
    
    public TextField setAllowXOverflow(boolean allowXOverflow) {
        this.allowXOverflow = allowXOverflow;
        this.autoWrap = !allowXOverflow;
        this.xScrollable = allowXOverflow;
        this.innerWidget.rebuildWrappedLines();
        this.markDirty();
        return this;
    }
    
    public boolean isAllowXOverflow() {
        return this.allowXOverflow;
    }
    
    public TextField setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        return this;
    }
    
    public int getMaxLines() {
        return this.maxLines;
    }
    
    public TextField setAutoGrow(boolean autoGrow) {
        this.autoGrow = autoGrow;
        return this;
    }
    
    public boolean isAutoGrow() {
        return this.autoGrow;
    }
    
    public TextField setDefaultLineCount(int defaultLineCount) {
        this.defaultLineCount = Math.max(1, defaultLineCount);
        return this;
    }
    
    public int getDefaultLineCount() {
        return this.defaultLineCount;
    }
    
    public TextField setText(String text) {
        this.innerWidget.setText(text);
        return this;
    }
    
    public String getText() {
        return this.innerWidget.getText();
    }
    
    public TextField setTextColor(int color) {
        this.innerWidget.textColor = color;
        return this;
    }
    
    public TextField setCursorColor(int color) {
        this.innerWidget.cursorColor = color;
        return this;
    }
    
    public TextField setSelectionColor(int color) {
        this.innerWidget.selectionColor = color;
        return this;
    }
    
    public TextField setBackgroundColor(int color) {
        this.innerWidget.backgroundColor = color;
        return this;
    }
    
    public TextField setBorderColor(int color) {
        this.innerWidget.borderColor = color;
        return this;
    }
    
    public TextField setFocusedBorderColor(int color) {
        this.innerWidget.focusedBorderColor = color;
        return this;
    }
    
    public TextField setHorizontalAlign(HorizontalAlign align) {
        this.innerWidget.horizontalAlign = align;
        return this;
    }
    
    public TextField setInnerUseMinWidth(boolean useMinWidth) {
        this.useMinWidth = useMinWidth;
        this.updateInnerParam();
        return this;
    }
    
    public TextField setInnerUseMinHeight(boolean useMinHeight) {
        this.useMinHeight = useMinHeight;
        this.updateInnerParam();
        return this;
    }
    
    protected void updateInnerParam() {
        int expectWidth = this.lastExpectWidth > 0 ? this.lastExpectWidth : this.innerWidget.expectWidth();
        int expectHeight = this.lastExpectHeight > 0 ? this.lastExpectHeight : this.innerWidget.expectHeight();
        
        SizeParam widthParam;
        SizeParam heightParam;
        
        if (this.useMinWidth) {
            widthParam = new SizeParam.Pixel(expectWidth);
        } else {
            widthParam = new SizeParam.Max(new SizeParam.Pixel(expectWidth), SizeParam.parse("100%"));
        }
        
        if (this.useMinHeight) {
            heightParam = new SizeParam.Pixel(expectHeight);
        } else {
            heightParam = new SizeParam.Max(new SizeParam.Pixel(expectHeight), SizeParam.parse("100%"));
        }
        
        this.innerParam = FlexElementParam.of(0, widthParam, heightParam);
        this.inner.getChildren().forEach(this.inner::removeChild);
        this.inner.addChild(this.innerWidget, this.innerParam);
        
        this.markDirty();
    }
    
    public TextField setInnerWidthParam(SizeParam widthParam) {
        this.innerParam = FlexElementParam.of(0, widthParam, this.innerParam.height());
        this.inner.getChildren().forEach(this.inner::removeChild);
        this.inner.addChild(this.innerWidget, this.innerParam);
        this.markDirty();
        return this;
    }
    
    public TextField setInnerHeightParam(SizeParam heightParam) {
        this.innerParam = FlexElementParam.of(0, this.innerParam.width(), heightParam);
        this.inner.getChildren().forEach(this.inner::removeChild);
        this.inner.addChild(this.innerWidget, this.innerParam);
        this.markDirty();
        return this;
    }
    
    public int getInnerExpectWidth() {
        return this.innerWidget.expectWidth();
    }
    
    public int getInnerExpectHeight() {
        return this.innerWidget.expectHeight();
    }
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        this.innerWidget.setFocused(focused);
    }
    
    @Override
    public int expectWidth() {
        return 100;
    }
    
    @Override
    public int expectHeight() {
        if (this.autoGrow) {
            return this.defaultLineCount * (this.lineHeight + this.lineSpacing);
        }
        return this.lineHeight * 3;
    }
    
    public class TextFieldInner extends AbstractWidget {
        
        protected String text = "";
        protected List<String> lines = new ArrayList<>();
        protected List<String> wrappedLines = new ArrayList<>();
        protected List<int[]> wrappedLineMapping = new ArrayList<>();
        protected int textColor = 0xFF000000;
        protected int cursorColor = 0xFF000000;
        protected int selectionColor = 0x804080FF;
        protected int backgroundColor = 0xFFFFFFFF;
        protected int borderColor = 0xFF888888;
        protected int focusedBorderColor = 0xFF4080FF;
        protected HorizontalAlign horizontalAlign = HorizontalAlign.LEFT;
        
        protected int cursorLine = 0;
        protected int cursorColumn = 0;
        protected int selectionStartLine = -1;
        protected int selectionStartColumn = -1;
        protected boolean isDragging = false;
        protected long lastBlinkTime = 0;
        protected boolean cursorVisible = true;
        protected static final long CURSOR_BLINK_INTERVAL = 500;
        
        protected static final int UNDO_STACK_SIZE = 32;
        protected final Deque<UndoState> undoStack = new ArrayDeque<>(UNDO_STACK_SIZE);
        
        private int lastWrapWidth = 0;
        
        public TextFieldInner() {
            super();
            this.lines.add("");
        }
        
        public String getText() {
            return this.text;
        }
        
        public void setText(String text) {
            this.text = text != null ? text : "";
            this.rebuildLines();
            this.cursorLine = 0;
            this.cursorColumn = 0;
            this.selectionStartLine = -1;
            this.selectionStartColumn = -1;
            this.undoStack.clear();
            this.updateSize();
            this.markDirty();
            TextField.this.markDirty();
        }
        
        protected void rebuildLines() {
            this.lines.clear();
            if (this.text.isEmpty()) {
                this.lines.add("");
            } else {
                String[] parts = this.text.split("\n", -1);
                for (String part : parts) {
                    this.lines.add(part);
                }
            }
            this.rebuildWrappedLines();
        }
        
        protected void rebuildWrappedLines() {
            this.wrappedLines.clear();
            this.wrappedLineMapping.clear();
            
            if (!TextField.this.autoWrap || this.contentWidth <= 0) {
                for (int i = 0; i < this.lines.size(); i++) {
                    this.wrappedLines.add(this.lines.get(i));
                    this.wrappedLineMapping.add(new int[]{i, 0});
                }
                return;
            }
            
            IGUIGraphics graphics = XKLib.gui.getGuiGraphics();
            if (graphics == null) {
                for (int i = 0; i < this.lines.size(); i++) {
                    this.wrappedLines.add(this.lines.get(i));
                    this.wrappedLineMapping.add(new int[]{i, 0});
                }
                return;
            }
            
            IFont font = graphics.defaultFont();
            float scale = TextField.this.lineHeight / (float) font.lineHeight();
            int maxWidth = this.contentWidth - 8;
            
            for (int lineIndex = 0; lineIndex < this.lines.size(); lineIndex++) {
                String line = this.lines.get(lineIndex);
                if (line.isEmpty()) {
                    this.wrappedLines.add("");
                    this.wrappedLineMapping.add(new int[]{lineIndex, 0});
                    continue;
                }
                
                StringBuilder currentLine = new StringBuilder();
                int startCol = 0;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    String testLine = currentLine.toString() + c;
                    int testWidth = (int) (font.width(testLine) * scale);
                    
                    if (testWidth > maxWidth && !currentLine.isEmpty()) {
                        this.wrappedLines.add(currentLine.toString());
                        this.wrappedLineMapping.add(new int[]{lineIndex, startCol});
                        currentLine = new StringBuilder();
                        startCol = i;
                        currentLine.append(c);
                    } else {
                        currentLine.append(c);
                    }
                }
                if (!currentLine.isEmpty()) {
                    this.wrappedLines.add(currentLine.toString());
                    this.wrappedLineMapping.add(new int[]{lineIndex, startCol});
                }
            }
            if (this.wrappedLines.isEmpty()) {
                this.wrappedLines.add("");
                this.wrappedLineMapping.add(new int[]{0, 0});
            }
        }
        
        protected void rebuildTextFromLines() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.lines.size(); i++) {
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(this.lines.get(i));
            }
            this.text = sb.toString();
            this.rebuildWrappedLines();
            this.updateSize();
        }
        
        protected void updateSize() {
            this.markDirty();
            TextField.this.markDirty();
        }
        
        protected List<String> getDisplayLines() {
            return TextField.this.autoWrap ? this.wrappedLines : this.lines;
        }
        
        @Override
        public int expectWidth() {
            int maxWidth = 0;
            IGUIGraphics graphics = XKLib.gui.getGuiGraphics();
            if (graphics == null) {
                return 100;
            }
            IFont font = graphics.defaultFont();
            float scale = TextField.this.lineHeight / (float) font.lineHeight();
            for (String line : this.getDisplayLines()) {
                int w = (int) (font.width(line) * scale);
                maxWidth = Math.max(maxWidth, w);
            }
            return maxWidth + 8;
        }
        
        @Override
        public int expectHeight() {
            int lineCount = Math.max(this.getDisplayLines().size(), TextField.this.defaultLineCount);
            return lineCount * (TextField.this.lineHeight + TextField.this.lineSpacing) + 8;
        }
        
        @Override
        public void resize() {
            super.resize();
            if (TextField.this.autoWrap && this.contentWidth > 0 && this.contentWidth != this.lastWrapWidth) {
                this.lastWrapWidth = this.contentWidth;
                this.rebuildWrappedLines();
            }
            
            int newExpectWidth = this.expectWidth();
            int newExpectHeight = this.expectHeight();
            
            if (newExpectWidth != TextField.this.lastExpectWidth || newExpectHeight != TextField.this.lastExpectHeight) {
                TextField.this.lastExpectWidth = newExpectWidth;
                TextField.this.lastExpectHeight = newExpectHeight;
                TextField.this.submitTreeUpdate(TextField.this::updateInnerParam);
            }
        }
        
        @Override
        public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            graphics.fill(this.contentX, this.contentY,
                this.contentX + this.contentWidth, this.contentY + this.contentHeight, this.backgroundColor);
            
            int border = this.focused ? this.focusedBorderColor : this.borderColor;
            graphics.renderOutline(this.contentX, this.contentY, this.contentWidth, this.contentHeight, border);
            
            int scissorX1 = Math.max(this.contentX + 2, TextField.this.inner.getContentX());
            int scissorY1 = Math.max(this.contentY + 2, TextField.this.inner.getContentY());
            int scissorX2 = Math.min(this.contentX + this.contentWidth - 2,
                TextField.this.inner.getContentX() + TextField.this.inner.getContentWidth());
            int scissorY2 = Math.min(this.contentY + this.contentHeight - 2,
                TextField.this.inner.getContentY() + TextField.this.inner.getContentHeight());
            
            graphics.enableScissor(scissorX1, scissorY1, scissorX2, scissorY2);
            
            List<String> displayLines = this.getDisplayLines();
            int lineH = TextField.this.lineHeight;
            int spacing = TextField.this.lineSpacing;
            
            for (int i = 0; i < displayLines.size(); i++) {
                String line = displayLines.get(i);
                int lineY = this.contentY + 4 + i * (lineH + spacing);
                int lineX = this.getLineStartX(line);
                
                if (TextField.this.showLineBottom) {
                    int bottomY = lineY + lineH;
                    graphics.hLine(this.contentX + 4, this.contentX + this.contentWidth - 4, bottomY, TextField.this.lineBottomColor);
                }
                
                if (this.hasSelection()) {
                    this.renderLineSelection(graphics, i, line, lineX, lineY, lineH);
                }
                
                if (!line.isEmpty()) {
                    graphics.drawString(line, lineX, lineY, this.textColor, lineH);
                }
            }
            
            if (this.focused && TextField.this.enabled && this.cursorVisible) {
                this.renderCursor(graphics, displayLines, lineH, spacing);
            }
            
            graphics.disableScissor();
            
            if (this.focused && TextField.this.enabled) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - this.lastBlinkTime > CURSOR_BLINK_INTERVAL) {
                    this.cursorVisible = !this.cursorVisible;
                    this.lastBlinkTime = currentTime;
                }
            } else if (!this.focused) {
                this.cursorVisible = false;
            }
            
            super.render(graphics, mouseX, mouseY, a);
        }
        
        protected int getLineStartX(String line) {
            int baseX = this.contentX + 4;
            if (this.horizontalAlign == HorizontalAlign.LEFT) {
                return baseX;
            }
            
            int lineWidth = this.getTextWidth(line);
            int availableWidth = this.contentWidth - 8;
            
            return switch (this.horizontalAlign) {
                case CENTER -> baseX + (availableWidth - lineWidth) / 2;
                case RIGHT -> baseX + availableWidth - lineWidth;
                default -> baseX;
            };
        }
        
        protected void renderLineSelection(IGUIGraphics graphics, int displayLineIndex, String line, int lineX, int lineY, int lineH) {
            if (!this.hasSelection()) return;
            
            int selStartLine, selStartCol, selEndLine, selEndCol;
            if (this.selectionStartLine < this.cursorLine ||
                (this.selectionStartLine == this.cursorLine && this.selectionStartColumn < this.cursorColumn)) {
                selStartLine = this.selectionStartLine;
                selStartCol = this.selectionStartColumn;
                selEndLine = this.cursorLine;
                selEndCol = this.cursorColumn;
            } else {
                selStartLine = this.cursorLine;
                selStartCol = this.cursorColumn;
                selEndLine = this.selectionStartLine;
                selEndCol = this.selectionStartColumn;
            }
            
            if (TextField.this.autoWrap && !this.wrappedLineMapping.isEmpty()) {
                int[] mapping = this.wrappedLineMapping.get(displayLineIndex);
                int origLine = mapping[0];
                int origStartCol = mapping[1];
                int origEndCol = origStartCol + line.length();
                
                if (origLine < selStartLine || origLine > selEndLine) {
                    return;
                }
                
                int renderStartCol = 0;
                int renderEndCol = line.length();
                
                if (origLine == selStartLine) {
                    renderStartCol = Math.max(0, selStartCol - origStartCol);
                }
                if (origLine == selEndLine) {
                    renderEndCol = Math.min(line.length(), selEndCol - origStartCol);
                }
                
                if (origLine > selStartLine && origLine < selEndLine) {
                    renderStartCol = 0;
                    renderEndCol = line.length();
                } else if (origLine == selStartLine && origLine == selEndLine) {
                    if (selStartCol > origEndCol || selEndCol < origStartCol) {
                        return;
                    }
                } else if (origLine == selStartLine) {
                    if (selStartCol > origEndCol) return;
                } else if (origLine == selEndLine) {
                    if (selEndCol < origStartCol) return;
                }
                
                renderStartCol = Math.max(0, Math.min(renderStartCol, line.length()));
                renderEndCol = Math.max(0, Math.min(renderEndCol, line.length()));
                
                if (renderStartCol < renderEndCol) {
                    int selStartX = lineX + this.getTextWidth(line.substring(0, renderStartCol));
                    int selEndX = lineX + this.getTextWidth(line.substring(0, renderEndCol));
                    graphics.fill(selStartX, lineY, selEndX, lineY + lineH, this.selectionColor);
                }
                return;
            }
            
            if (displayLineIndex < selStartLine || displayLineIndex > selEndLine) {
                return;
            }
            
            int startCol = (displayLineIndex == selStartLine) ? selStartCol : 0;
            int endCol = (displayLineIndex == selEndLine) ? selEndCol : line.length();
            
            startCol = Math.min(startCol, line.length());
            endCol = Math.min(endCol, line.length());
            
            if (startCol < endCol) {
                int selStartX = lineX + this.getTextWidth(line.substring(0, startCol));
                int selEndX = lineX + this.getTextWidth(line.substring(0, endCol));
                graphics.fill(selStartX, lineY, selEndX, lineY + lineH, this.selectionColor);
            }
        }
        
        protected void renderCursor(IGUIGraphics graphics, List<String> displayLines, int lineH, int spacing) {
            if (TextField.this.autoWrap && !this.wrappedLineMapping.isEmpty()) {
                for (int i = 0; i < this.wrappedLineMapping.size(); i++) {
                    int[] mapping = this.wrappedLineMapping.get(i);
                    int origLine = mapping[0];
                    int origStartCol = mapping[1];
                    int lineLength = displayLines.get(i).length();
                    
                    if (origLine == this.cursorLine &&
                        this.cursorColumn >= origStartCol &&
                        this.cursorColumn <= origStartCol + lineLength) {
                        int col = this.cursorColumn - origStartCol;
                        String displayText = displayLines.get(i);
                        col = Math.min(col, displayText.length());
                        
                        int cursorY = this.contentY + 4 + i * (lineH + spacing);
                        int lineX = this.getLineStartX(displayText);
                        int cursorX = lineX + this.getTextWidth(displayText.substring(0, col));
                        
                        graphics.fill(cursorX, cursorY + 2, cursorX + 1, cursorY + lineH - 2, this.cursorColor);
                        return;
                    }
                }
                return;
            }
            
            int cursorLineIndex = Math.min(this.cursorLine, displayLines.size() - 1);
            String cursorLineText = cursorLineIndex >= 0 && cursorLineIndex < displayLines.size()
                ? displayLines.get(cursorLineIndex) : "";
            int col = Math.min(this.cursorColumn, cursorLineText.length());
            
            int cursorY = this.contentY + 4 + cursorLineIndex * (lineH + spacing);
            int lineX = this.getLineStartX(cursorLineText);
            int cursorX = lineX + this.getTextWidth(cursorLineText.substring(0, col));
            
            graphics.fill(cursorX, cursorY + 2, cursorX + 1, cursorY + lineH - 2, this.cursorColor);
        }
        
        @Override
        public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (!this.visible) {
                return false;
            }
            
            if (this.isMouseOver(event.x(), event.y())) {
                if (this.shouldTakeFocusAfterInteraction()) {
                    TextField.this.setFocused(true);
                }
                return this.onMouseClicked(event, doubleClick);
            }
            return false;
        }
        
        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            this.cursorVisible = true;
            this.lastBlinkTime = System.currentTimeMillis();
            
            int[] pos = this.getPositionFromMouse(event.x(), event.y());
            
            if (doubleClick) {
                this.selectWord(pos[0], pos[1]);
            } else {
                if (event.hasShiftDown()) {
                    if (this.selectionStartLine < 0) {
                        this.selectionStartLine = this.cursorLine;
                        this.selectionStartColumn = this.cursorColumn;
                    }
                } else {
                    this.selectionStartLine = pos[0];
                    this.selectionStartColumn = pos[1];
                }
                this.cursorLine = pos[0];
                this.cursorColumn = pos[1];
                this.isDragging = true;
            }
            
            this.ensureCursorVisible();
            return true;
        }
        
        @Override
        public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
            if (!this.visible) {
                return false;
            }
            if (this.isDragging) {
                return this.onMouseDragged(event, dx, dy);
            }
            return false;
        }
        
        @Override
        protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
            if (this.isDragging) {
                if (this.selectionStartLine < 0) {
                    this.selectionStartLine = this.cursorLine;
                    this.selectionStartColumn = this.cursorColumn;
                }
                int[] pos = this.getPositionFromMouse(event.x(), event.y());
                this.cursorLine = pos[0];
                this.cursorColumn = pos[1];
                this.ensureCursorVisible();
                return true;
            }
            return false;
        }
        
        @Override
        public boolean mouseReleased(IMouseButtonEvent event) {
            if (!this.visible) {
                return false;
            }
            if (this.isDragging) {
                this.isDragging = false;
                return true;
            }
            return false;
        }
        
        @Override
        public boolean keyPressed(IKeyEvent event) {
            if (!this.visible || !this.focused) {
                return false;
            }
            return this.onKeyPressed(event);
        }
        
        @Override
        protected boolean onKeyPressed(IKeyEvent event) {
            this.cursorVisible = true;
            this.lastBlinkTime = System.currentTimeMillis();
            
            if (event.isSelectAll()) {
                this.selectAll();
                return true;
            }
            
            if (event.isCopy()) {
                this.copy();
                return true;
            }
            
            if (!TextField.this.enabled) {
                return false;
            }
            
            if (event.isPaste()) {
                this.paste();
                return true;
            }
            
            if (event.isCut()) {
                this.cut();
                return true;
            }
            
            if (this.isUndo(event)) {
                this.undo();
                return true;
            }
            
            if (event.isLeft()) {
                this.moveCursorLeft(event.hasShiftDown(), event.hasControlDownWithQuirk());
                return true;
            }
            
            if (event.isRight()) {
                this.moveCursorRight(event.hasShiftDown(), event.hasControlDownWithQuirk());
                return true;
            }
            
            if (event.isUp()) {
                this.moveCursorUp(event.hasShiftDown());
                return true;
            }
            
            if (event.isDown()) {
                this.moveCursorDown(event.hasShiftDown());
                return true;
            }
            
            if (event.key() == GLFW.GLFW_KEY_HOME) {
                this.moveCursorToLineStart(event.hasShiftDown(), event.hasControlDownWithQuirk());
                return true;
            }
            
            if (event.key() == GLFW.GLFW_KEY_END) {
                this.moveCursorToLineEnd(event.hasShiftDown(), event.hasControlDownWithQuirk());
                return true;
            }
            
            if (event.key() == GLFW.GLFW_KEY_BACKSPACE) {
                this.deleteBackward(event.hasControlDownWithQuirk());
                return true;
            }
            
            if (event.key() == GLFW.GLFW_KEY_DELETE) {
                this.deleteForward(event.hasControlDownWithQuirk());
                return true;
            }
            
            if (event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
                this.insertNewLine();
                return true;
            }
            
            return false;
        }
        
        @Override
        protected boolean onCharTyped(ICharEvent event) {
            if (!TextField.this.enabled) {
                return false;
            }
            
            this.cursorVisible = true;
            this.lastBlinkTime = System.currentTimeMillis();
            
            String charStr = event.codepointAsString();
            if (charStr != null && !charStr.isEmpty()) {
                char c = charStr.charAt(0);
                if (c < 32) {
                    return false;
                }
                this.insertText(charStr);
                return true;
            }
            return false;
        }
        
        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (focused) {
                this.cursorVisible = true;
                this.lastBlinkTime = System.currentTimeMillis();
            } else {
                this.isDragging = false;
                this.selectionStartLine = -1;
                this.selectionStartColumn = -1;
                this.undoStack.clear();
            }
        }
        
        protected void insertNewLine() {
            if (TextField.this.maxLines > 0 && this.lines.size() >= TextField.this.maxLines) {
                return;
            }
            
            this.pushUndoState();
            
            if (this.hasSelection()) {
                this.deleteSelection();
            }
            
            String currentLine = this.lines.get(this.cursorLine);
            String before = currentLine.substring(0, this.cursorColumn);
            String after = currentLine.substring(this.cursorColumn);
            
            this.lines.set(this.cursorLine, before);
            this.lines.add(this.cursorLine + 1, after);
            
            this.cursorLine++;
            this.cursorColumn = 0;
            this.selectionStartLine = -1;
            this.selectionStartColumn = -1;
            
            this.rebuildTextFromLines();
            this.ensureCursorVisible();
        }
        
        protected void insertText(String str) {
            if (str.contains("\n")) {
                String[] parts = str.split("\n", -1);
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0) {
                        if (TextField.this.maxLines > 0 && this.lines.size() >= TextField.this.maxLines) {
                            break;
                        }
                        this.insertNewLine();
                    }
                    if (!parts[i].isEmpty()) {
                        this.insertTextSingleLine(parts[i]);
                    }
                }
            } else {
                this.insertTextSingleLine(str);
            }
        }
        
        protected void insertTextSingleLine(String str) {
            this.pushUndoState();
            
            if (this.hasSelection()) {
                this.deleteSelection();
            }
            
            String currentLine = this.lines.get(this.cursorLine);
            String before = currentLine.substring(0, this.cursorColumn);
            String after = currentLine.substring(this.cursorColumn);
            this.lines.set(this.cursorLine, before + str + after);
            this.cursorColumn += str.length();
            this.selectionStartLine = -1;
            this.selectionStartColumn = -1;
            
            this.rebuildTextFromLines();
            this.ensureCursorVisible();
        }
        
        protected void deleteSelection() {
            if (!this.hasSelection()) {
                return;
            }
            
            int startLine, startCol, endLine, endCol;
            if (this.selectionStartLine < this.cursorLine ||
                (this.selectionStartLine == this.cursorLine && this.selectionStartColumn < this.cursorColumn)) {
                startLine = this.selectionStartLine;
                startCol = this.selectionStartColumn;
                endLine = this.cursorLine;
                endCol = this.cursorColumn;
            } else {
                startLine = this.cursorLine;
                startCol = this.cursorColumn;
                endLine = this.selectionStartLine;
                endCol = this.selectionStartColumn;
            }
            
            if (startLine == endLine) {
                String line = this.lines.get(startLine);
                this.lines.set(startLine, line.substring(0, startCol) + line.substring(endCol));
            } else {
                String startLineText = this.lines.get(startLine);
                String endLineText = this.lines.get(endLine);
                this.lines.set(startLine, startLineText.substring(0, startCol) + endLineText.substring(endCol));
                
                for (int i = endLine; i > startLine; i--) {
                    this.lines.remove(i);
                }
            }
            
            this.cursorLine = startLine;
            this.cursorColumn = startCol;
            this.selectionStartLine = -1;
            this.selectionStartColumn = -1;
            
            this.rebuildTextFromLines();
        }
        
        protected void deleteBackward(boolean wholeWord) {
            if (this.hasSelection()) {
                this.pushUndoState();
                this.deleteSelection();
            } else if (this.cursorColumn > 0) {
                this.pushUndoState();
                String line = this.lines.get(this.cursorLine);
                int deletePos = wholeWord ? this.findWordStart(line, this.cursorColumn) : this.cursorColumn - 1;
                this.lines.set(this.cursorLine, line.substring(0, deletePos) + line.substring(this.cursorColumn));
                this.cursorColumn = deletePos;
                this.rebuildTextFromLines();
            } else if (this.cursorLine > 0) {
                this.pushUndoState();
                String currentLine = this.lines.get(this.cursorLine);
                String prevLine = this.lines.get(this.cursorLine - 1);
                this.cursorColumn = prevLine.length();
                this.lines.set(this.cursorLine - 1, prevLine + currentLine);
                this.lines.remove(this.cursorLine);
                this.cursorLine--;
                this.rebuildTextFromLines();
            }
            this.ensureCursorVisible();
        }
        
        protected void deleteForward(boolean wholeWord) {
            if (this.hasSelection()) {
                this.pushUndoState();
                this.deleteSelection();
            } else {
                String line = this.lines.get(this.cursorLine);
                if (this.cursorColumn < line.length()) {
                    this.pushUndoState();
                    int deletePos = wholeWord ? this.findWordEnd(line, this.cursorColumn) : this.cursorColumn + 1;
                    this.lines.set(this.cursorLine, line.substring(0, this.cursorColumn) + line.substring(deletePos));
                    this.rebuildTextFromLines();
                } else if (this.cursorLine < this.lines.size() - 1) {
                    this.pushUndoState();
                    String nextLine = this.lines.get(this.cursorLine + 1);
                    this.lines.set(this.cursorLine, line + nextLine);
                    this.lines.remove(this.cursorLine + 1);
                    this.rebuildTextFromLines();
                }
            }
            this.ensureCursorVisible();
        }
        
        protected void moveCursorLeft(boolean selecting, boolean wholeWord) {
            if (selecting && this.selectionStartLine < 0) {
                this.selectionStartLine = this.cursorLine;
                this.selectionStartColumn = this.cursorColumn;
            } else if (!selecting) {
                this.selectionStartLine = -1;
                this.selectionStartColumn = -1;
            }
            
            if (wholeWord) {
                String line = this.lines.get(this.cursorLine);
                this.cursorColumn = this.findWordStart(line, this.cursorColumn);
            } else if (this.cursorColumn > 0) {
                this.cursorColumn--;
            } else if (this.cursorLine > 0) {
                this.cursorLine--;
                this.cursorColumn = this.lines.get(this.cursorLine).length();
            }
            
            this.ensureCursorVisible();
        }
        
        protected void moveCursorRight(boolean selecting, boolean wholeWord) {
            if (selecting && this.selectionStartLine < 0) {
                this.selectionStartLine = this.cursorLine;
                this.selectionStartColumn = this.cursorColumn;
            } else if (!selecting) {
                this.selectionStartLine = -1;
                this.selectionStartColumn = -1;
            }
            
            String line = this.lines.get(this.cursorLine);
            if (wholeWord) {
                this.cursorColumn = this.findWordEnd(line, this.cursorColumn);
            } else if (this.cursorColumn < line.length()) {
                this.cursorColumn++;
            } else if (this.cursorLine < this.lines.size() - 1) {
                this.cursorLine++;
                this.cursorColumn = 0;
            }
            
            this.ensureCursorVisible();
        }
        
        protected void moveCursorUp(boolean selecting) {
            if (selecting && this.selectionStartLine < 0) {
                this.selectionStartLine = this.cursorLine;
                this.selectionStartColumn = this.cursorColumn;
            } else if (!selecting) {
                this.selectionStartLine = -1;
                this.selectionStartColumn = -1;
            }
            
            if (this.cursorLine > 0) {
                this.cursorLine--;
                this.cursorColumn = Math.min(this.cursorColumn, this.lines.get(this.cursorLine).length());
            } else {
                this.cursorColumn = 0;
            }
            
            this.ensureCursorVisible();
        }
        
        protected void moveCursorDown(boolean selecting) {
            if (selecting && this.selectionStartLine < 0) {
                this.selectionStartLine = this.cursorLine;
                this.selectionStartColumn = this.cursorColumn;
            } else if (!selecting) {
                this.selectionStartLine = -1;
                this.selectionStartColumn = -1;
            }
            
            if (this.cursorLine < this.lines.size() - 1) {
                this.cursorLine++;
                this.cursorColumn = Math.min(this.cursorColumn, this.lines.get(this.cursorLine).length());
            } else {
                this.cursorColumn = this.lines.get(this.cursorLine).length();
            }
            
            this.ensureCursorVisible();
        }
        
        protected void moveCursorToLineStart(boolean selecting, boolean toDocStart) {
            if (selecting && this.selectionStartLine < 0) {
                this.selectionStartLine = this.cursorLine;
                this.selectionStartColumn = this.cursorColumn;
            } else if (!selecting) {
                this.selectionStartLine = -1;
                this.selectionStartColumn = -1;
            }
            
            if (toDocStart) {
                this.cursorLine = 0;
            }
            this.cursorColumn = 0;
            
            this.ensureCursorVisible();
        }
        
        protected void moveCursorToLineEnd(boolean selecting, boolean toDocEnd) {
            if (selecting && this.selectionStartLine < 0) {
                this.selectionStartLine = this.cursorLine;
                this.selectionStartColumn = this.cursorColumn;
            } else if (!selecting) {
                this.selectionStartLine = -1;
                this.selectionStartColumn = -1;
            }
            
            if (toDocEnd) {
                this.cursorLine = this.lines.size() - 1;
            }
            this.cursorColumn = this.lines.get(this.cursorLine).length();
            
            this.ensureCursorVisible();
        }
        
        protected void selectAll() {
            this.selectionStartLine = 0;
            this.selectionStartColumn = 0;
            this.cursorLine = this.lines.size() - 1;
            this.cursorColumn = this.lines.get(this.cursorLine).length();
            this.ensureCursorVisible();
        }
        
        protected void selectWord(int line, int column) {
            String lineText = this.lines.get(line);
            this.selectionStartLine = line;
            this.selectionStartColumn = this.findWordStart(lineText, column);
            this.cursorLine = line;
            this.cursorColumn = this.findWordEnd(lineText, column);
            this.ensureCursorVisible();
        }
        
        protected void copy() {
            if (this.hasSelection()) {
                String selected = this.getSelectedText();
                GLFW.glfwSetClipboardString(0, selected);
            }
        }
        
        protected void paste() {
            String clipboard = GLFW.glfwGetClipboardString(0);
            if (clipboard != null && !clipboard.isEmpty()) {
                clipboard = clipboard.replace("\r\n", "\n").replace("\r", "\n");
                this.insertText(clipboard);
            }
        }
        
        protected void cut() {
            if (this.hasSelection()) {
                this.pushUndoState();
                this.copy();
                this.deleteSelection();
                this.ensureCursorVisible();
            }
        }
        
        protected boolean isUndo(IKeyEvent event) {
            return event.key() == GLFW.GLFW_KEY_Z && event.hasControlDownWithQuirk() && !event.hasShiftDown() && !event.hasAltDown();
        }
        
        protected void pushUndoState() {
            if (this.undoStack.size() >= UNDO_STACK_SIZE) {
                this.undoStack.removeLast();
            }
            this.undoStack.push(new UndoState(
                new ArrayList<>(this.lines),
                this.cursorLine, this.cursorColumn,
                this.selectionStartLine, this.selectionStartColumn));
        }
        
        protected void undo() {
            if (this.undoStack.isEmpty()) {
                return;
            }
            
            UndoState state = this.undoStack.pop();
            this.lines = new ArrayList<>(state.lines);
            this.cursorLine = state.cursorLine;
            this.cursorColumn = state.cursorColumn;
            this.selectionStartLine = state.selectionStartLine;
            this.selectionStartColumn = state.selectionStartColumn;
            this.rebuildTextFromLines();
            this.ensureCursorVisible();
        }
        
        protected record UndoState(List<String> lines, int cursorLine, int cursorColumn,
                                   int selectionStartLine, int selectionStartColumn) {}
        
        protected boolean hasSelection() {
            return this.selectionStartLine >= 0 &&
                   (this.selectionStartLine != this.cursorLine || this.selectionStartColumn != this.cursorColumn);
        }
        
        protected String getSelectedText() {
            if (!this.hasSelection()) {
                return "";
            }
            
            int startLine, startCol, endLine, endCol;
            if (this.selectionStartLine < this.cursorLine ||
                (this.selectionStartLine == this.cursorLine && this.selectionStartColumn < this.cursorColumn)) {
                startLine = this.selectionStartLine;
                startCol = this.selectionStartColumn;
                endLine = this.cursorLine;
                endCol = this.cursorColumn;
            } else {
                startLine = this.cursorLine;
                startCol = this.cursorColumn;
                endLine = this.selectionStartLine;
                endCol = this.selectionStartColumn;
            }
            
            if (startLine == endLine) {
                return this.lines.get(startLine).substring(startCol, endCol);
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(this.lines.get(startLine).substring(startCol));
            for (int i = startLine + 1; i < endLine; i++) {
                sb.append("\n").append(this.lines.get(i));
            }
            sb.append("\n").append(this.lines.get(endLine).substring(0, endCol));
            return sb.toString();
        }
        
        protected int findWordStart(String line, int position) {
            if (position <= 0) {
                return 0;
            }
            
            int pos = position - 1;
            while (pos > 0 && Character.isWhitespace(line.charAt(pos))) {
                pos--;
            }
            while (pos > 0 && !Character.isWhitespace(line.charAt(pos - 1))) {
                pos--;
            }
            return pos;
        }
        
        protected int findWordEnd(String line, int position) {
            if (position >= line.length()) {
                return line.length();
            }
            
            int pos = position;
            while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) {
                pos++;
            }
            while (pos < line.length() && !Character.isWhitespace(line.charAt(pos))) {
                pos++;
            }
            return pos;
        }
        
        protected int[] getPositionFromMouse(double mouseX, double mouseY) {
            int lineH = TextField.this.lineHeight;
            int spacing = TextField.this.lineSpacing;
            
            int relativeY = (int) mouseY - this.contentY - 4;
            List<String> displayLines = this.getDisplayLines();
            int displayLineIndex = Math.max(0, Math.min(relativeY / (lineH + spacing), displayLines.size() - 1));
            
            if (TextField.this.autoWrap && !this.wrappedLineMapping.isEmpty()) {
                int[] mapping = this.wrappedLineMapping.get(displayLineIndex);
                int originalLine = mapping[0];
                int startCol = mapping[1];
                
                String displayText = displayLines.get(displayLineIndex);
                int lineX = this.getLineStartX(displayText);
                int relativeX = (int) mouseX - lineX;
                
                if (relativeX <= 0) {
                    return new int[]{originalLine, startCol};
                }
                
                IGUIGraphics graphics = XKLib.gui.getGuiGraphics();
                if (graphics == null) {
                    return new int[]{originalLine, startCol + displayText.length()};
                }
                
                IFont font = graphics.defaultFont();
                float scale = TextField.this.lineHeight / (float) font.lineHeight();
                
                for (int i = 0; i <= displayText.length(); i++) {
                    int charWidth = (int) (font.width(displayText.substring(0, i)) * scale);
                    if (charWidth >= relativeX) {
                        if (i > 0) {
                            int prevWidth = (int) (font.width(displayText.substring(0, i - 1)) * scale);
                            if (relativeX - prevWidth < charWidth - relativeX) {
                                return new int[]{originalLine, startCol + i - 1};
                            }
                        }
                        return new int[]{originalLine, startCol + i};
                    }
                }
                return new int[]{originalLine, startCol + displayText.length()};
            }
            
            int line = Math.max(0, Math.min(displayLineIndex, this.lines.size() - 1));
            String lineText = this.lines.get(line);
            int lineX = this.getLineStartX(lineText);
            int relativeX = (int) mouseX - lineX;
            
            if (relativeX <= 0) {
                return new int[]{line, 0};
            }
            
            IGUIGraphics graphics = XKLib.gui.getGuiGraphics();
            if (graphics == null) {
                return new int[]{line, lineText.length()};
            }
            
            IFont font = graphics.defaultFont();
            float scale = TextField.this.lineHeight / (float) font.lineHeight();
            
            for (int i = 0; i <= lineText.length(); i++) {
                int charWidth = (int) (font.width(lineText.substring(0, i)) * scale);
                if (charWidth >= relativeX) {
                    if (i > 0) {
                        int prevWidth = (int) (font.width(lineText.substring(0, i - 1)) * scale);
                        if (relativeX - prevWidth < charWidth - relativeX) {
                            return new int[]{line, i - 1};
                        }
                    }
                    return new int[]{line, i};
                }
            }
            return new int[]{line, lineText.length()};
        }
        
        protected void ensureCursorVisible() {
            int lineH = TextField.this.lineHeight;
            int spacing = TextField.this.lineSpacing;
            
            int displayLineIndex = this.cursorLine;
            if (TextField.this.autoWrap && !this.wrappedLineMapping.isEmpty()) {
                for (int i = 0; i < this.wrappedLineMapping.size(); i++) {
                    int[] mapping = this.wrappedLineMapping.get(i);
                    int origLine = mapping[0];
                    int origStartCol = mapping[1];
                    int lineLength = this.wrappedLines.get(i).length();
                    
                    if (origLine == this.cursorLine &&
                        this.cursorColumn >= origStartCol &&
                        this.cursorColumn <= origStartCol + lineLength) {
                        displayLineIndex = i;
                        break;
                    }
                }
            }
            
            int cursorY = displayLineIndex * (lineH + spacing);
            int viewHeight = TextField.this.inner.getHeight();
            int currentOffsetY = -TextField.this.inner.offsetY;
            
            if (cursorY < currentOffsetY) {
                TextField.this.inner.setOffsetY(-cursorY);
            } else if (cursorY + lineH > currentOffsetY + viewHeight) {
                TextField.this.inner.setOffsetY(-(cursorY + lineH - viewHeight));
            }
            
            if (TextField.this.allowXOverflow) {
                String line = this.lines.get(this.cursorLine);
                int cursorX = this.getTextWidth(line.substring(0, Math.min(this.cursorColumn, line.length())));
                int viewWidth = TextField.this.inner.getWidth();
                int currentOffsetX = -TextField.this.inner.offsetX;
                
                if (cursorX < currentOffsetX) {
                    TextField.this.inner.setOffsetX(-cursorX);
                } else if (cursorX + 8 > currentOffsetX + viewWidth) {
                    TextField.this.inner.setOffsetX(-(cursorX + 8 - viewWidth));
                }
            }
        }
        
        protected int getTextWidth(String text) {
            if (text == null || text.isEmpty()) {
                return 0;
            }
            
            IGUIGraphics graphics = XKLib.gui.getGuiGraphics();
            if (graphics == null) {
                return 0;
            }
            
            IFont font = graphics.defaultFont();
            float scale = TextField.this.lineHeight / (float) font.lineHeight();
            return (int) (font.width(text) * scale);
        }
    }
}
