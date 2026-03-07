package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TrackSizingFunction;

import java.util.List;

public class SplitContainerTest {

    private static ContainerWidget colorPanel(int color) {
        var w = new ContainerWidget();
        w.addDecoration(new Background(color));
        return w;
    }

    private static Label label(String text, int bg) {
        var l = new Label(text);
        l.addDecoration(new Background(bg));
        return l;
    }

    public static void horizontalSplitTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new SplitContainer(false);
            root.asTreeRoot();

            var left = colorPanel(0xFF1A6B5A);
            var leftStyle = new TaffyStyle();
            leftStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setFirst(left, leftStyle);

            var right = colorPanel(0xFF6B3A1A);
            var rightStyle = new TaffyStyle();
            rightStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setSecond(right, rightStyle);

            return root;
        })) {
            frame.run();
        }
    }

    public static void verticalSplitTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new SplitContainer(true);
            root.asTreeRoot();

            var top = colorPanel(0xFF1A3B6B);
            var topStyle = new TaffyStyle();
            topStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setFirst(top, topStyle);

            var bottom = colorPanel(0xFF6B1A5A);
            var bottomStyle = new TaffyStyle();
            bottomStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setSecond(bottom, bottomStyle);

            return root;
        })) {
            frame.run();
        }
    }

    public static void splitWithFlexContentTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new SplitContainer(false);
            root.asTreeRoot();

            var leftPanel = colorPanel(0xFF1A1A2E);
            var leftStyle = new TaffyStyle();
            leftStyle.display = TaffyDisplay.FLEX;
            leftStyle.flexDirection = FlexDirection.COLUMN;
            leftStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            leftStyle.gap = TaffySize.all(LengthPercentage.length(4));
            leftPanel.setStyle(leftStyle);

            for (int i = 0; i < 5; i++) {
                var item = new Button("左 " + i, () -> {});
                item.addDecoration(new Background(0xFF2D3748));
                var itemStyle = new TaffyStyle();
                itemStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(40));
                itemStyle.flexShrink = 0;
                leftPanel.addChild(item, itemStyle);
            }
            root.setFirst(leftPanel, leftStyle);

            var rightPanel = colorPanel(0xFF2E1A2E);
            var rightStyle = new TaffyStyle();
            rightStyle.display = TaffyDisplay.FLEX;
            rightStyle.flexDirection = FlexDirection.COLUMN;
            rightStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            rightStyle.gap = TaffySize.all(LengthPercentage.length(4));
            rightPanel.setStyle(rightStyle);

            for (int i = 0; i < 5; i++) {
                var item = label("右 " + i, 0xFF4A3060);
                var itemStyle = new TaffyStyle();
                itemStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(40));
                itemStyle.flexShrink = 0;
                rightPanel.addChild(item, itemStyle);
            }
            root.setSecond(rightPanel, rightStyle);

            return root;
        })) {
            frame.run();
        }
    }

    public static void splitWithGridContentTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new SplitContainer(true);
            root.asTreeRoot();

            var topPanel = colorPanel(0xFF0F1B2E);
            var topStyle = new TaffyStyle();
            topStyle.display = TaffyDisplay.GRID;
            topStyle.gridTemplateColumns = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );
            topStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            topStyle.gap = TaffySize.all(LengthPercentage.length(4));
            topPanel.setStyle(topStyle);

            int[] colors = {0xFFE94560, 0xFF0F3460, 0xFF533483, 0xFF2EC4B6, 0xFFFF6B6B, 0xFF4ECDC4};
            for (int i = 0; i < 6; i++) {
                var cell = label("格 " + i, colors[i % colors.length]);
                var cellStyle = new TaffyStyle();
                topPanel.addChild(cell, cellStyle);
            }
            root.setFirst(topPanel, topStyle);

            var bottomPanel = colorPanel(0xFF2E2E0F);
            var bottomStyle = new TaffyStyle();
            bottomStyle.display = TaffyDisplay.GRID;
            bottomStyle.gridTemplateColumns = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );
            bottomStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            bottomStyle.gap = TaffySize.all(LengthPercentage.length(4));
            bottomPanel.setStyle(bottomStyle);

            for (int i = 0; i < 4; i++) {
                var cell = label("底 " + i, colors[(i + 3) % colors.length]);
                var cellStyle = new TaffyStyle();
                bottomPanel.addChild(cell, cellStyle);
            }
            root.setSecond(bottomPanel, bottomStyle);

            return root;
        })) {
            frame.run();
        }
    }

    public static void nestedSplitTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var outer = new SplitContainer(false);
            outer.asTreeRoot();

            var left = colorPanel(0xFF1A2E1A);
            var leftStyle = new TaffyStyle();
            leftStyle.size = TaffySize.all(TaffyDimension.percent(1f));
            outer.setFirst(left, leftStyle);
            var innerSplit = new SplitContainer(true);
            

            var innerTop = colorPanel(0xFF2E1A1A);
            var innerTopStyle = new TaffyStyle();
            innerSplit.setFirst(innerTop, innerTopStyle);

            var innerBottom = colorPanel(0xFF1A1A2E);
            var innerBottomStyle = new TaffyStyle();
            innerSplit.setSecond(innerBottom, innerBottomStyle);

            outer.setSecond(innerSplit);

            return outer;
        })) {
            frame.run();
        }
    }

    public static void main(String[] args) throws Exception {
        nestedSplitTest();
    }
}


