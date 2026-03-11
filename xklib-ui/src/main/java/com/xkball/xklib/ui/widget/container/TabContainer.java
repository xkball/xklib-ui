package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklib.ui.layout.TextScale;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffyRect;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.LengthPercentageAuto;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TrackSizingFunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TabContainer extends ContainerWidget {

    private static final int TAB_BAR_HEIGHT = 32;

    protected IntLayoutVariable selected = new IntLayoutVariable(0);
    protected final List<TabPage> tabs = new ArrayList<>();
    protected final ContainerWidget tabBar = new ContainerWidget(){
        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            super.doRender(graphics, mouseX, mouseY, a);
        }
    };
    protected final ContainerWidget contentPanel = new ContainerWidget();

    public TabContainer() {
        super();
        this.selected.addCallback(this::onSelectedChanged);
    }

    public TabContainer addTabPage(Widget widget, String title, int order) {
        var page = new TabPage(order, title, widget.style.display == TaffyDisplay.NONE ? TaffyDisplay.FLEX : widget.style.display, widget);
        tabs.add(page);
        tabs.sort(Comparator.comparingInt(TabPage::index));

        var btn = new Button(title, () -> selected.set(order));
        btn.setTextScale(TextScale.EXPAND_WIDTH);
        btn.addDecoration(ButtonLooks.transparent());
        btn.setColor(0xffffffff);
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

        this.style.display = TaffyDisplay.GRID;
        this.style.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        this.style.gridTemplateColumns = List.of(TrackSizingFunction.fr(1f));
        this.style.gridTemplateRows = List.of(
                TrackSizingFunction.fixed(LengthPercentage.length(TAB_BAR_HEIGHT)),
                TrackSizingFunction.fr(1f)
        );
        this.style.alignItems = AlignItems.STRETCH;
        this.style.justifyContent = AlignContent.STRETCH;

        var tabBarStyle = new TaffyStyle();
        tabBarStyle.display = TaffyDisplay.FLEX;
        tabBarStyle.alignItems = AlignItems.STRETCH;
        tabBarStyle.justifyContent = AlignContent.STRETCH;
        tabBar.setStyle(tabBarStyle);

        this.addChild(tabBar, tabBarStyle);
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

    public record TabPage(int index, String title, TaffyDisplay rawDisplay, Widget widget) {
    }
    
}
