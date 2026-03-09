package com.xkball.xklib.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.layout.SimpleTextSplitter;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.render.IFont;
import com.xkball.xklib.ui.system.GuiSystem;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextEdit extends Widget {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TextEdit.class);
    
    protected boolean multiLine = false;
    protected boolean wrapLine = true;
    protected boolean allowEdit = true;
    protected float lineHeight;
    protected float lineGap = 2;
    protected TextAlign textAlign = TextAlign.LEFT;
    
    protected StringBuilder text;
    protected List<String> displayLines = new ArrayList<>();
    protected int cursorLine = 0;
    protected int cursorColumn = 0;
    protected int selectionStartLine = -1;
    protected int selectionStartColumn = -1;
    protected boolean draggingSelection = false;
    protected long lastBlinkTime = 0;
    protected boolean cursorVisible = true;
    protected int lastWidth = -1;
    protected float rw,rh;
    
    public TextEdit() {
        this("");
    }
    
    public TextEdit(String initialText) {
        this.text = new StringBuilder(initialText);
        this.displayLines.add(initialText);
        this.lineHeight = GuiSystem.INSTANCE.get().getGuiGraphics().defaultFont().lineHeight();
        this.setSize("content","content");
    }
    
    public void updateSize(){
        float rw = 0f;
        float rh;
        var graphics = GuiSystem.INSTANCE.get().getGuiGraphics();
        if(!this.multiLine){
            rh = this.lineHeight + 4;
            rw = graphics.defaultFont().width(text.toString(), (int) lineHeight) + 10;
        }
        else if (this.wrapLine){
            var splitter = new SimpleTextSplitter();
            var font = graphics.defaultFont();
            var wrappedLines = splitter.split(font, text.toString(), (lineHeight/font.lineHeight()) * (rw - 10));
            rh = (this.lineHeight + 2) * wrappedLines.size() + 2;
        }
        else {
            var lines = text.toString().split("\n");
            rh = (this.lineHeight + 2) * lines.length + 2;
            rw = Arrays.stream(lines)
                    .map(s -> (float)graphics.defaultFont().width(s, (int) this.lineHeight))
                    .max(Float::compareTo)
                    .orElse(rw) + 10;
        }
        if(this.rw != rw || this.rh != rh){
            this.rw = rw;
            this.rh = rh;
            this.setStyle(s -> {
                s.size = TaffySize.of(TaffyDimension.length(this.rw),TaffyDimension.length(this.rh));
                s.minSize = TaffySize.of(TaffyDimension.length(this.rw),TaffyDimension.length(this.rh));
            });
            
        }
    }
    
    
    @Override
    public boolean isFocusable() {
        return true;
    }
    
    @Override
    public void onFocusChanged(boolean focused) {
        if (!focused){
            this.cursorLine = 0;
            this.cursorColumn = 0;
            this.selectionStartLine = -1;
            this.selectionStartColumn = -1;
            this.draggingSelection = false;
            this.cursorVisible = false;
        }
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        super.resize(offsetX, offsetY);
        int currentWidth = (int) this.width;
        if (currentWidth > 0 && currentWidth != lastWidth) {
            lastWidth = currentWidth;
            updateLines();
        }
    }
    
    protected void updateLines() {
        var graphics = GuiSystem.INSTANCE.get().getGuiGraphics();
        if (graphics == null) return;
        
        if (!multiLine) {
            displayLines.clear();
            displayLines.add(text.toString());
        } else if (wrapLine && lastWidth > 0) {
            var splitter = new SimpleTextSplitter();
            var font = graphics.defaultFont();
            displayLines = splitter.split(font, text.toString(), (lineHeight/font.lineHeight()) * (lastWidth - 10));
        } else {
            String[] textLines = text.toString().split("\n", -1);
            displayLines.clear();
            displayLines.addAll(Arrays.asList(textLines));
        }
        this.updateSize();
    }
    
    protected String[] getRawLines() {
        return text.toString().split("\n", -1);
    }
    
    protected String getRawLine(int lineIndex) {
        String[] rawLines = getRawLines();
        if (lineIndex < 0 || lineIndex >= rawLines.length) {
            return "";
        }
        return rawLines[lineIndex];
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        
        var font = graphics.defaultFont();
        float currentY = this.y + 2;
        
        int displayLineForCursor = -1;
        int displayColumnForCursor = -1;
        
        if (this.isPrimaryFocused() && multiLine && wrapLine) {
            var cursorPos = getCursorDisplayPosition();
            displayLineForCursor = cursorPos[0];
            displayColumnForCursor = cursorPos[1];
        }
        
        for (int i = 0; i < displayLines.size(); i++) {
            if (currentY > this.y + this.height) continue;
            if (currentY + lineHeight < this.y) {
                currentY += lineHeight + lineGap;
                continue;
            }
            
            String line = displayLines.get(i);
            float textX = calculateTextX(graphics, line);
            
            if (hasSelection()) {
                renderSelection(graphics, i, line, textX, currentY, font);
            }
            
            graphics.drawString(font, line, textX, currentY, 0xFFFFFFFF);
            
            if (this.isPrimaryFocused() && shouldShowCursor()) {
                if (multiLine && wrapLine) {
                    if (i == displayLineForCursor) {
                        renderCursorAtColumn(graphics, line, textX, currentY, font, displayColumnForCursor);
                    }
                } else {
                    if (i == cursorLine) {
                        renderCursorAtColumn(graphics, line, textX, currentY, font, cursorColumn);
                    }
                }
            }
            
            currentY += lineHeight + lineGap;
        }
    }
    
    protected int[] getCursorDisplayPosition() {
        if (!multiLine || !wrapLine) {
            return new int[]{cursorLine, cursorColumn};
        }
        
        String textUpToCursor = getTextUpToCursor();
        var graphics = GuiSystem.INSTANCE.get().getGuiGraphics();
        var font = graphics.defaultFont();
        var splitter = new SimpleTextSplitter();
        var wrappedLines = splitter.split(font, textUpToCursor, (lineHeight/font.lineHeight()) * (lastWidth - 10));
        
        if (wrappedLines.isEmpty()) {
            return new int[]{0, 0};
        }
        
        int displayLine = wrappedLines.size() - 1;
        int displayColumn = wrappedLines.get(displayLine).length();
        
        return new int[]{displayLine, displayColumn};
    }
    
    protected String getTextUpToCursor() {
        String[] rawLines = getRawLines();
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < cursorLine && i < rawLines.length; i++) {
            result.append(rawLines[i]);
            if (i < cursorLine - 1) {
                result.append('\n');
            }
        }
        
        if (cursorLine < rawLines.length) {
            if (cursorLine > 0) {
                result.append('\n');
            }
            String currentLine = rawLines[cursorLine];
            result.append(currentLine.substring(0, Math.min(cursorColumn, currentLine.length())));
        }
        
        return result.toString();
    }
    
    protected void renderCursorAtColumn(IGUIGraphics graphics, String line, float textX, float textY, IFont font, int column) {
        String beforeCursor = column > line.length() ? line : line.substring(0, Math.min(column, line.length()));
        float cursorX = textX + font.width(beforeCursor);
        graphics.vLine(cursorX, textY, textY + lineHeight, 0xFFFFFFFF);
    }
    
    protected float calculateTextX(IGUIGraphics graphics, String line) {
        float textX = this.x + 2;
        
        if (textAlign == TextAlign.CENTER) {
            float textWidth = graphics.defaultFont().width(line);
            textX = this.x + (this.width - textWidth) / 2f;
        } else if (textAlign == TextAlign.RIGHT) {
            float textWidth = graphics.defaultFont().width(line);
            textX = this.x + this.width - textWidth - 2;
        }
        
        return textX;
    }
    
    protected boolean shouldShowCursor() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBlinkTime > 530) {
            cursorVisible = !cursorVisible;
            lastBlinkTime = currentTime;
        }
        return cursorVisible;
    }
    
    protected void renderSelection(IGUIGraphics graphics, int lineIndex, String line, float textX, float textY, IFont font) {
        if (!hasSelection()) return;
        
        if (multiLine && wrapLine) {
            renderSelectionWrapped(graphics, lineIndex, line, textX, textY, font);
            return;
        }
        
        int startLine = Math.min(cursorLine, selectionStartLine);
        int endLine = Math.max(cursorLine, selectionStartLine);
        
        if (lineIndex < startLine || lineIndex > endLine) return;
        
        int startCol = 0;
        int endCol = line.length();
        
        if (lineIndex == startLine && lineIndex == endLine) {
            startCol = Math.min(cursorColumn, selectionStartColumn);
            endCol = Math.max(cursorColumn, selectionStartColumn);
        } else if (lineIndex == startLine) {
            startCol = cursorLine == startLine ? cursorColumn : selectionStartColumn;
        } else if (lineIndex == endLine) {
            endCol = cursorLine == endLine ? cursorColumn : selectionStartColumn;
        }
        
        String beforeSel = line.substring(0, Math.min(startCol, line.length()));
        String selected = line.substring(Math.min(startCol, line.length()), Math.min(endCol, line.length()));
        
        float selX = textX + font.width(beforeSel);
        float selWidth = font.width(selected);
        
        graphics.fill(selX, textY, selX + selWidth, textY + lineHeight, 0x800080FF);
    }
    
    protected void renderSelectionWrapped(IGUIGraphics graphics, int displayLineIndex, String displayLine, float textX, float textY, IFont font) {
        int startOffset = getAbsoluteOffset(Math.min(cursorLine, selectionStartLine),
                                           cursorLine < selectionStartLine ? cursorColumn : selectionStartColumn);
        int endOffset = getAbsoluteOffset(Math.max(cursorLine, selectionStartLine),
                                         cursorLine > selectionStartLine ? cursorColumn : selectionStartColumn);
        
        if (cursorLine == selectionStartLine) {
            startOffset = getAbsoluteOffset(cursorLine, Math.min(cursorColumn, selectionStartColumn));
            endOffset = getAbsoluteOffset(cursorLine, Math.max(cursorColumn, selectionStartColumn));
        }
        
        int displayLineStart = 0;
        for (int i = 0; i < displayLineIndex; i++) {
            displayLineStart += displayLines.get(i).length();
        }
        int displayLineEnd = displayLineStart + displayLine.length();
        
        if (displayLineEnd <= startOffset || displayLineStart >= endOffset) {
            return;
        }
        
        int selStart = Math.max(0, startOffset - displayLineStart);
        int selEnd = Math.min(displayLine.length(), endOffset - displayLineStart);
        
        if (selStart >= selEnd) return;
        
        String beforeSel = displayLine.substring(0, selStart);
        String selected = displayLine.substring(selStart, selEnd);
        
        float selX = textX + font.width(beforeSel);
        float selWidth = font.width(selected);
        
        graphics.fill(selX, textY, selX + selWidth, textY + lineHeight, 0x800080FF);
    }
    
    protected boolean hasSelection() {
        return selectionStartLine >= 0 && (selectionStartLine != cursorLine || selectionStartColumn != cursorColumn);
    }
    
    protected void clearSelection() {
        selectionStartLine = -1;
        selectionStartColumn = -1;
    }
    
    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }
    
    public float getLineHeight(){
        return this.lineHeight;
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        float relativeX = (float) event.x() - this.x;
        float relativeY = (float) event.y() - this.y;
        
        int clickedDisplayLine = (int) ((relativeY) / (lineHeight + lineGap));
        clickedDisplayLine = Math.max(0, Math.min(clickedDisplayLine, displayLines.size() - 1));
        
        String line = displayLines.get(clickedDisplayLine);
        var graphics = GuiSystem.INSTANCE.get().getGuiGraphics();
        float textX = calculateTextX(graphics, line);
        
        int clickedColumn = findColumnAtX(line, graphics.defaultFont(), relativeX + this.x - textX);
        
        if (multiLine && wrapLine) {
            int[] rawPos = mapDisplayToRawPosition(clickedDisplayLine, clickedColumn);
            cursorLine = rawPos[0];
            cursorColumn = rawPos[1];
        } else {
            cursorLine = clickedDisplayLine;
            cursorColumn = clickedColumn;
        }
        
        if (GuiSystem.INSTANCE.get().isShiftDown()) {
            if (selectionStartLine == -1) {
                selectionStartLine = cursorLine;
                selectionStartColumn = cursorColumn;
            }
        } else {
            clearSelection();
        }
        
        draggingSelection = true;
        selectionStartLine = cursorLine;
        selectionStartColumn = cursorColumn;
        
        return true;
    }
    
    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!draggingSelection) return super.onMouseDragged(event, dx, dy);
        
        float relativeX = (float) event.x() - this.x;
        float relativeY = (float) event.y() - this.y;
        
        int clickedDisplayLine = (int) ((relativeY) / (lineHeight + lineGap));
        clickedDisplayLine = Math.max(0, Math.min(clickedDisplayLine, displayLines.size() - 1));
        
        String line = displayLines.get(clickedDisplayLine);
        var graphics = GuiSystem.INSTANCE.get().getGuiGraphics();
        float textX = calculateTextX(graphics, line);
        
        int clickedColumn = findColumnAtX(line, graphics.defaultFont(), relativeX + this.x - textX);
        
        if (multiLine && wrapLine) {
            int[] rawPos = mapDisplayToRawPosition(clickedDisplayLine, clickedColumn);
            cursorLine = rawPos[0];
            cursorColumn = rawPos[1];
        } else {
            cursorLine = clickedDisplayLine;
            cursorColumn = clickedColumn;
        }
        
        return true;
    }
    
    protected int[] mapDisplayToRawPosition(int displayLine, int displayColumn) {
        if (!multiLine || !wrapLine) {
            return new int[]{displayLine, displayColumn};
        }
        
        int charCount = 0;
        for (int i = 0; i < displayLine && i < displayLines.size(); i++) {
            charCount += displayLines.get(i).length();
        }
        charCount += displayColumn;
        
        String[] rawLines = getRawLines();
        int currentChar = 0;
        
        for (int rawLine = 0; rawLine < rawLines.length; rawLine++) {
            String line = rawLines[rawLine];
            if (currentChar + line.length() >= charCount) {
                int rawColumn = charCount - currentChar;
                return new int[]{rawLine, Math.min(rawColumn, line.length())};
            }
            currentChar += line.length();
            if (rawLine < rawLines.length - 1) {
                currentChar += 1;
            }
        }
        
        if (rawLines.length > 0) {
            return new int[]{rawLines.length - 1, rawLines[rawLines.length - 1].length()};
        }
        
        return new int[]{0, 0};
    }
    
    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        draggingSelection = false;
        return super.onMouseReleased(event);
    }
    
    protected int findColumnAtX(String line, IFont font, float targetX) {
        if (targetX <= 0) return 0;
        
        for (int i = 0; i <= line.length(); i++) {
            String sub = line.substring(0, i);
            float width = font.width(sub);
            if (width >= targetX) {
                if (i == 0) return 0;
                String prev = line.substring(0, i - 1);
                float prevWidth = font.width(prev);
                return (targetX - prevWidth) < (width - targetX) ? i - 1 : i;
            }
        }
        
        return line.length();
    }
    
    @Override
    protected boolean onKeyPressed(IKeyEvent event) {
        if (!allowEdit) {
            if (event.isCopy()) {
                copyToClipboard();
                return true;
            }
            return super.onKeyPressed(event);
        }
        
        resetCursorBlink();
        
        if (event.isCopy()) {
            copyToClipboard();
            return true;
        }
        
        if (event.isPaste()) {
            pasteFromClipboard();
            return true;
        }
        
        if (event.isCut()) {
            copyToClipboard();
            deleteSelection();
            return true;
        }
        
        if (event.isSelectAll()) {
            selectAll();
            return true;
        }
        
        int key = event.key();
        boolean shift = event.hasShiftDown();
        boolean ctrl = event.hasControlDownWithQuirk();
        
        switch (key) {
            case GLFW.GLFW_KEY_LEFT:
                moveCursorLeft(shift, ctrl);
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                moveCursorRight(shift, ctrl);
                return true;
            case GLFW.GLFW_KEY_UP:
                if (multiLine) {
                    moveCursorUp(shift);
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_DOWN:
                if (multiLine) {
                    moveCursorDown(shift);
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_HOME:
                moveCursorToLineStart(shift);
                return true;
            case GLFW.GLFW_KEY_END:
                moveCursorToLineEnd(shift);
                return true;
            case GLFW.GLFW_KEY_BACKSPACE:
                handleBackspace();
                return true;
            case GLFW.GLFW_KEY_DELETE:
                handleDelete();
                return true;
            case GLFW.GLFW_KEY_ENTER:
                if (multiLine) {
                    insertText("\n");
                    return true;
                }
                break;
        }
        
        return super.onKeyPressed(event);
    }
    
    @Override
    protected boolean onCharTyped(ICharEvent event) {
        if (!allowEdit) return super.onCharTyped(event);
        
        var str = event.codepointAsString();
        if(str != null && !str.isEmpty()){
            insertText(str);
        }
        return true;
    }
    
    protected void resetCursorBlink() {
        cursorVisible = true;
        lastBlinkTime = System.currentTimeMillis();
    }
    
    protected void insertText(String str) {
        if (hasSelection()) {
            deleteSelection();
        }
        
        int offset = getAbsoluteOffset(cursorLine, cursorColumn);
        text.insert(offset, str);
        
        if (str.contains("\n")) {
            this.updateLines();
            int newlines = (int) str.chars().filter(ch -> ch == '\n').count();
            cursorLine += newlines;
            int lastNewline = str.lastIndexOf('\n');
            cursorColumn = str.length() - lastNewline - 1;
        } else {
            cursorColumn += str.length();
            this.updateLines();
        }
        this.markDirty();
        clearSelection();
    }
    
    protected void deleteSelection() {
        if (!hasSelection()) return;
        
        int startLine = Math.min(cursorLine, selectionStartLine);
        int startCol = (startLine == cursorLine) ? cursorColumn : selectionStartColumn;
        if (startLine == selectionStartLine && startLine == cursorLine) {
            startCol = Math.min(cursorColumn, selectionStartColumn);
        }
        
        int endLine = Math.max(cursorLine, selectionStartLine);
        int endCol = (endLine == cursorLine) ? cursorColumn : selectionStartColumn;
        if (endLine == selectionStartLine && endLine == cursorLine) {
            endCol = Math.max(cursorColumn, selectionStartColumn);
        }
        
        int startOffset = getAbsoluteOffset(startLine, startCol);
        int endOffset = getAbsoluteOffset(endLine, endCol);
        
        text.delete(startOffset, endOffset);
        
        cursorLine = startLine;
        cursorColumn = startCol;
        clearSelection();
        updateLines();
    }
    
    protected int getAbsoluteOffset(int line, int column) {
        String textStr = text.toString();
        int offset = 0;
        int currentLine = 0;
        
        for (int i = 0; i < textStr.length() && currentLine < line; i++) {
            if (textStr.charAt(i) == '\n') {
                currentLine++;
            }
            offset++;
        }
        
        String lineText = "";
        int lineStart = offset;
        for (int i = offset; i < textStr.length(); i++) {
            if (textStr.charAt(i) == '\n') break;
            lineText += textStr.charAt(i);
        }
        
        return lineStart + Math.min(column, lineText.length());
    }
    
    protected void handleBackspace() {
        if (hasSelection()) {
            deleteSelection();
        } else if (cursorColumn > 0) {
            cursorColumn--;
            int offset = getAbsoluteOffset(cursorLine, cursorColumn);
            text.deleteCharAt(offset);
            updateLines();
        } else if (cursorLine > 0 && multiLine) {
            cursorLine--;
            String prevLine = getRawLine(cursorLine);
            cursorColumn = prevLine.length();
            int offset = getAbsoluteOffset(cursorLine, cursorColumn);
            text.deleteCharAt(offset);
            updateLines();
        }
    }
    
    protected void handleDelete() {
        if (hasSelection()) {
            deleteSelection();
        } else {
            String currentLine = getRawLine(cursorLine);
            if (cursorColumn < currentLine.length()) {
                int offset = getAbsoluteOffset(cursorLine, cursorColumn);
                text.deleteCharAt(offset);
                updateLines();
            } else if (cursorLine < getRawLines().length - 1 && multiLine) {
                int offset = getAbsoluteOffset(cursorLine, cursorColumn);
                text.deleteCharAt(offset);
                updateLines();
            }
        }
    }
    
    protected void moveCursorLeft(boolean shift, boolean ctrl) {
        if (!shift && hasSelection() && !ctrl) {
            int startLine = Math.min(cursorLine, selectionStartLine);
            int startCol = Math.min(cursorColumn, selectionStartColumn);
            cursorLine = startLine;
            cursorColumn = startCol;
            clearSelection();
            return;
        }
        
        if (shift && selectionStartLine == -1) {
            selectionStartLine = cursorLine;
            selectionStartColumn = cursorColumn;
        }
        
        String currentLine = getRawLine(cursorLine);
        
        if (cursorColumn > 0) {
            if (ctrl) {
                cursorColumn = findPreviousWordBoundary(currentLine, cursorColumn);
            } else {
                cursorColumn--;
            }
        } else if (cursorLine > 0 && multiLine) {
            cursorLine--;
            cursorColumn = getRawLine(cursorLine).length();
        }
        
        if (!shift) {
            clearSelection();
        }
    }
    
    protected void moveCursorRight(boolean shift, boolean ctrl) {
        if (!shift && hasSelection() && !ctrl) {
            int endLine = Math.max(cursorLine, selectionStartLine);
            int endCol = Math.max(cursorColumn, selectionStartColumn);
            cursorLine = endLine;
            cursorColumn = endCol;
            clearSelection();
            return;
        }
        
        if (shift && selectionStartLine == -1) {
            selectionStartLine = cursorLine;
            selectionStartColumn = cursorColumn;
        }
        
        String currentLine = getRawLine(cursorLine);
        if (cursorColumn < currentLine.length()) {
            if (ctrl) {
                cursorColumn = findNextWordBoundary(currentLine, cursorColumn);
            } else {
                cursorColumn++;
            }
        } else if (cursorLine < getRawLines().length - 1 && multiLine) {
            cursorLine++;
            cursorColumn = 0;
        }
        
        if (!shift) {
            clearSelection();
        }
    }
    
    protected void moveCursorUp(boolean shift) {
        if (shift && selectionStartLine == -1) {
            selectionStartLine = cursorLine;
            selectionStartColumn = cursorColumn;
        }
        
        if (cursorLine > 0) {
            cursorLine--;
            String line = getRawLine(cursorLine);
            cursorColumn = Math.min(cursorColumn, line.length());
        }
        
        if (!shift) {
            clearSelection();
        }
    }
    
    protected void moveCursorDown(boolean shift) {
        if (shift && selectionStartLine == -1) {
            selectionStartLine = cursorLine;
            selectionStartColumn = cursorColumn;
        }
        
        if (cursorLine < getRawLines().length - 1) {
            cursorLine++;
            String line = getRawLine(cursorLine);
            cursorColumn = Math.min(cursorColumn, line.length());
        }
        
        if (!shift) {
            clearSelection();
        }
    }
    
    protected void moveCursorToLineStart(boolean shift) {
        if (shift && selectionStartLine == -1) {
            selectionStartLine = cursorLine;
            selectionStartColumn = cursorColumn;
        }
        
        cursorColumn = 0;
        
        if (!shift) {
            clearSelection();
        }
    }
    
    protected void moveCursorToLineEnd(boolean shift) {
        if (shift && selectionStartLine == -1) {
            selectionStartLine = cursorLine;
            selectionStartColumn = cursorColumn;
        }
        
        cursorColumn = getRawLine(cursorLine).length();
        
        if (!shift) {
            clearSelection();
        }
    }
    
    protected int findPreviousWordBoundary(String line, int position) {
        if (position <= 0) return 0;
        
        int pos = position - 1;
        while (pos > 0 && Character.isWhitespace(line.charAt(pos))) {
            pos--;
        }
        
        while (pos > 0 && !Character.isWhitespace(line.charAt(pos - 1))) {
            pos--;
        }
        
        return pos;
    }
    
    protected int findNextWordBoundary(String line, int position) {
        if (position >= line.length()) return line.length();
        
        int pos = position;
        while (pos < line.length() && !Character.isWhitespace(line.charAt(pos))) {
            pos++;
        }
        
        while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) {
            pos++;
        }
        
        return pos;
    }
    
    protected void selectAll() {
        cursorLine = 0;
        cursorColumn = 0;
        selectionStartLine = 0;
        selectionStartColumn = 0;
        
        String[] rawLines = getRawLines();
        if (rawLines.length > 0) {
            cursorLine = rawLines.length - 1;
            cursorColumn = rawLines[cursorLine].length();
        }
    }
    
    protected void copyToClipboard() {
        if (!hasSelection()) return;
        
        int startLine = Math.min(cursorLine, selectionStartLine);
        int startCol = (startLine == cursorLine) ? cursorColumn : selectionStartColumn;
        if (startLine == selectionStartLine && startLine == cursorLine) {
            startCol = Math.min(cursorColumn, selectionStartColumn);
        }
        
        int endLine = Math.max(cursorLine, selectionStartLine);
        int endCol = (endLine == cursorLine) ? cursorColumn : selectionStartColumn;
        if (endLine == selectionStartLine && endLine == cursorLine) {
            endCol = Math.max(cursorColumn, selectionStartColumn);
        }
        
        int startOffset = getAbsoluteOffset(startLine, startCol);
        int endOffset = getAbsoluteOffset(endLine, endCol);
        
        String selectedText = text.substring(startOffset, endOffset);
        
        try {
            long window = XKLib.RENDER_CONTEXT.get().getWindow().getHandle();
            GLFW.glfwSetClipboardString(window, selectedText);
        } catch (Exception _) {
        }
    }
    
    protected void pasteFromClipboard() {
        try {
            long window = XKLib.RENDER_CONTEXT.get().getWindow().getHandle();
            String clipboardText = GLFW.glfwGetClipboardString(window);
            if (clipboardText != null && !clipboardText.isEmpty()) {
                if (!multiLine) {
                    clipboardText = clipboardText.replace("\n", "").replace("\r", "");
                }
                insertText(clipboardText);
            }
        } catch (Exception e) {
        }
    }
    
    public String getText() {
        return text.toString();
    }
    
    public void setText(String text) {
        this.text = new StringBuilder(text);
        cursorLine = 0;
        cursorColumn = 0;
        clearSelection();
        updateLines();
    }
    
    public boolean isMultiLine() {
        return multiLine;
    }
    
    public void setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
        updateLines();
    }
    
    public boolean isWrapLine() {
        return wrapLine;
    }
    
    public void setWrapLine(boolean wrapLine) {
        this.wrapLine = wrapLine;
        updateLines();
    }
    
    public boolean isAllowEdit() {
        return allowEdit;
    }
    
    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
    }
    
    public TextAlign getTextAlign() {
        return textAlign;
    }
    
    public void setTextAlign(TextAlign textAlign) {
        this.textAlign = textAlign;
    }
}
