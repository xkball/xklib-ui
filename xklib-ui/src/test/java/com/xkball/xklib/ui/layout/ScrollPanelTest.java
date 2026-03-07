package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.ContainerWidget;
import com.xkball.xklib.ui.widget.Label;
import dev.vfyjxf.taffy.geometry.TaffyPoint;
import dev.vfyjxf.taffy.geometry.TaffyRect;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.Overflow;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;
import dev.vfyjxf.taffy.style.TrackSizingFunction;

import java.util.List;

public class ScrollPanelTest {

    private static final int[] COLORS = {
            0xFFE94560, 0xFF0F3460, 0xFF533483, 0xFF2EC4B6,
            0xFFFF6B6B, 0xFF4ECDC4, 0xFF45B7D1, 0xFF96CEB4,
            0xFFFECEA8, 0xFF88D8B0, 0xFFDDA0DD, 0xFF98FB98
    };

    private static ContainerWidget bgContainer(int color) {
        var w = new ContainerWidget();
        w.addDecoration(new Background(color));
        return w;
    }

    private static Label colorLabel(int color, String text, float h) {
        var label = new Label(text, TextAlign.CENTER, 0xFFFFFFFF);
        label.addDecoration(new Background(color));
        label.getStyle().size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.length(h));
        label.getStyle().flexShrink = 0;
        return label;
    }

    public static void scrollableFlexColTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.ROW;
            rootStyle.justifyContent = AlignContent.CENTER;
            rootStyle.alignItems = AlignItems.CENTER;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var scrollContainer = new ContainerWidget();
            scrollContainer.addDecoration(new Background(0xFF2D3748));
            var scrollStyle = new TaffyStyle();
            scrollStyle.flexDirection = FlexDirection.COLUMN;
            scrollStyle.justifyContent = AlignContent.START;
            scrollStyle.alignItems = AlignItems.CENTER;
            scrollStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.percent(0.98f));
            scrollStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
            scrollStyle.gap = TaffySize.of(LengthPercentage.length(0), LengthPercentage.length(4));
            scrollStyle.scrollbarWidth = 8;
            scrollContainer.setStyle(scrollStyle);
            root.addChild(scrollContainer);

            for (int i = 0; i < 25; i++) {
                var label = colorLabel(COLORS[i % COLORS.length], "行 " + (i + 1), 40);
                label.getStyle().size = new TaffySize<>(TaffyDimension.percent(0.98f), TaffyDimension.length(40));
                scrollContainer.addChild(label);
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void scrollableGridTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.ROW;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var scrollContainer = new ContainerWidget();
            scrollContainer.addDecoration(new Background(0xFF2D3748));
            var scrollStyle = new TaffyStyle();
            scrollStyle.display = TaffyDisplay.GRID;
            scrollStyle.gridTemplateColumns = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );
            scrollStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            scrollStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
            scrollStyle.scrollbarWidth = 8;
            scrollStyle.gap = TaffySize.all(LengthPercentage.length(4));
            scrollContainer.setStyle(scrollStyle);
            root.addChild(scrollContainer);

            for (int i = 0; i < 30; i++) {
                var itemStyle = new TaffyStyle();
                itemStyle.size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.length(60));
                itemStyle.flexShrink = 0;
                var cell = new Label("Grid格 " + (i + 1), TextAlign.CENTER, 0xFFFFFFFF);
                cell.addDecoration(new Background(COLORS[i % COLORS.length]));
                cell.setStyle(itemStyle);
                scrollContainer.addChild(cell);
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void nestedScrollTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF0D0D0D);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.COLUMN;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var outerScroll = new ContainerWidget();
            outerScroll.addDecoration(new Background(0xFF1A1A2E));
            var outerStyle = new TaffyStyle();
            outerStyle.flexDirection = FlexDirection.ROW;
            outerStyle.justifyContent = AlignContent.START;
            outerStyle.alignItems = AlignItems.STRETCH;
            outerStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            outerStyle.overflow = new TaffyPoint<>(Overflow.SCROLL, Overflow.VISIBLE);
            outerStyle.scrollbarWidth = 8;
            outerStyle.gap = TaffySize.all(LengthPercentage.length(8));
            outerStyle.padding = TaffyRect.all(LengthPercentage.length(8));
            outerScroll.setStyle(outerStyle);
            root.addChild(outerScroll);

            int[] panelColors = {0xFF2D3748, 0xFF1A3A2A, 0xFF3A1A2A, 0xFF1A2A3A, 0xFF2A3A1A};
            int[] itemColors = {0xFFE94560, 0xFF0F3460, 0xFF533483, 0xFF2EC4B6, 0xFFFF6B6B};

            for (int p = 0; p < 5; p++) {
                var innerScroll = new ContainerWidget();
                innerScroll.addDecoration(new Background(panelColors[p]));
                var innerStyle = new TaffyStyle();
                innerStyle.flexDirection = FlexDirection.COLUMN;
                innerStyle.justifyContent = AlignContent.START;
                innerStyle.alignItems = AlignItems.STRETCH;
                innerStyle.size = new TaffySize<>(TaffyDimension.length(200), TaffyDimension.percent(1f));
                innerStyle.flexShrink = 0;
                innerStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
                innerStyle.scrollbarWidth = 8;
                innerStyle.gap = TaffySize.all(LengthPercentage.length(4));
                innerStyle.padding = TaffyRect.all(LengthPercentage.length(4));
                innerScroll.setStyle(innerStyle);
                outerScroll.addChild(innerScroll);

                for (int i = 0; i < 15; i++) {
                    var label = new Label("面板" + (p + 1) + "-项目" + (i + 1), TextAlign.CENTER, 0xFFFFFFFF);
                    label.addDecoration(new Background(itemColors[(p + i) % itemColors.length]));
                    var itemStyle = new TaffyStyle();
                    itemStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(44));
                    itemStyle.flexShrink = 0;
                    label.setStyle(itemStyle);
                    innerScroll.addChild(label);
                }
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void scrollableFlexRowTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.COLUMN;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var scrollContainer = new ContainerWidget();
            scrollContainer.addDecoration(new Background(0xFF2D3748));
            var scrollStyle = new TaffyStyle();
            scrollStyle.flexDirection = FlexDirection.ROW;
            scrollStyle.justifyContent = AlignContent.START;
            scrollStyle.alignItems = AlignItems.CENTER;
            scrollStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            scrollStyle.overflow = new TaffyPoint<>(Overflow.SCROLL, Overflow.VISIBLE);
            scrollStyle.scrollbarWidth = 8;
            scrollStyle.gap = TaffySize.all(LengthPercentage.length(8));
            scrollStyle.padding = TaffyRect.all(LengthPercentage.length(8));
            scrollContainer.setStyle(scrollStyle);
            root.addChild(scrollContainer);

            for (int i = 0; i < 25; i++) {
                var itemStyle = new TaffyStyle();
                itemStyle.size = new TaffySize<>(TaffyDimension.length(120), TaffyDimension.length(80));
                itemStyle.flexShrink = 0;
                var btn = new Button("按钮 " + (i + 1), () -> System.out.println("clicked"));
                btn.setStyle(itemStyle);
                btn.addDecoration(ButtonLooks.roundRect(COLORS[i % COLORS.length], 0xFF0c4a6e));
                scrollContainer.addChild(btn);
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void verticalInVerticalTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF0D0D0D);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.ROW;
            rootStyle.alignItems = AlignItems.STRETCH;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var outerFlex = new ContainerWidget();
            outerFlex.addDecoration(new Background(0xFF1A1A2E));
            var outerFlexStyle = new TaffyStyle();
            outerFlexStyle.flexDirection = FlexDirection.COLUMN;
            outerFlexStyle.justifyContent = AlignContent.START;
            outerFlexStyle.alignItems = AlignItems.STRETCH;
            outerFlexStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.percent(1f));
            outerFlexStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
            outerFlexStyle.scrollbarWidth = 8;
            outerFlexStyle.gap = TaffySize.all(LengthPercentage.length(6));
            outerFlexStyle.padding = TaffyRect.all(LengthPercentage.length(6));
            outerFlex.setStyle(outerFlexStyle);
            root.addChild(outerFlex);

            for (int p = 0; p < 4; p++) {
                var innerFlex = new ContainerWidget();
                innerFlex.addDecoration(new Background(0xFF2D3748));
                var innerStyle = new TaffyStyle();
                innerStyle.flexDirection = FlexDirection.COLUMN;
                innerStyle.justifyContent = AlignContent.START;
                innerStyle.alignItems = AlignItems.STRETCH;
                innerStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(200));
                innerStyle.flexShrink = 0;
                innerStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
                innerStyle.scrollbarWidth = 8;
                innerStyle.gap = TaffySize.all(LengthPercentage.length(3));
                innerFlex.setStyle(innerStyle);
                outerFlex.addChild(innerFlex);

                for (int i = 0; i < 10; i++) {
                    var label = colorLabel(COLORS[(p * 3 + i) % COLORS.length], "外" + (p + 1) + "-Flex行" + (i + 1), 32);
                    label.getStyle().size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(32));
                    innerFlex.addChild(label);
                }
            }

            var outerGrid = new ContainerWidget();
            outerGrid.addDecoration(new Background(0xFF1A2E1A));
            var outerGridStyle = new TaffyStyle();
            outerGridStyle.flexDirection = FlexDirection.COLUMN;
            outerGridStyle.justifyContent = AlignContent.START;
            outerGridStyle.alignItems = AlignItems.STRETCH;
            outerGridStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.percent(1f));
            outerGridStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
            outerGridStyle.scrollbarWidth = 8;
            outerGridStyle.gap = TaffySize.all(LengthPercentage.length(6));
            outerGridStyle.padding = TaffyRect.all(LengthPercentage.length(6));
            outerGrid.setStyle(outerGridStyle);
            root.addChild(outerGrid);

            for (int p = 0; p < 4; p++) {
                var innerGrid = new ContainerWidget();
                innerGrid.addDecoration(new Background(0xFF2E3D2E));
                var innerStyle = new TaffyStyle();
                innerStyle.display = TaffyDisplay.GRID;
                innerStyle.gridTemplateColumns = List.of(
                        TrackSizingFunction.fr(1f),
                        TrackSizingFunction.fr(1f)
                );
                innerStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(200));
                innerStyle.flexShrink = 0;
                innerStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
                innerStyle.scrollbarWidth = 8;
                innerStyle.gap = TaffySize.all(LengthPercentage.length(3));
                innerGrid.setStyle(innerStyle);
                outerGrid.addChild(innerGrid);

                for (int i = 0; i < 12; i++) {
                    var itemStyle = new TaffyStyle();
                    itemStyle.size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.length(40));
                    itemStyle.flexShrink = 0;
                    var label = new Label("外" + (p + 1) + "-Grid" + (i + 1), TextAlign.CENTER, 0xFFFFFFFF);
                    label.addDecoration(new Background(COLORS[(p * 4 + i) % COLORS.length]));
                    label.setStyle(itemStyle);
                    innerGrid.addChild(label);
                }
            }

            return root;
        })) {
            frame.run();
        }
    }

    public static void tripleNestedScrollTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF050505);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.COLUMN;
            rootStyle.justifyContent = AlignContent.START;
            rootStyle.alignItems = AlignItems.STRETCH;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            rootStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
            rootStyle.scrollbarWidth = 8;
            rootStyle.gap = TaffySize.all(LengthPercentage.length(8));
            rootStyle.padding = TaffyRect.all(LengthPercentage.length(8));
            root.setStyle(rootStyle);

            int[] lvl1Colors = {0xFF1A1A2E, 0xFF1A2E1A, 0xFF2E1A1A};
            int[] lvl2Colors = {0xFF2D3748, 0xFF2D4838, 0xFF483828};
            int[] lvl3Colors = {0xFF3A4A5A, 0xFF3A5A4A, 0xFF5A4A3A};

            for (int a = 0; a < 3; a++) {
                var lvl1 = new ContainerWidget();
                lvl1.addDecoration(new Background(lvl1Colors[a]));
                var lvl1Style = new TaffyStyle();
                lvl1Style.flexDirection = FlexDirection.ROW;
                lvl1Style.justifyContent = AlignContent.START;
                lvl1Style.alignItems = AlignItems.STRETCH;
                lvl1Style.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(240));
                lvl1Style.flexShrink = 0;
                lvl1Style.overflow = new TaffyPoint<>(Overflow.SCROLL, Overflow.VISIBLE);
                lvl1Style.scrollbarWidth = 8;
                lvl1Style.gap = TaffySize.all(LengthPercentage.length(6));
                lvl1Style.padding = TaffyRect.all(LengthPercentage.length(6));
                lvl1.setStyle(lvl1Style);
                root.addChild(lvl1);

                for (int b = 0; b < 5; b++) {
                    var lvl2 = new ContainerWidget();
                    lvl2.addDecoration(new Background(lvl2Colors[a]));
                    var lvl2Style = new TaffyStyle();
                    lvl2Style.flexDirection = FlexDirection.COLUMN;
                    lvl2Style.justifyContent = AlignContent.START;
                    lvl2Style.alignItems = AlignItems.STRETCH;
                    lvl2Style.size = new TaffySize<>(TaffyDimension.length(180), TaffyDimension.percent(1f));
                    lvl2Style.flexShrink = 0;
                    lvl2Style.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
                    lvl2Style.scrollbarWidth = 8;
                    lvl2Style.gap = TaffySize.all(LengthPercentage.length(3));
                    lvl2Style.padding = TaffyRect.all(LengthPercentage.length(3));
                    lvl2.setStyle(lvl2Style);
                    lvl1.addChild(lvl2);

                    for (int c = 0; c < 8; c++) {
                        var lvl3 = new ContainerWidget();
                        lvl3.addDecoration(new Background(lvl3Colors[a]));
                        var lvl3Style = new TaffyStyle();
                        lvl3Style.flexDirection = FlexDirection.ROW;
                        lvl3Style.justifyContent = AlignContent.START;
                        lvl3Style.alignItems = AlignItems.CENTER;
                        lvl3Style.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(50));
                        lvl3Style.flexShrink = 0;
                        lvl3Style.overflow = new TaffyPoint<>(Overflow.SCROLL, Overflow.VISIBLE);
                        lvl3Style.scrollbarWidth = 4;
                        lvl3Style.gap = TaffySize.all(LengthPercentage.length(2));
                        lvl3.setStyle(lvl3Style);
                        lvl2.addChild(lvl3);

                        for (int d = 0; d < 6; d++) {
                            var label = new Label(a + "-" + b + "-" + c + "-" + d, TextAlign.CENTER, 0xFFFFFFFF);
                            label.addDecoration(new Background(COLORS[(a + b + c + d) % COLORS.length]));
                            var lStyle = new TaffyStyle();
                            lStyle.size = new TaffySize<>(TaffyDimension.length(80), TaffyDimension.length(36));
                            lStyle.flexShrink = 0;
                            label.setStyle(lStyle);
                            lvl3.addChild(label);
                        }
                    }
                }
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void biaxialScrollWithNestedTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = bgContainer(0xFF0A0A0A);
            root.asTreeRoot();
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.COLUMN;
            rootStyle.justifyContent = AlignContent.START;
            rootStyle.alignItems = AlignItems.STRETCH;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var biaxial = new ContainerWidget();
            biaxial.addDecoration(new Background(0xFF1A1A2E));
            var biaxialStyle = new TaffyStyle();
            biaxialStyle.flexDirection = FlexDirection.COLUMN;
            biaxialStyle.justifyContent = AlignContent.START;
            biaxialStyle.alignItems = AlignItems.START;
            biaxialStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            biaxialStyle.overflow = new TaffyPoint<>(Overflow.SCROLL, Overflow.SCROLL);
            biaxialStyle.scrollbarWidth = 8;
            biaxialStyle.gap = TaffySize.all(LengthPercentage.length(8));
            biaxialStyle.padding = TaffyRect.all(LengthPercentage.length(8));
            biaxial.setStyle(biaxialStyle);
            root.addChild(biaxial);

            int[] rowColors = {0xFF2D3748, 0xFF1A3A2A, 0xFF3A1A2A, 0xFF1A2A3A, 0xFF2A3A1A,
                    0xFF3A2A1A, 0xFF1A1A3A, 0xFF3A1A3A};

            for (int row = 0; row < 8; row++) {
                var rowContainer = new ContainerWidget();
                rowContainer.addDecoration(new Background(rowColors[row]));
                var rowStyle = new TaffyStyle();
                rowStyle.flexDirection = FlexDirection.ROW;
                rowStyle.justifyContent = AlignContent.START;
                rowStyle.alignItems = AlignItems.CENTER;
                rowStyle.size = new TaffySize<>(TaffyDimension.length(1600), TaffyDimension.length(100));
                rowStyle.flexShrink = 0;
                rowStyle.gap = TaffySize.all(LengthPercentage.length(6));
                rowStyle.padding = TaffyRect.all(LengthPercentage.length(6));
                rowContainer.setStyle(rowStyle);
                biaxial.addChild(rowContainer);

                if (row % 3 == 0) {
                    var nestedScroll = new ContainerWidget();
                    nestedScroll.addDecoration(new Background(0xFF0D0D2E));
                    var nestedStyle = new TaffyStyle();
                    nestedStyle.flexDirection = FlexDirection.ROW;
                    nestedStyle.justifyContent = AlignContent.START;
                    nestedStyle.alignItems = AlignItems.CENTER;
                    nestedStyle.size = new TaffySize<>(TaffyDimension.length(300), TaffyDimension.percent(0.85f));
                    nestedStyle.flexShrink = 0;
                    nestedStyle.overflow = new TaffyPoint<>(Overflow.SCROLL, Overflow.VISIBLE);
                    nestedStyle.scrollbarWidth = 5;
                    nestedStyle.gap = TaffySize.all(LengthPercentage.length(3));
                    nestedScroll.setStyle(nestedStyle);
                    rowContainer.addChild(nestedScroll);

                    for (int k = 0; k < 10; k++) {
                        var label = colorLabel(COLORS[(row + k) % COLORS.length], "嵌" + (k + 1), 60);
                        label.getStyle().size = new TaffySize<>(TaffyDimension.length(70), TaffyDimension.percent(0.9f));
                        nestedScroll.addChild(label);
                    }

                    for (int k = 0; k < 8; k++) {
                        var label = colorLabel(COLORS[(row + k + 4) % COLORS.length], "行" + row + "-格" + (k + 1), 60);
                        label.getStyle().size = new TaffySize<>(TaffyDimension.length(100), TaffyDimension.percent(0.85f));
                        rowContainer.addChild(label);
                    }
                } else if (row % 3 == 1) {
                    var nestedGrid = new ContainerWidget();
                    nestedGrid.addDecoration(new Background(0xFF0D2E0D));
                    var nestedGridStyle = new TaffyStyle();
                    nestedGridStyle.display = TaffyDisplay.GRID;
                    nestedGridStyle.gridTemplateColumns = List.of(
                            TrackSizingFunction.fr(1f),
                            TrackSizingFunction.fr(1f)
                    );
                    nestedGridStyle.size = new TaffySize<>(TaffyDimension.length(200), TaffyDimension.percent(0.85f));
                    nestedGridStyle.flexShrink = 0;
                    nestedGridStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);
                    nestedGridStyle.scrollbarWidth = 5;
                    nestedGridStyle.gap = TaffySize.all(LengthPercentage.length(2));
                    nestedGrid.setStyle(nestedGridStyle);
                    rowContainer.addChild(nestedGrid);

                    for (int k = 0; k < 8; k++) {
                        var gStyle = new TaffyStyle();
                        gStyle.size = new TaffySize<>(TaffyDimension.auto(), TaffyDimension.length(28));
                        gStyle.flexShrink = 0;
                        var label = new Label("G" + k, TextAlign.CENTER, 0xFFFFFFFF);
                        label.addDecoration(new Background(COLORS[(row + k) % COLORS.length]));
                        label.setStyle(gStyle);
                        nestedGrid.addChild(label);
                    }

                    for (int k = 0; k < 10; k++) {
                        var label = colorLabel(COLORS[(row + k + 2) % COLORS.length], "行" + row + "-项" + (k + 1), 60);
                        label.getStyle().size = new TaffySize<>(TaffyDimension.length(90), TaffyDimension.percent(0.85f));
                        rowContainer.addChild(label);
                    }
                } else {
                    for (int k = 0; k < 14; k++) {
                        var label = colorLabel(COLORS[(row + k) % COLORS.length], "行" + row + "-" + (k + 1), 60);
                        label.getStyle().size = new TaffySize<>(TaffyDimension.length(100), TaffyDimension.percent(0.85f));
                        rowContainer.addChild(label);
                    }
                }
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void main(String[] args) throws Exception {
        verticalInVerticalTest();
    }
}
