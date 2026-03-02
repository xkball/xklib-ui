package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.widgets.Button;

public class SectionBox extends GridLayout {

    private static final int DEFAULT_HEADER_HEIGHT = 28;
    private static final int BG_COLOR = 0xFFE2E8F0;
    private static final int HEADER_COLOR = 0xFFCBD5E1;
    private static final int BORDER_COLOR = 0xFF94A3B8;

    private final String title;
    private final FlexLayout content;
    private final int headerHeight;

    private boolean expanded = false;
    private int lastContentHeight = -1;

    public SectionBox(String title) {
        this(title, DEFAULT_HEADER_HEIGHT);
    }

    public SectionBox(String title, int headerHeight) {
        this(title, headerHeight, new FlexParam.Builder()
                .direction(FlexParam.Direction.COL)
                .justify(FlexParam.Align.START)
                .align(FlexParam.Align.START)
                .overflow(true)
                .build());
    }

    public SectionBox(String title, FlexParam contentParam) {
        this(title, DEFAULT_HEADER_HEIGHT, contentParam);
    }

    public SectionBox(String title, int headerHeight, FlexParam contentParam) {
        super();
        this.title = title;
        this.headerHeight = headerHeight;
        this.content = new FlexLayout(contentParam) {
            @Override
            public void onActualSizeChanged() {
                SectionBox.this.markDirty();
            }
        };
    }

    public SectionBox addContent(AbstractWidget widget, FlexElementParam param) {
        this.content.addChild(widget, param);
        return this;
    }

    public FlexLayout getContent() {
        return this.content;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    @Override
    public void init() {
        super.init();
        this.rebuild();
    }

    private void rebuild() {
        this.children.clear();

        if (this.expanded) {
            this.setGridParam(new GridParam.Builder()
                    .addCol("1")
                    .addRow(headerHeight + "px")
                    .addRow("1")
                    .build());
        } else {
            this.setGridParam(new GridParam.Builder()
                    .addCol("1")
                    .addRow(headerHeight + "px")
                    .build());
        }

        var headerBtn = new Button((this.expanded ? "▼ " : "▶ ") + this.title, this::toggle, Button.DefaultButtonStyles.TRANSPARENT);
        super.addChild(headerBtn, new GridElementParam(0, 0, 1, 1));

        if (this.expanded) {
            this.content.init();
            super.addChild(this.content, new GridElementParam(1, 0, 1, 1));
        }

        this.markDirty();
    }

    private void toggle() {
        this.expanded = !this.expanded;
        this.submitTreeUpdate(this::rebuild);
    }

    @Override
    public void resize() {
        super.resize();
        if (!this.expanded) {
            this.lastContentHeight = -1;
            this.updateSelfHeight(headerHeight);
            return;
        }
        int contentH = this.content.actualHeight;
        if (contentH <= 0) {
            return;
        }
        int total = headerHeight + contentH;
        if (total != this.lastContentHeight) {
            this.lastContentHeight = total;
            this.submitTreeUpdate(() -> this.updateSelfHeight(total));
        }
    }

    private void updateSelfHeight(int height) {
        if(this.contentHeight == height){
            return;
        }
        this.tryUpdateSelfLayout(old -> {
            if (old instanceof FlexElementParam fep) {
                return new FlexElementParam(fep.order, fep.width, new SizeParam.Pixel(height));
            }
            return old;
        });
    }

    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, BG_COLOR);
        graphics.fill(this.x, this.y, this.x + this.width, this.y + headerHeight, HEADER_COLOR);
        graphics.renderOutline(this.x, this.y, this.width, this.height, BORDER_COLOR);
        super.render(graphics, mouseX, mouseY, a);
    }
}
