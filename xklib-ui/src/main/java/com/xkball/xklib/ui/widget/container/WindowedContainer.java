package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.IconButton;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.TaffyDimension;

@GuiWidgetClass
public class WindowedContainer extends AbsoluteContainer {
    
    private static final float TOP_BAR_HEIGHT = 24f;
    private static final float BORDER_HIT_SIZE = 2f;
    private static final float MIN_WINDOW_WIDTH = 120f;
    private static final float MIN_WINDOW_HEIGHT = 80f;
    private static final int RESIZE_NONE = 0;
    private static final int RESIZE_LEFT = 1;
    private static final int RESIZE_RIGHT = 2;
    private static final int RESIZE_TOP = 4;
    private static final int RESIZE_BOTTOM = 8;
    private static final String DEFAULT_WINDOW_CSS = """
            display: flex;
            flex-direction: column;
            """;
    private static final String TOP_BAR_CSS = """
            display: flex;
            flex-direction: row;
            align-items: center;
            justify-content: start;
            size: 100% 10rpx;
            background-color: 0xEE1E293B;
            """;
    private static final String TITLE_CSS = """
            size: 100%-10rpx 10rpx;
            margin-left: 4px;
            text-color: 0xFFE2E8F0;
            text-drop-shadow: false;
            text-scale: expand-width;
            text-height: 10rpx;
            """;
    private static final String CONTENT_PANEL_CSS = """
            display: flex;
            align-items: stretch;
            justify-content: stretch;
            size: 100% 100%-10rpx;
            background-color: 0xEE0F172A;
            """;
    private static final String CONTENT_PANEL_CSS_AUTO_HEIGHT = """
            display: flex;
            align-items: stretch;
            justify-content: stretch;
            size: 100% auto;
            background-color: 0xEE0F172A;
            """;
    private static final String CLOSE_BUTTON_CSS = """
            size: 10rpx 10rpx;
            margin-right: 2rpx;
            """;

    private float nextWindowX = 0;
    private float nextWindowY = 0;
    private boolean dispatchingMouseClick = false;
    private SubWindow activeMouseWindow = null;
    private boolean autoRemoveFromGuiSystemWhenEmpty = false;
    private boolean blockInput = false;

    public WindowedContainer() {
        this.clampWidget = false;
    }

    public boolean isAutoRemoveFromGuiSystemWhenEmpty() {
        return this.autoRemoveFromGuiSystemWhenEmpty;
    }

    public WindowedContainer setAutoRemoveFromGuiSystemWhenEmpty(boolean value) {
        this.autoRemoveFromGuiSystemWhenEmpty = value;
        return this;
    }

    public void setBlockInput(boolean value) {
        this.blockInput = value;
    }

    public boolean isBlockInput() {
        return blockInput;
    }

    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible() || !this.isMouseOver(event.x(), event.y())) {
            return false;
        }
        Widget clickedChild = null;
        var oldDispatchingMouseClick = this.dispatchingMouseClick;
        this.dispatchingMouseClick = true;
        try {
            for (Widget child : this.children) {
                if (child.visible() && child.enabled && child.getRectangle().containsPoint((int) event.x(), (int) event.y())) {
                    if (child.mouseClicked(event, doubleClick)) {
                        clickedChild = child;
                        break;
                    }
                }
            }
        } finally {
            this.dispatchingMouseClick = oldDispatchingMouseClick;
        }
        if (clickedChild instanceof SubWindow subWindow && subWindow.closed) {
            this.removeChild(clickedChild);
            this.activeMouseWindow = null;
            return true;
        }
        if (clickedChild instanceof SubWindow subWindow) {
            this.activeMouseWindow = subWindow;
        }
        if (clickedChild != null && this.autoReorder && this.children.size() > 1) {
            this.children.remove(clickedChild);
            this.children.addFirst(clickedChild);
            return true;
        }
        return clickedChild != null || this.blockInput;
    }

    @Override
    public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!this.enabled || !this.visible()) {
            return false;
        }
        if (this.activeMouseWindow != null && this.activeMouseWindow.enabled && this.activeMouseWindow.visible()) {
            return this.activeMouseWindow.mouseDragged(event, dx, dy) || this.blockInput;
        }
        return super.mouseDragged(event, dx, dy) || this.blockInput;
    }

    @Override
    public boolean mouseReleased(IMouseButtonEvent event) {
        if (!this.enabled || !this.visible()) {
            return false;
        }
        if (this.activeMouseWindow != null) {
            var window = this.activeMouseWindow;
            this.activeMouseWindow = null;
            return window.mouseReleased(event) || this.blockInput;
        }
        return super.mouseReleased(event) || this.blockInput;
    }

    @Override
    public boolean keyPressed(IKeyEvent event) {
        return super.keyPressed(event) || (this.enabled && this.visible() && this.blockInput);
    }

    @Override
    public boolean keyReleased(IKeyEvent event) {
        return super.keyReleased(event) || (this.enabled && this.visible() && this.blockInput);
    }

    @Override
    public boolean charTyped(ICharEvent event) {
        return super.charTyped(event) || (this.enabled && this.visible() && this.blockInput);
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        //如果返回super的结果, 则多层时, 下层组件永远无法拿到hover的状态
        return blockInput;
    }
    
    @Override
    public void setHovered(boolean hovered) {
        //todo 以后GuiSystem不应该是分层的
        //这相当于一个窗口管理器 不应该持有hover状态
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        super.resize(offsetX, offsetY);
        for (var child : this.children) {
            if (child instanceof SubWindow subWindow && subWindow.clampTopBarInParent()) {
                subWindow.resize(this.x - this.xScrollOffset + subWindow.getAbsoluteX(), this.y - this.yScrollOffset + subWindow.getAbsoluteY());
            }
        }
    }
    
    @Override
    public void onSizeChanged() {
        this.nextWindowX = this.width / 2;
        this.nextWindowY = this.height / 2;
    }
    
    @Override
    public void removeChild(Widget widget) {
        super.removeChild(widget);
        if (this.activeMouseWindow == widget) {
            this.activeMouseWindow = null;
        }
        this.tryAutoRemoveFromGuiSystem();
    }

    private void tryAutoRemoveFromGuiSystem() {
        if (!this.autoRemoveFromGuiSystemWhenEmpty) {
            return;
        }
        for (var child : this.children) {
            if (child instanceof SubWindow) {
                return;
            }
        }
        GuiSystem.INSTANCE.get().removeScreenLayer(this);
    }


    public SubWindow addSubWindow(Widget content, float width, float height) {
        return this.addSubWindow(content, "", true, this.nextWindowX, this.nextWindowY, width, height, "", true);
    }

    public SubWindow addSubWindow(Widget content, float width, float height, String frameCssStyle) {
        return this.addSubWindow(content, "", true, this.nextWindowX, this.nextWindowY, width, height, frameCssStyle, true);
    }

    public SubWindow addSubWindow(Widget content, IComponent title, boolean resizable, float width, float height) {
        return this.addSubWindow(content, title, resizable, this.nextWindowX, this.nextWindowY, width, height, "", true);
    }

    public SubWindow addSubWindow(Widget content, String title, boolean resizable, float width, float height) {
        return this.addSubWindow(content, IComponent.literal(title), resizable, this.nextWindowX, this.nextWindowY, width, height, "", true);
    }

    public SubWindow addSubWindow(Widget content, float x, float y, float width, float height) {
        return this.addSubWindow(content, "", true, x, y, width, height, "", true);
    }

    public SubWindow addSubWindow(Widget content, IComponent title, boolean resizable, float x, float y, float width, float height) {
        return this.addSubWindow(content, title, resizable, x, y, width, height, "", true);
    }

    public SubWindow addSubWindow(Widget content, String title, boolean resizable, float x, float y, float width, float height) {
        return this.addSubWindow(content, IComponent.literal(title), resizable, x, y, width, height, "", true);
    }

    public SubWindow addSubWindow(Widget content, IComponent title, boolean resizable, float x, float y, float width, float height, String frameCssStyle, boolean autoShrinkHeight) {
        var window = new SubWindow(content, title, resizable, frameCssStyle);
        window.setAutoHeight(autoShrinkHeight);
        window.setAbsoluteSize(x, y);
        window.setWindowSize(width, height);
        this.addChild(window);
        this.nextWindowX = x + 14;
        this.nextWindowY = y + 14;
        return window;
    }

    public SubWindow addSubWindow(Widget content, String title, boolean resizable, float x, float y, float width, float height, String frameCssStyle, boolean autoShrinkHeight) {
        return this.addSubWindow(content, IComponent.literal(title), resizable, x, y, width, height, frameCssStyle, autoShrinkHeight);
    }

    @Override
    protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
        return super.onMouseScrolled(x, y, scrollX, scrollY) || blockInput;
    }

    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        return super.onMouseDragged(event, dx, dy) || blockInput;
    }

    @Override
    protected boolean onCharTyped(ICharEvent event) {
        return super.onCharTyped(event) || blockInput;
    }

    @Override
    protected boolean onKeyReleased(IKeyEvent event) {
        return super.onKeyReleased(event) || blockInput;
    }

    @Override
    protected boolean onKeyPressed(IKeyEvent event) {
        return super.onKeyPressed(event) || blockInput;
    }

    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        return super.onMouseReleased(event) || blockInput;
    }

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        return super.onMouseClicked(event, doubleClick) || blockInput;
    }

    public class SubWindow extends ContainerWidget {

        private final ContainerWidget topBar = new ContainerWidget();
        private final ContainerWidget contentPanel = new ContainerWidget();
        private final Label titleLabel = new Label();
        private final IconButton closeButton = new IconButton(new ResourceLocation("xklibmc", "icon/close"), this::close);
        private final boolean resizable;
        private int resizeMode = RESIZE_NONE;
        private boolean moving = false;
        private boolean closed = false;
        private boolean autoHeight = false;
        private float minWidth = MIN_WINDOW_WIDTH;
        private float minHeight = MIN_WINDOW_HEIGHT;
        private float outerWidth;
        private float outerHeight;

        public SubWindow(Widget content, IComponent title, boolean resizable, String frameCssStyle) {
            this.resizable = resizable;
            this.inlineStyle(DEFAULT_WINDOW_CSS + frameCssStyle);
            this.topBar.inlineStyle(TOP_BAR_CSS);
            this.contentPanel.inlineStyle(CONTENT_PANEL_CSS);
            this.titleLabel.setText(title);
            this.titleLabel.inlineStyle(TITLE_CSS);
            this.closeButton.inlineStyle(CLOSE_BUTTON_CSS);
            this.topBar.addChild(this.titleLabel);
            this.topBar.addChild(this.closeButton);
            this.addChild(this.topBar);
            this.addChild(this.contentPanel);
            this.setContent(content);
        }

        public SubWindow(Widget content, String title, boolean resizable, String frameCssStyle) {
            this(content, IComponent.literal(title), resizable, frameCssStyle);
        }

        public SubWindow setContent(Widget content) {
            this.contentPanel.clearChildren();
            content.setStyle(s -> s.size = TaffySize.all(TaffyDimension.percent(1f)));
            this.contentPanel.addChild(content);
            return this;
        }

        public SubWindow setWindowSize(float width, float height) {
            var newWidth = Math.max(this.minWidth, width);
            var newHeight = Math.max(this.minHeight, height);
            this.outerWidth = newWidth;
            this.outerHeight = newHeight;
            this.setStyle(s -> s.size = TaffySize.of(TaffyDimension.length(newWidth), TaffyDimension.length(newHeight)));
            this.markDirty();
            return this;
        }

        public SubWindow setMinWindowSize(float width, float height) {
            this.minWidth = width;
            this.minHeight = height;
            return this.setWindowSize(this.outerWidth, this.outerHeight);
        }

        public SubWindow setAutoHeight(boolean autoHeight) {
            this.autoHeight = autoHeight;
            if (autoHeight) {
                this.contentPanel.inlineStyle(CONTENT_PANEL_CSS_AUTO_HEIGHT);
            } else {
                this.contentPanel.inlineStyle(CONTENT_PANEL_CSS);
            }
            this.markDirty();
            return this;
        }

        public boolean isAutoHeight() {
            return this.autoHeight;
        }

        @Override
        public void resize(float offsetX, float offsetY) {
            super.resize(offsetX, offsetY);
            if (this.autoHeight) {
                this.autoSizeHeight();
            }
        }

        private void autoSizeHeight() {
            var contentLayout = this.contentPanel.getLayout();
            if (contentLayout == null) {
                return;
            }
            var style = this.getStyle();
            float minH = style.minSize.height.isLength() ? style.minSize.height.getValue() : MIN_WINDOW_HEIGHT;
            float maxH = style.maxSize.height.isLength() ? style.maxSize.height.getValue() : Float.MAX_VALUE;
            float contentHeight = contentLayout.size().getHeight();
            float neededHeight = TOP_BAR_HEIGHT + contentHeight;
            float newHeight = Math.clamp(neededHeight, minH, maxH);
            if (Math.abs(newHeight - this.outerHeight) > 0.5f) {
                this.setWindowSize(this.outerWidth, newHeight);
            }
        }

        public void close() {
            this.closed = true;
            this.enabled = false;
            if (WindowedContainer.this.activeMouseWindow == this) {
                WindowedContainer.this.activeMouseWindow = null;
            }
            if (!WindowedContainer.this.dispatchingMouseClick) {
                WindowedContainer.this.removeChild(this);
            }
        }

        @Override
        public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (!this.enabled || !this.visible() || !this.isMouseOver(event.x(), event.y())) {
                return false;
            }
            if (event.button() == 0 && this.resizable) {
                var mode = this.findResizeMode(event.x(), event.y());
                if (mode != RESIZE_NONE) {
                    this.resizeMode = mode;
                    return true;
                }
            }
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
            if (!this.enabled || !this.visible()) {
                return false;
            }
            if (this.resizeMode != RESIZE_NONE && event.button() == 0) {
                this.resizeBy((float) dx, (float) dy);
                return true;
            }
            if (this.moving && event.button() == 0) {
                this.setAbsoluteX(this.absoluteX + (float) dx);
                this.setAbsoluteY(this.absoluteY + (float) dy);
                this.clampTopBarInParent();
                this.markDirty();
                return true;
            }
            return super.mouseDragged(event, dx, dy);
        }

        @Override
        public boolean mouseReleased(IMouseButtonEvent event) {
            if (this.resizeMode != RESIZE_NONE || this.moving) {
                this.resizeMode = RESIZE_NONE;
                this.moving = false;
                return true;
            }
            return super.mouseReleased(event);
        }

        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (event.button() == 0 && this.topBar.isMouseOver(event.x(), event.y()) && !this.closeButton.isMouseOver(event.x(), event.y())) {
                this.moving = true;
                return true;
            }
            return super.onMouseClicked(event, doubleClick);
        }

        @Override
        public void onFocusChanged(boolean focused) {
            if (!focused) {
                this.resizeMode = RESIZE_NONE;
                this.moving = false;
            }
            super.onFocusChanged(focused);
        }

        private int findResizeMode(double mouseX, double mouseY) {
            if (!this.resizable) {
                return RESIZE_NONE;
            }
            var mode = RESIZE_NONE;
            if (mouseX - this.x <= BORDER_HIT_SIZE) {
                mode |= RESIZE_LEFT;
            }
            if (this.getMaxX() - mouseX <= BORDER_HIT_SIZE) {
                mode |= RESIZE_RIGHT;
            }
            if (mouseY - this.y <= BORDER_HIT_SIZE) {
                mode |= RESIZE_TOP;
            }
            if (this.getMaxY() - mouseY <= BORDER_HIT_SIZE) {
                mode |= RESIZE_BOTTOM;
            }
            return mode;
        }

        private void resizeBy(float dx, float dy) {
            var newX = this.absoluteX;
            var newY = this.absoluteY;
            var newWidth = this.outerWidth;
            var newHeight = this.outerHeight;
            var parentWidth = WindowedContainer.this.getWidth();

            if ((this.resizeMode & RESIZE_LEFT) != 0) {
                var clamped = Math.max(-this.absoluteX, Math.min(this.outerWidth - this.minWidth, dx));
                newX = this.absoluteX + clamped;
                newWidth = this.outerWidth - clamped;
            }
            if ((this.resizeMode & RESIZE_RIGHT) != 0) {
                var lower = -(this.outerWidth - this.minWidth);
                var upper = parentWidth > 0 ? Math.max(0f, parentWidth - this.absoluteX - this.outerWidth) : Float.MAX_VALUE;
                var clamped = Math.max(lower, Math.min(upper, dx));
                newWidth = this.outerWidth + clamped;
            }
            if ((this.resizeMode & RESIZE_TOP) != 0) {
                var clamped = Math.max(-this.absoluteY, Math.min(this.outerHeight - this.minHeight, dy));
                newY = this.absoluteY + clamped;
                newHeight = this.outerHeight - clamped;
            }
            if ((this.resizeMode & RESIZE_BOTTOM) != 0) {
                var lower = -(this.outerHeight - this.minHeight);
                var clamped = Math.max(lower, dy);
                newHeight = this.outerHeight + clamped;
            }

            this.setAbsoluteX(newX);
            this.setAbsoluteY(newY);
            this.setWindowSize(newWidth, newHeight);
            this.clampTopBarInParent();
        }

        private boolean clampTopBarInParent() {
            var parentWidth = WindowedContainer.this.getWidth();
            var parentHeight = WindowedContainer.this.getHeight();
            if (parentWidth <= 0 || parentHeight <= 0) {
                return false;
            }
            var maxX = Math.max(0, parentWidth - this.outerWidth);
            var maxY = Math.max(0, parentHeight - TOP_BAR_HEIGHT);
            var newX = Math.clamp(this.absoluteX, 0, maxX);
            var newY = Math.clamp(this.absoluteY, 0, maxY);
            if (newX == this.absoluteX && newY == this.absoluteY) {
                return false;
            }
            this.setAbsoluteX(newX);
            this.setAbsoluteY(newY);
            return true;
        }
    }
}
