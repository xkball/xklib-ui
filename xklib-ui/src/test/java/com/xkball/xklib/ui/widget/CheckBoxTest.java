package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.LayoutVariable;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;

public class CheckBoxTest {

    public static void main(String[] ignored) throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new ContainerWidget();
            root.asTreeRoot();
            root.addDecoration(new Background(0xFFFFFFFF));
            root.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.justifyContent = AlignContent.CENTER;
                s.alignItems = AlignItems.CENTER;
                s.gap = TaffySize.of(LengthPercentage.length(20), LengthPercentage.length(20));
                s.size = TaffySize.all(TaffyDimension.percent(1f));
            });

            var title = new Label("CheckBox 测试", TextAlign.CENTER, 0xFF1E293B);
            title.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.length(300), TaffyDimension.length(24)));
            root.addChild(title);

            var binding = new BooleanLayoutVariable();
            binding.addCallback(v -> System.out.println("CheckBox 值变更: " + v));

            var cb1 = new CheckBox();
            cb1.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.length(60), TaffyDimension.length(30)));
            cb1.bind(binding);
            root.addChild(cb1);

            var statusLabel = new Label("当前: 关", TextAlign.CENTER, 0xFF1E293B);
            statusLabel.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.length(300), TaffyDimension.length(24)));
            binding.addCallback(v -> statusLabel.setText("当前: " + (v ? "开" : "关")));
            root.addChild(statusLabel);

            var cb2 = new CheckBox();
            cb2.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.length(80), TaffyDimension.length(36)));
            cb2.setValue(true);
            root.addChild(cb2);

            var label2 = new Label("初始为开(无绑定)", TextAlign.CENTER, 0xFF1E293B);
            label2.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.length(300), TaffyDimension.length(24)));
            root.addChild(label2);

            return root;
        })) {
            frame.run();
        }
    }
}

