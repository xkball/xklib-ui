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
                widget.setStyle(s -> s.display = tab.rawDisplay());
            } else {
                widget.setStyle(s -> s.display = TaffyDisplay.NONE);
            }
        }
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

    public record TabPage(int index, String title, TaffyDisplay rawDisplay, Widget widget) {
    }
    
}
