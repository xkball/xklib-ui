package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.layout.DefaultStyles;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffyRect;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.CalcExpression;
import dev.vfyjxf.taffy.style.LengthPercentageAuto;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class TabContainer extends ContainerWidget {

    private static final int TAB_BAR_HEIGHT = 32;
    private static final String SELF_CSS = """
            * {
                display: grid;
                size: 100% 100%;
                grid-template-columns: 1fr;
                grid-template-rows: 32 1fr;
                align-items: stretch;
                justify-content: stretch;
            }
            """;

    protected IntLayoutVariable selected = new IntLayoutVariable(0);
    protected final List<TabPage> tabs = new ArrayList<>();
    protected final ContainerWidget tabBar = new ContainerWidget();
    protected final ContainerWidget contentPanel = new ContainerWidget();

    public TabContainer() {
        super();
        this.selected.addCallback(this::onSelectedChanged);
    }

    @Override
    public String createCSSAsSelf() {
        return super.createCSSAsSelf() + SELF_CSS;
    }

    public TabContainer addTabPage(Widget widget, String title, int order) {
        var page = new TabPage(order, title, widget.style.display == TaffyDisplay.NONE ? TaffyDisplay.FLEX : widget.style.display, widget);
        tabs.add(page);
        tabs.sort(Comparator.comparingInt(TabPage::index));

        var btn = new Button(title, () -> selected.set(order));
        btn.setTextScale(TextScale.EXPAND_WIDTH);
        btn.addDecoration(ButtonLooks.transparent());
        btn.style.margin = TaffyRect.all(LengthPercentageAuto.length(4));
        tabBar.addChild(btn);
        if (tabs.size() > 1) {
            widget.setStyle(s -> s.display = TaffyDisplay.NONE);
        }
        contentPanel.addChild(widget);

        return this;
    }

    public TabContainer addTabPage(Widget widget, String title) {
        return addTabPage(widget, title, tabs.size());
    }

    private void onSelectedChanged(int newSelected) {
        for (int i = 0; i < tabs.size(); i++) {
            var tab = tabs.get(i);
            var widget = tab.widget();
            if (i == newSelected) {
                if(widget.style.display == TaffyDisplay.NONE) widget.setStyle(s -> s.display = tab.rawDisplay());
            } else {
                if(widget.style.display != TaffyDisplay.NONE) tab.rawDisplay = widget.style.display;
                widget.setStyle(s -> s.display = TaffyDisplay.NONE);
            }
        }
    }
    
    //调用太早 子组件还没有遍历就调用了
    @Override
    public void onStyleSheetChanged() {
        this.onSelectedChanged(this.selected.get());
    }
    
    @Override
    public void init() {
        super.init();
        
        tabBar.applyStyle(DefaultStyles::flexCenteredRow);
        contentPanel.setStyle(s -> s.size = new TaffySize<>(s.size.width, TaffyDimension.calc(CalcExpression.percentMinusLength(1,TAB_BAR_HEIGHT))));
        this.addChild(tabBar);
        this.addChild(contentPanel);
    }

    public int getSelected() {
        return selected.get();
    }

    public void setSelected(int index) {
        selected.set(index);
    }

    public List<TabPage> getTabs() {
        return tabs;
    }
    
    public ContainerWidget getTabBar(){
        return tabBar;
    }
    
    public static final class TabPage {
        private final int index;
        private final String title;
        private TaffyDisplay rawDisplay;
        private final Widget widget;
        
        public TabPage(int index, String title, TaffyDisplay rawDisplay, Widget widget) {
            this.index = index;
            this.title = title;
            this.rawDisplay = rawDisplay;
            this.widget = widget;
        }
        
        public int index() {
            return index;
        }
        
        public String title() {
            return title;
        }
        
        public TaffyDisplay rawDisplay() {
            return rawDisplay;
        }
        
        public Widget widget() {
            return widget;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TabPage) obj;
            return this.index == that.index &&
                    Objects.equals(this.title, that.title) &&
                    Objects.equals(this.rawDisplay, that.rawDisplay) &&
                    Objects.equals(this.widget, that.widget);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(index, title, rawDisplay, widget);
        }
        
        @Override
        public String toString() {
            return "TabPage[" +
                    "index=" + index + ", " +
                    "title=" + title + ", " +
                    "rawDisplay=" + rawDisplay + ", " +
                    "widget=" + widget + ']';
        }
        
    }
    
}
