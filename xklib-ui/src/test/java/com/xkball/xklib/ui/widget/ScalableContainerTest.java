package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.deco.ButtonLooks;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.ScalableContainer;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;

import java.util.List;

public class ScalableContainerTest {

    public static void main(String[] ignored) throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new ContainerWidget();
            root.asTreeRoot();
            root.addDecoration(new Background(0xFF0F172A));
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.COLUMN;
            rootStyle.justifyContent = AlignContent.CENTER;
            rootStyle.alignItems = AlignItems.CENTER;
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var title = new Label("ScalableContainer 测试", TextAlign.CENTER, 0xFFE2E8F0);
            var titleStyle = new TaffyStyle();
            titleStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(30));
            titleStyle.flexShrink = 0;
            root.addChild(title, titleStyle);

            var hint = new Label("滚轮缩放 | 左键拖动平移 | Shift+滚轮垂直移动 | Ctrl+滚轮水平移动", TextAlign.CENTER, 0xFF94A3B8);
            var hintStyle = new TaffyStyle();
            hintStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(20));
            hintStyle.flexShrink = 0;
            root.addChild(hint, hintStyle);

            var scalableContainer = new ScalableContainer();
            scalableContainer.addDecoration(new Background(0xFF1E293B));
            var scStyle = new TaffyStyle();
            scStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.percent(0.85f));
            root.addChild(scalableContainer, scStyle);

            var content = createComplexContent();
            scalableContainer.setChild(content);

            return root;
        })) {
            frame.run();
        }
    }

    private static ContainerWidget createComplexContent() {
        var content = new ContainerWidget();
        content.addDecoration(new Background(0xFF334155));
        var contentStyle = new TaffyStyle();
        contentStyle.flexDirection = FlexDirection.COLUMN;
        contentStyle.justifyContent = AlignContent.START;
        contentStyle.alignItems = AlignItems.CENTER;
        contentStyle.gap = TaffySize.of(LengthPercentage.length(15), LengthPercentage.length(15));
        contentStyle.size = new TaffySize<>(TaffyDimension.length(600), TaffyDimension.length(800));
        content.setStyle(contentStyle);

        var section1 = createButtonSection();
        content.addChild(section1);

        var section2 = createComboBoxSection();
        content.addChild(section2);

        var section3 = createDragBoxSection();
        content.addChild(section3);

        var section4 = createTextFieldSection();
        content.addChild(section4);

        var section5 = createGridSection();
        content.addChild(section5);

        return content;
    }

    private static ContainerWidget createButtonSection() {
        var section = new ContainerWidget();
        section.addDecoration(new Background(0xFF475569));
        var sectionStyle = new TaffyStyle();
        sectionStyle.flexDirection = FlexDirection.COLUMN;
        sectionStyle.alignItems = AlignItems.STRETCH;
        sectionStyle.gap = TaffySize.of(LengthPercentage.length(10), LengthPercentage.length(10));
        sectionStyle.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.content());
        section.setStyle(sectionStyle);

        var sectionTitle = new Label("按钮组", TextAlign.LEFT, 0xFFFFFFFF);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        titleStyle.flexShrink = 0;
        section.addChild(sectionTitle, titleStyle);

        var buttonRow = new ContainerWidget();
        var rowStyle = new TaffyStyle();
        rowStyle.flexDirection = FlexDirection.ROW;
        rowStyle.justifyContent = AlignContent.START;
        rowStyle.alignItems = AlignItems.CENTER;
        rowStyle.gap = TaffySize.of(LengthPercentage.length(10), LengthPercentage.length(10));
        buttonRow.setStyle(rowStyle);

        for (int i = 1; i <= 5; i++) {
            int idx = i;
            var btn = new Button("按钮 " + i, () -> System.out.println("点击了按钮 " + idx));
            btn.addDecoration(ButtonLooks.roundRect());
            var btnStyle = new TaffyStyle();
            btnStyle.size = new TaffySize<>(TaffyDimension.length(100), TaffyDimension.length(32));
            btnStyle.flexShrink = 0;
            buttonRow.addChild(btn, btnStyle);
        }

        var buttonRowStyle = new TaffyStyle();
        buttonRowStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.auto());
        section.addChild(buttonRow, buttonRowStyle);

        return section;
    }

    private static ContainerWidget createComboBoxSection() {
        var section = new ContainerWidget();
        section.addDecoration(new Background(0xFF475569));
        var sectionStyle = new TaffyStyle();
        sectionStyle.flexDirection = FlexDirection.COLUMN;
        sectionStyle.alignItems = AlignItems.STRETCH;
        sectionStyle.gap = TaffySize.of(LengthPercentage.length(10), LengthPercentage.length(10));
        sectionStyle.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.content());
        section.setStyle(sectionStyle);

        var sectionTitle = new Label("下拉选择框", TextAlign.LEFT, 0xFFFFFFFF);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        titleStyle.flexShrink = 0;
        section.addChild(sectionTitle, titleStyle);

        var fruits = List.of("苹果", "香蕉", "橙子", "葡萄", "西瓜", "草莓", "芒果", "樱桃");
        var comboBox = new ComboBox<>(fruits, s -> s, false);
        var comboStyle = new TaffyStyle();
        comboStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(32));
        comboStyle.flexShrink = 0;
        section.addChild(comboBox, comboStyle);

        var selectedLabel = new Label("选择: " + comboBox.getSelected(), TextAlign.LEFT, 0xFFE2E8F0);
        comboBox.setOnChange((String v) -> selectedLabel.setText("选择: " + v));
        var labelStyle = new TaffyStyle();
        labelStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle.flexShrink = 0;
        section.addChild(selectedLabel, labelStyle);

        return section;
    }

    private static ContainerWidget createDragBoxSection() {
        var section = new ContainerWidget();
        section.addDecoration(new Background(0xFF475569));
        var sectionStyle = new TaffyStyle();
        sectionStyle.flexDirection = FlexDirection.COLUMN;
        sectionStyle.alignItems = AlignItems.STRETCH;
        sectionStyle.gap = TaffySize.of(LengthPercentage.length(10), LengthPercentage.length(10));
        sectionStyle.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.content());
        section.setStyle(sectionStyle);

        var sectionTitle = new Label("拖动滑块", TextAlign.LEFT, 0xFFFFFFFF);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        titleStyle.flexShrink = 0;
        section.addChild(sectionTitle, titleStyle);

        var drag1 = new DragBox(0, 100, 50);
        var drag1Style = new TaffyStyle();
        drag1Style.size = new TaffySize<>(TaffyDimension.percent(0.8f), TaffyDimension.length(24));
        drag1Style.flexShrink = 0;
        section.addChild(drag1, drag1Style);

        var valueLabel = new Label(String.format("值: %.2f", drag1.getValue()), TextAlign.LEFT, 0xFFE2E8F0);
        drag1.setOnChange((Double v) -> valueLabel.setText(String.format("值: %.2f", v)));
        var labelStyle = new TaffyStyle();
        labelStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle.flexShrink = 0;
        section.addChild(valueLabel, labelStyle);

        return section;
    }

    private static ContainerWidget createTextFieldSection() {
        var section = new ContainerWidget();
        section.addDecoration(new Background(0xFF475569));
        var sectionStyle = new TaffyStyle();
        sectionStyle.flexDirection = FlexDirection.COLUMN;
        sectionStyle.alignItems = AlignItems.STRETCH;
        sectionStyle.gap = TaffySize.of(LengthPercentage.length(10), LengthPercentage.length(10));
        sectionStyle.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.content());
        section.setStyle(sectionStyle);

        var sectionTitle = new Label("更多滑块控制", TextAlign.LEFT, 0xFFFFFFFF);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        titleStyle.flexShrink = 0;
        section.addChild(sectionTitle, titleStyle);

        var drag1 = new DragBox(0, 255, 128);
        var drag1Style = new TaffyStyle();
        drag1Style.size = new TaffySize<>(TaffyDimension.percent(0.8f), TaffyDimension.length(24));
        drag1Style.flexShrink = 0;
        section.addChild(drag1, drag1Style);

        var valueLabel1 = new Label(String.format("红色: %d", (int) drag1.getValue()), TextAlign.LEFT, 0xFFE2E8F0);
        drag1.setOnChange((Double v) -> valueLabel1.setText(String.format("红色: %d", (int) v.doubleValue())));
        var labelStyle1 = new TaffyStyle();
        labelStyle1.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle1.flexShrink = 0;
        section.addChild(valueLabel1, labelStyle1);

        var drag2 = new DragBox(0, 255, 128);
        var drag2Style = new TaffyStyle();
        drag2Style.size = new TaffySize<>(TaffyDimension.percent(0.8f), TaffyDimension.length(24));
        drag2Style.flexShrink = 0;
        section.addChild(drag2, drag2Style);

        var valueLabel2 = new Label(String.format("绿色: %d", (int) drag2.getValue()), TextAlign.LEFT, 0xFFE2E8F0);
        drag2.setOnChange((Double v) -> valueLabel2.setText(String.format("绿色: %d", (int) v.doubleValue())));
        var labelStyle2 = new TaffyStyle();
        labelStyle2.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle2.flexShrink = 0;
        section.addChild(valueLabel2, labelStyle2);

        var drag3 = new DragBox(0, 255, 128);
        var drag3Style = new TaffyStyle();
        drag3Style.size = new TaffySize<>(TaffyDimension.percent(0.8f), TaffyDimension.length(24));
        drag3Style.flexShrink = 0;
        section.addChild(drag3, drag3Style);

        var valueLabel3 = new Label(String.format("蓝色: %d", (int) drag3.getValue()), TextAlign.LEFT, 0xFFE2E8F0);
        drag3.setOnChange((Double v) -> valueLabel3.setText(String.format("蓝色: %d", (int) v.doubleValue())));
        var labelStyle3 = new TaffyStyle();
        labelStyle3.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle3.flexShrink = 0;
        section.addChild(valueLabel3, labelStyle3);

        return section;
    }

    private static ContainerWidget createGridSection() {
        var section = new ContainerWidget();
        section.addDecoration(new Background(0xFF475569));
        var sectionStyle = new TaffyStyle();
        sectionStyle.flexDirection = FlexDirection.COLUMN;
        sectionStyle.alignItems = AlignItems.STRETCH;
        sectionStyle.gap = TaffySize.of(LengthPercentage.length(10), LengthPercentage.length(10));
        sectionStyle.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.content());
        section.setStyle(sectionStyle);

        var sectionTitle = new Label("颜色网格", TextAlign.LEFT, 0xFFFFFFFF);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        titleStyle.flexShrink = 0;
        section.addChild(sectionTitle, titleStyle);

        var grid = new ContainerWidget();
        var gridStyle = new TaffyStyle();
        gridStyle.flexDirection = FlexDirection.ROW;
        gridStyle.justifyContent = AlignContent.START;
        gridStyle.alignItems = AlignItems.START;
        gridStyle.gap = TaffySize.of(LengthPercentage.length(5), LengthPercentage.length(5));
        grid.setStyle(gridStyle);

        int[] colors = {
            0xFFE94560, 0xFF0F3460, 0xFF533483, 0xFF2EC4B6,
            0xFFFF6B6B, 0xFF4ECDC4, 0xFF45B7D1, 0xFF96CEB4,
            0xFFFECEA8, 0xFF88D8B0, 0xFFDDA0DD, 0xFF98FB98
        };

        for (int color : colors) {
            var colorBox = new ContainerWidget();
            colorBox.addDecoration(new Background(color));
            var boxStyle = new TaffyStyle();
            boxStyle.size = new TaffySize<>(TaffyDimension.length(60), TaffyDimension.length(60));
            boxStyle.flexShrink = 0;
            grid.addChild(colorBox, boxStyle);
        }

        var gridContainerStyle = new TaffyStyle();
        gridContainerStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.auto());
        section.addChild(grid, gridContainerStyle);

        return section;
    }
}

