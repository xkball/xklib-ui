package com.xkball.xklib.ui.widget.widgets;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.render.IFont;
import com.xkball.xklib.ui.widget.AbstractWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class EditBox extends AbstractWidget {

    protected String text = "";
    protected int lineHeight = 20;
    protected int boxHeight = -1;
    protected int textColor = 0xFF000000;
    protected int cursorColor = 0xFF000000;
    protected int selectionColor = 0x804080FF;
    protected int backgroundColor = 0xFFFFFFFF;
    protected int borderColor = 0xFF888888;
    protected int focusedBorderColor = 0xFF4080FF;
    
    protected int cursorPosition = 0;
    protected int selectionStart = -1;
    protected int scrollOffset = 0;
    protected boolean isDragging = false;
    protected long lastBlinkTime = 0;
    protected boolean cursorVisible = true;
    protected static final long CURSOR_BLINK_INTERVAL = 500;
    
    protected static final int UNDO_STACK_SIZE = 16;
    protected final Deque<UndoState> undoStack = new ArrayDeque<>(UNDO_STACK_SIZE);

    public EditBox() {
        super();
    }

    public EditBox(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public String getText() {
        return this.text;
    }

    public EditBox setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPosition = Math.min(this.cursorPosition, this.text.length());
        this.selectionStart = -1;
        this.undoStack.clear();
        this.clampScrollOffset();
        this.markDirty();
        return this;
    }

    public int getLineHeight() {
        return this.lineHeight;
    }

    public EditBox setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        this.markDirty();
        return this;
    }

    public int getBoxHeight() {
        return this.boxHeight > 0 ? this.boxHeight : this.lineHeight * 2;
    }

    public EditBox setBoxHeight(int boxHeight) {
        this.boxHeight = boxHeight;
        this.markDirty();
        return this;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public EditBox setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public EditBox setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public EditBox setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public EditBox setFocusedBorderColor(int focusedBorderColor) {
        this.focusedBorderColor = focusedBorderColor;
        return this;
    }

    public EditBox setCursorColor(int cursorColor) {
        this.cursorColor = cursorColor;
        return this;
    }

    public EditBox setSelectionColor(int selectionColor) {
        this.selectionColor = selectionColor;
        return this;
    }

    @Override
    public void resize() {
        super.resize();
        this.clampScrollOffset();
    }

    @Override
    public int expectWidth() {
        return 100;
    }

    @Override
    public int expectHeight() {
        return this.getBoxHeight();
    }

    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        int actualBoxHeight = this.getBoxHeight();
        int boxY = this.contentY + (this.contentHeight - actualBoxHeight) / 2;
        
        graphics.fill(this.contentX, boxY, this.contentX + this.contentWidth, boxY + actualBoxHeight, this.backgroundColor);
        
        int border = this.focused ? this.focusedBorderColor : this.borderColor;
        graphics.renderOutline(this.contentX, boxY, this.contentWidth, actualBoxHeight, border);
        
        int textY = boxY + (actualBoxHeight - this.lineHeight) / 2;
        int textX = this.contentX + 4 - this.scrollOffset;
        
        graphics.enableScissor(this.contentX + 2, boxY, this.contentX + this.contentWidth - 2, boxY + actualBoxHeight);
        
        if (this.hasSelection()) {
            int selStart = Math.min(this.selectionStart, this.cursorPosition);
            int selEnd = Math.max(this.selectionStart, this.cursorPosition);
            
            int selStartX = textX + this.getTextWidth(this.text.substring(0, selStart));
            int selEndX = textX + this.getTextWidth(this.text.substring(0, selEnd));
            
            graphics.fill(selStartX, textY, selEndX, textY + this.lineHeight, this.selectionColor);
        }
        
        if (!this.text.isEmpty()) {
            graphics.drawString(this.text, textX, textY, this.textColor, this.lineHeight);
        }
        
        if (this.focused && this.enabled && this.cursorVisible) {
            int cursorX = textX + this.getTextWidth(this.text.substring(0, this.cursorPosition));
            graphics.fill(cursorX, textY + 2, cursorX + 1, textY + this.lineHeight - 2, this.cursorColor);
        }
        
        graphics.disableScissor();
        
        if (this.focused && this.enabled) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastBlinkTime > CURSOR_BLINK_INTERVAL) {
                this.cursorVisible = !this.cursorVisible;
                this.lastBlinkTime = currentTime;
            }
        }
        else if (!this.focused) {
            this.selectionStart = -1;
            this.cursorVisible = false;
        }
        
        super.render(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.visible) {
            return false;
        }
        
        if (this.isMouseOver(event.x(), event.y())) {
            if (this.enabled && this.shouldTakeFocusAfterInteraction()) {
                this.setFocused(true);
            }
            return this.onMouseClicked(event, doubleClick);
        }
        return false;
    }

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        this.cursorVisible = true;
        this.lastBlinkTime = System.currentTimeMillis();
        
        int clickPos = this.getPositionFromMouse(event.x());
        
        if (doubleClick) {
            this.selectWord(clickPos);
        } else {
            if (event.hasShiftDown()) {
                if (this.selectionStart < 0) {
                    this.selectionStart = this.cursorPosition;
                }
                this.cursorPosition = clickPos;
            } else {
                this.selectionStart = clickPos;
                this.cursorPosition = clickPos;
            }
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
        return this.onMouseDragged(event, dx, dy);
    }

    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (this.isDragging) {
            if (this.selectionStart < 0) {
                this.selectionStart = this.cursorPosition;
            }
            this.cursorPosition = this.getPositionFromMouse(event.x());
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
        return this.onMouseReleased(event);
    }

    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        this.isDragging = false;
        return true;
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
        
        if (!this.enabled) {
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
        
        if (event.key() == GLFW.GLFW_KEY_HOME) {
            this.moveCursorToStart(event.hasShiftDown());
            return true;
        }
        
        if (event.key() == GLFW.GLFW_KEY_END) {
            this.moveCursorToEnd(event.hasShiftDown());
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
        
        return false;
    }

    @Override
    protected boolean onCharTyped(ICharEvent event) {
        if (!this.enabled) {
            return false;
        }
        
        this.cursorVisible = true;
        this.lastBlinkTime = System.currentTimeMillis();
        
        String charStr = event.codepointAsString();
        if (charStr != null && !charStr.isEmpty()) {
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
        }
    }

    protected void insertText(String str) {
        this.pushUndoState();
        
        if (this.hasSelection()) {
            this.deleteSelection();
        }
        
        String before = this.text.substring(0, this.cursorPosition);
        String after = this.text.substring(this.cursorPosition);
        this.text = before + str + after;
        this.cursorPosition += str.length();
        this.ensureCursorVisible();
    }

    protected void deleteSelection() {
        if (!this.hasSelection()) {
            return;
        }
        
        int start = Math.min(this.selectionStart, this.cursorPosition);
        int end = Math.max(this.selectionStart, this.cursorPosition);
        
        this.text = this.text.substring(0, start) + this.text.substring(end);
        this.cursorPosition = start;
        this.selectionStart = -1;
        this.clampScrollOffset();
    }

    protected void deleteBackward(boolean wholeWord) {
        if (this.hasSelection()) {
            this.pushUndoState();
            this.deleteSelection();
        } else if (this.cursorPosition > 0) {
            this.pushUndoState();
            int deletePos = wholeWord ? this.findWordStart(this.cursorPosition) : this.cursorPosition - 1;
            this.text = this.text.substring(0, deletePos) + this.text.substring(this.cursorPosition);
            this.cursorPosition = deletePos;
            this.clampScrollOffset();
        }
        this.ensureCursorVisible();
    }

    protected void deleteForward(boolean wholeWord) {
        if (this.hasSelection()) {
            this.pushUndoState();
            this.deleteSelection();
        } else if (this.cursorPosition < this.text.length()) {
            this.pushUndoState();
            int deletePos = wholeWord ? this.findWordEnd(this.cursorPosition) : this.cursorPosition + 1;
            this.text = this.text.substring(0, this.cursorPosition) + this.text.substring(deletePos);
            this.clampScrollOffset();
        }
        this.ensureCursorVisible();
    }

    protected void moveCursorLeft(boolean selecting, boolean wholeWord) {
        if (selecting && this.selectionStart < 0) {
            this.selectionStart = this.cursorPosition;
        } else if (!selecting) {
            this.selectionStart = -1;
        }
        
        if (wholeWord) {
            this.cursorPosition = this.findWordStart(this.cursorPosition);
        } else if (this.cursorPosition > 0) {
            this.cursorPosition--;
        }
        
        this.ensureCursorVisible();
    }

    protected void moveCursorRight(boolean selecting, boolean wholeWord) {
        if (selecting && this.selectionStart < 0) {
            this.selectionStart = this.cursorPosition;
        } else if (!selecting) {
            this.selectionStart = -1;
        }
        
        if (wholeWord) {
            this.cursorPosition = this.findWordEnd(this.cursorPosition);
        } else if (this.cursorPosition < this.text.length()) {
            this.cursorPosition++;
        }
        
        this.ensureCursorVisible();
    }

    protected void moveCursorToStart(boolean selecting) {
        if (selecting && this.selectionStart < 0) {
            this.selectionStart = this.cursorPosition;
        } else if (!selecting) {
            this.selectionStart = -1;
        }
        
        this.cursorPosition = 0;
        this.ensureCursorVisible();
    }

    protected void moveCursorToEnd(boolean selecting) {
        if (selecting && this.selectionStart < 0) {
            this.selectionStart = this.cursorPosition;
        } else if (!selecting) {
            this.selectionStart = -1;
        }
        
        this.cursorPosition = this.text.length();
        this.ensureCursorVisible();
    }

    protected void selectAll() {
        this.selectionStart = 0;
        this.cursorPosition = this.text.length();
        this.ensureCursorVisible();
    }

    protected void selectWord(int position) {
        this.selectionStart = this.findWordStart(position);
        this.cursorPosition = this.findWordEnd(position);
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
            clipboard = clipboard.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
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
        this.undoStack.push(new UndoState(this.text, this.cursorPosition, this.selectionStart));
    }

    protected void undo() {
        if (this.undoStack.isEmpty()) {
            return;
        }
        
        UndoState state = this.undoStack.pop();
        this.text = state.text;
        this.cursorPosition = state.cursorPosition;
        this.selectionStart = state.selectionStart;
        this.clampScrollOffset();
        this.ensureCursorVisible();
    }

    protected record UndoState(String text, int cursorPosition, int selectionStart) {}

    protected boolean hasSelection() {
        return this.selectionStart >= 0 && this.selectionStart != this.cursorPosition;
    }

    protected String getSelectedText() {
        if (!this.hasSelection()) {
            return "";
        }
        int start = Math.min(this.selectionStart, this.cursorPosition);
        int end = Math.max(this.selectionStart, this.cursorPosition);
        return this.text.substring(start, end);
    }

    protected int findWordStart(int position) {
        if (position <= 0) {
            return 0;
        }
        
        int pos = position - 1;
        while (pos > 0 && Character.isWhitespace(this.text.charAt(pos))) {
            pos--;
        }
        while (pos > 0 && !Character.isWhitespace(this.text.charAt(pos - 1))) {
            pos--;
        }
        return pos;
    }

    protected int findWordEnd(int position) {
        if (position >= this.text.length()) {
            return this.text.length();
        }
        
        int pos = position;
        while (pos < this.text.length() && Character.isWhitespace(this.text.charAt(pos))) {
            pos++;
        }
        while (pos < this.text.length() && !Character.isWhitespace(this.text.charAt(pos))) {
            pos++;
        }
        return pos;
    }

    protected int getPositionFromMouse(double mouseX) {
        int textX = this.contentX + 4 - this.scrollOffset;
        int relativeX = (int) mouseX - textX;
        
        if (relativeX <= 0) {
            return 0;
        }
        
        float scale = this.getScale();
        IFont font = XKLib.gui.getGuiGraphics().defaultFont();
        
        for (int i = 0; i <= this.text.length(); i++) {
            int charWidth = (int) (font.width(this.text.substring(0, i)) * scale);
            if (charWidth >= relativeX) {
                if (i > 0) {
                    int prevWidth = (int) (font.width(this.text.substring(0, i - 1)) * scale);
                    if (relativeX - prevWidth < charWidth - relativeX) {
                        return i - 1;
                    }
                }
                return i;
            }
        }
        return this.text.length();
    }

    protected void ensureCursorVisible() {
        int cursorX = this.getTextWidth(this.text.substring(0, this.cursorPosition));
        int visibleWidth = this.contentWidth - 8;
        
        if (cursorX - this.scrollOffset < 0) {
            this.scrollOffset = cursorX;
        } else if (cursorX - this.scrollOffset > visibleWidth) {
            this.scrollOffset = cursorX - visibleWidth;
        }
        
        this.clampScrollOffset();
    }

    protected void clampScrollOffset() {
        int textWidth = this.getTextWidth(this.text);
        int visibleWidth = this.contentWidth - 8;
        
        if (textWidth <= visibleWidth) {
            this.scrollOffset = 0;
        } else {
            this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, textWidth - visibleWidth));
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
        float scale = this.getScale();
        return (int) (font.width(text) * scale);
    }

    protected float getScale() {
        IGUIGraphics graphics = XKLib.gui.getGuiGraphics();
        if (graphics == null) {
            return 1.0f;
        }
        return this.lineHeight / (float) graphics.defaultFont().lineHeight();
    }
}
