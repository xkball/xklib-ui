package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffyLine;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.GridPlacement;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;
import dev.vfyjxf.taffy.style.TrackSizingFunction;

import java.util.List;

public class GridLayoutTest {

    private static final int[] CELL_COLORS = {
            0xFFE94560, 0xFF0F3460, 0xFF533483, 0xFF2EC4B6,
            0xFFFF6B6B, 0xFF4ECDC4, 0xFF45B7D1, 0xFF96CEB4,
            0xFFFECEA8, 0xFF88D8B0, 0xFFDDA0DD, 0xFF98FB98
    };

    private static ContainerWidget bgContainer(int color) {
        var w = new ContainerWidget();
        w.addDecoration(new Background(color));
        return w;
    }

    private static TaffyStyle rootGridStyle(List<TrackSizingFunction> cols, List<TrackSizingFunction> rows) {
        var s = new TaffyStyle();
        s.display = TaffyDisplay.GRID;
        s.gridTemplateColumns = cols;
        s.gridTemplateRows = rows;
        s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        return s;
    }

    private static Widget cell(int color, String text) {
        var w = new ContainerWidget();
        w.addDecoration(new Background(color));
        w.setStyle(s -> {
            s.alignItems = AlignItems.CENTER;
            s.justifyContent = AlignContent.CENTER;
        });
        var label = new Label(text, TextAlign.CENTER, 0xFFFFFFFF);
        label.getStyle().size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(20));
        w.addChild(label);
        return w;
    }

    private static Widget cellAtLine(int color, String text, int colLine, int rowLine) {
        var w = (ContainerWidget) cell(color, text);
        w.getStyle().gridColumn = new TaffyLine<>(GridPlacement.line(colLine), GridPlacement.auto());
        w.getStyle().gridRow = new TaffyLine<>(GridPlacement.line(rowLine), GridPlacement.auto());
        return w;
    }

    private static Widget cellWithSpan(int color, String text, int colLine, int rowLine, int colSpan, int rowSpan) {
        var w = (ContainerWidget) cell(color, text);
        w.getStyle().gridColumn = new TaffyLine<>(GridPlacement.line(colLine), GridPlacement.span(colSpan));
        w.getStyle().gridRow = new TaffyLine<>(GridPlacement.line(rowLine), GridPlacement.span(rowSpan));
        return w;
    }

    public static void basicGridTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var cols = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );
            var rows = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );

            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(rootGridStyle(cols, rows));
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 3; c++) {
                    int idx = r * 3 + c;
                    root.addChild(cell(CELL_COLORS[idx], "(" + (c + 1) + "," + (r + 1) + ")"));
                }
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void fixedAndFlexColsTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var cols = List.of(
                    TrackSizingFunction.fixed(LengthPercentage.length(150)),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(2f)
            );
            var rows = List.of(
                    TrackSizingFunction.fixed(LengthPercentage.length(80)),
                    TrackSizingFunction.fixed(LengthPercentage.length(80)),
                    TrackSizingFunction.fr(1)
            );

            var root = bgContainer(0xFF2D3748);
            root.asTreeRoot();
            root.setStyle(rootGridStyle(cols, rows));
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    int idx = r * 3 + c;
                    root.addChild(cell(CELL_COLORS[idx % CELL_COLORS.length], "r" + r + "c" + c));
                }
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void spanTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var cols = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );
            var rows = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );

            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(rootGridStyle(cols, rows));
            root.addChild(cellWithSpan(0xFFE94560, "span 2x2", 1, 1, 2, 2));
            root.addChild(cellAtLine(0xFF0F3460, "c3r1", 3, 1));
            root.addChild(cellAtLine(0xFF533483, "c3r2", 3, 2));
            root.addChild(cellWithSpan(0xFF2EC4B6, "span 3", 1, 3, 3, 1));
            return root;
        })) {
            frame.run();
        }
    }

    public static void percentColsTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var cols = List.of(
                    TrackSizingFunction.fixed(LengthPercentage.percent(0.3f)),
                    TrackSizingFunction.fixed(LengthPercentage.percent(0.5f)),
                    TrackSizingFunction.fr(1f)
            );
            var rows = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );

            var root = bgContainer(0xFF2D3748);
            root.asTreeRoot();
            root.setStyle(rootGridStyle(cols, rows));
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    int idx = r * 3 + c;
                    root.addChild(cell(CELL_COLORS[idx % CELL_COLORS.length], "r" + r + "c" + c));
                }
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void gridWithGapTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var cols = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );
            var rows = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );

            var rootStyle = rootGridStyle(cols, rows);
            rootStyle.gap = TaffySize.all(LengthPercentage.percent(0.03f));

            var root = bgContainer(0xFF1A1A2E);
            root.asTreeRoot();
            root.setStyle(rootStyle);
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 4; c++) {
                    final int idx = r * 4 + c;
                    var btn = new Button("btn" + idx, () -> System.out.println("clicked " + idx));
                    btn.addDecoration(ButtonLooks.roundRect());
                    root.addChild(btn);
                }
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void gridAlignTest() throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var cols = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );
            var rows = List.of(
                    TrackSizingFunction.fr(1f),
                    TrackSizingFunction.fr(1f)
            );

            var rootStyle = rootGridStyle(cols, rows);
            rootStyle.justifyContent = AlignContent.CENTER;
            rootStyle.alignContent = AlignContent.CENTER;
            rootStyle.alignItems = AlignItems.CENTER;
            rootStyle.justifyItems = AlignItems.CENTER;

            var root = bgContainer(0xFF2D3748);
            root.asTreeRoot();
            root.setStyle(rootStyle);
            String[] labels = {"左上", "右上", "左下", "右下"};
            int[] cellColors = {0xFFE94560, 0xFF0F3460, 0xFF533483, 0xFF2EC4B6};
            for (int i = 0; i < labels.length; i++) {
                var label = new Label(labels[i], TextAlign.CENTER, 0xFFFFFFFF);
                label.getStyle().size = new TaffySize<>(TaffyDimension.length(120), TaffyDimension.length(60));
                label.addDecoration(new Background(cellColors[i]));
                root.addChild(label);
            }
            return root;
        })) {
            frame.run();
        }
    }

    public static void main(String[] args) throws Exception {
        gridAlignTest();
    }
}
