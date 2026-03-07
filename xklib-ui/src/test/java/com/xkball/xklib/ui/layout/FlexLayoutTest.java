package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.ContainerWidget;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;

public class FlexLayoutTest {

    private static TaffyStyle flexStyle(FlexDirection dir, AlignContent justify, AlignItems align) {
        var s = new TaffyStyle();
        s.flexDirection = dir;
        s.justifyContent = justify;
        s.alignItems = align;
        s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        return s;
    }

    private static ContainerWidget bgContainer(int color) {
        var w = new ContainerWidget();
        w.addDecoration(new Background(color));
        return w;
    }

    private static Widget colorBlock(int color, float w, float h) {
        var s = new TaffyStyle();
        s.size = new TaffySize<>(TaffyDimension.length(w), TaffyDimension.length(h));
        var widget = new Widget();
        widget.addDecoration(new Background(color));
        widget.getStyle().size = s.size;
        return widget;
    }

    public static void rowStartTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(flexStyle(FlexDirection.ROW, AlignContent.START, AlignItems.START));
            root.addChild(colorBlock(0xFFE94560, 80, 40));
            root.addChild(colorBlock(0xFF0F3460, 80, 60));
            root.addChild(colorBlock(0xFF533483, 80, 50));
            return root;
        })) {
            frame.run();
        }
    }

    public static void rowCenterTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(flexStyle(FlexDirection.ROW, AlignContent.CENTER, AlignItems.CENTER));
            root.setStyle(s -> s.gap = TaffySize.all(LengthPercentage.length(2)));
            root.addChild(colorBlock(0xFFE94560, 80, 40));
            root.addChild(colorBlock(0xFF0F3460, 80, 60));
            root.addChild(colorBlock(0xFF533483, 80, 50));
            return root;
        })) {
            frame.run();
        }
    }

    public static void rowSpaceBetweenTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(flexStyle(FlexDirection.ROW, AlignContent.SPACE_BETWEEN, AlignItems.CENTER));
            root.addChild(colorBlock(0xFFE94560, 80, 40));
            root.addChild(colorBlock(0xFF0F3460, 80, 60));
            root.addChild(colorBlock(0xFF533483, 80, 50));
            return root;
        })) {
            frame.run();
        }
    }

    public static void rowSpaceAroundTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(flexStyle(FlexDirection.ROW, AlignContent.SPACE_AROUND, AlignItems.CENTER));
            root.addChild(colorBlock(0xFFE94560, 80, 40));
            root.addChild(colorBlock(0xFF0F3460, 80, 60));
            root.addChild(colorBlock(0xFF533483, 80, 50));
            return root;
        })) {
            frame.run();
        }
    }

    public static void colStartTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(flexStyle(FlexDirection.COLUMN, AlignContent.START, AlignItems.START));
            root.addChild(colorBlock(0xFFE94560, 200, 50));
            root.addChild(colorBlock(0xFF0F3460, 300, 50));
            root.addChild(colorBlock(0xFF533483, 150, 50));
            return root;
        })) {
            frame.run();
        }
    }

    public static void colEndTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(flexStyle(FlexDirection.COLUMN, AlignContent.END, AlignItems.END));
            root.addChild(colorBlock(0xFFE94560, 200, 50));
            root.addChild(colorBlock(0xFF0F3460, 300, 50));
            root.addChild(colorBlock(0xFF533483, 150, 50));
            return root;
        })) {
            frame.run();
        }
    }

    public static void flexGrowTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.ROW;
            rootStyle.alignItems = AlignItems.STRETCH;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var s1 = new TaffyStyle();
            s1.flexGrow = 1f;
            var w1 = new Widget();
            w1.setStyle(s1);
            w1.addDecoration(new Background(0xFFE94560));
            root.addChild(w1);

            var s2 = new TaffyStyle();
            s2.flexGrow = 2f;
            var w2 = new Widget();
            w2.setStyle(s2);
            w2.addDecoration(new Background(0xFF0F3460));
            root.addChild(w2);

            var s3 = new TaffyStyle();
            s3.flexGrow = 1f;
            var w3 = new Widget();
            w3.setStyle(s3);
            w3.addDecoration(new Background(0xFF533483));
            root.addChild(w3);

            return root;
        })) {
            frame.run();
        }
    }

    public static void flexWithLabelAndButtonTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF2D3748);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.COLUMN;
            rootStyle.justifyContent = AlignContent.CENTER;
            rootStyle.alignItems = AlignItems.CENTER;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            rootStyle.gap = TaffySize.all(LengthPercentage.length(2));
            root.setStyle(rootStyle);

            for (int i = 0; i < 5; i++) {
                int idx = i;
                var itemStyle = new TaffyStyle();
                itemStyle.size = new TaffySize<>(TaffyDimension.length(300), TaffyDimension.length(40));
                var btn = new Button("按钮 " + idx, () -> System.out.println("点击了按钮 " + idx));
                btn.setStyle(itemStyle);
                btn.addDecoration(ButtonLooks.roundRect());
                root.addChild(btn);
            }

            return root;
        })) {
            frame.run();
        }
    }

    public static void rowReverseTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(flexStyle(FlexDirection.ROW_REVERSE, AlignContent.START, AlignItems.CENTER));
            int[] colors = {0xFFE94560, 0xFF0F3460, 0xFF533483, 0xFF2EC4B6};
            for (int i = 0; i < colors.length; i++) {
                var cell = new ContainerWidget();
                root.addChild(cell);
                cell.addDecoration(new Background(colors[i]));
                var s = new TaffyStyle();
                s.size = new TaffySize<>(TaffyDimension.length(60), TaffyDimension.length(60));
                s.flexDirection = FlexDirection.ROW;
                s.justifyContent = AlignContent.CENTER;
                s.alignItems = AlignItems.CENTER;
                cell.setStyle(s);
                
                var labelStyle = new TaffyStyle();
                labelStyle.size = new TaffySize<>(TaffyDimension.stretch(), TaffyDimension.length(50));
                var label = new Label("" + (i + 1), TextAlign.CENTER);
                cell.addChild(label,labelStyle);
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void main(String[] args) throws Exception {
        flexWithLabelAndButtonTest();
    }
}

