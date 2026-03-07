package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;

import java.util.List;

public class ComboBoxAndDragBoxTest {

    public static void main(String[] ignored) throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new ContainerWidget();
            root.asTreeRoot();
            root.addDecoration(new Background(0xFF1E293B));
            var rootStyle = new TaffyStyle();
            rootStyle.flexDirection = FlexDirection.COLUMN;
            rootStyle.justifyContent = AlignContent.CENTER;
            rootStyle.alignItems = AlignItems.CENTER;
            rootStyle.gap = TaffySize.of(LengthPercentage.length(20), LengthPercentage.length(20));
            rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
            root.setStyle(rootStyle);

            var title = new Label("ComboBox 和 DragBox 测试", TextAlign.CENTER, 0xFFE2E8F0);
            var titleStyle = new TaffyStyle();
            titleStyle.size = new TaffySize<>(TaffyDimension.length(400), TaffyDimension.length(30));
            titleStyle.flexShrink = 0;
            root.addChild(title, titleStyle);

            var comboSection = createComboBoxSection();
            root.addChild(comboSection);

            var dragSection = createDragBoxSection();
            root.addChild(dragSection);

            return root;
        })) {
            frame.run();
        }
    }

    private static ContainerWidget createComboBoxSection() {
        var section = new ContainerWidget();
        section.addDecoration(new Background(0xFF0F172A));
        var sectionStyle = new TaffyStyle();
        sectionStyle.flexDirection = FlexDirection.COLUMN;
        sectionStyle.alignItems = AlignItems.STRETCH;
        sectionStyle.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.content());
        sectionStyle.gap = TaffySize.of(LengthPercentage.length(10), LengthPercentage.length(10));
        section.setStyle(sectionStyle);

        var sectionTitle = new Label("ComboBox 示例", TextAlign.LEFT, 0xFF94A3B8);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        titleStyle.flexShrink = 0;
        section.addChild(sectionTitle, titleStyle);

        var fruits = List.of("苹果", "香蕉", "橙子", "葡萄", "西瓜", "草莓", "芒果", "樱桃");
        var comboBox1 = new ComboBox<>(fruits, s -> s, false);
        var combo1Style = new TaffyStyle();
        combo1Style.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(32));
        combo1Style.flexShrink = 0;
        section.addChild(comboBox1, combo1Style);

        var selectedLabel1 = new Label("选择: " + comboBox1.getSelected(), TextAlign.LEFT, 0xFFE2E8F0);
        comboBox1.setOnChange((String v) -> selectedLabel1.setText("选择: " + v));
        var labelStyle1 = new TaffyStyle();
        labelStyle1.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle1.flexShrink = 0;
        section.addChild(selectedLabel1, labelStyle1);

        var numbers = List.of(1, 2, 3, 5, 10, 20, 50, 100);
        var comboBox2 = new ComboBox<>(numbers, n -> "数字: " + n, true);
        var combo2Style = new TaffyStyle();
        combo2Style.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(32));
        combo2Style.flexShrink = 0;
        section.addChild(comboBox2, combo2Style);

        var selectedLabel2 = new Label("选择: 无", TextAlign.LEFT, 0xFFE2E8F0);
        comboBox2.setOnChange((Integer v) -> selectedLabel2.setText("选择: " + (v == null ? "无" : v)));
        var labelStyle2 = new TaffyStyle();
        labelStyle2.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle2.flexShrink = 0;
        section.addChild(selectedLabel2, labelStyle2);

        return section;
    }

    private static ContainerWidget createDragBoxSection() {
        var section = new ContainerWidget();
        section.addDecoration(new Background(0xFF0F172A));
        var sectionStyle = new TaffyStyle();
        sectionStyle.flexDirection = FlexDirection.COLUMN;
        sectionStyle.alignItems = AlignItems.STRETCH;
        sectionStyle.gap = TaffySize.of(LengthPercentage.length(10), LengthPercentage.length(10));
        sectionStyle.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.content());
        section.setStyle(sectionStyle);

        var sectionTitle = new Label("DragBox 示例", TextAlign.LEFT, 0xFF94A3B8);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        titleStyle.flexShrink = 0;
        section.addChild(sectionTitle, titleStyle);

        var drag1 = new DragBox(0, 100, 50);
        var drag1Style = new TaffyStyle();
        drag1Style.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        drag1Style.flexShrink = 0;
        section.addChild(drag1, drag1Style);

        var valueLabel1 = new Label(String.format("值: %.2f (范围: 0 - 100)", drag1.getValue()), TextAlign.LEFT, 0xFFE2E8F0);
        drag1.setOnChange((Double v) -> valueLabel1.setText(String.format("值: %.2f (范围: 0 - 100)", v)));
        var labelStyle1 = new TaffyStyle();
        labelStyle1.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle1.flexShrink = 0;
        section.addChild(valueLabel1, labelStyle1);

        var drag2 = new DragBox(-1.0, 1.0, 0);
        var drag2Style = new TaffyStyle();
        drag2Style.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        drag2Style.flexShrink = 0;
        section.addChild(drag2, drag2Style);

        var valueLabel2 = new Label(String.format("值: %.3f (范围: -1.0 - 1.0)", drag2.getValue()), TextAlign.LEFT, 0xFFE2E8F0);
        drag2.setOnChange((Double v) -> valueLabel2.setText(String.format("值: %.3f (范围: -1.0 - 1.0)", v)));
        var labelStyle2 = new TaffyStyle();
        labelStyle2.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle2.flexShrink = 0;
        section.addChild(valueLabel2, labelStyle2);

        var drag3 = new DragBox(0, 10, 5);
        var drag3Style = new TaffyStyle();
        drag3Style.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(30));
        drag3Style.flexShrink = 0;
        section.addChild(drag3, drag3Style);

        var valueLabel3 = new Label(String.format("整数值: %d", (int) drag3.getValue()), TextAlign.LEFT, 0xFFE2E8F0);
        drag3.setOnChange((Double v) -> valueLabel3.setText(String.format("整数值: %d", (int) v.doubleValue())));
        var labelStyle3 = new TaffyStyle();
        labelStyle3.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(24));
        labelStyle3.flexShrink = 0;
        section.addChild(valueLabel3, labelStyle3);

        return section;
    }
}


