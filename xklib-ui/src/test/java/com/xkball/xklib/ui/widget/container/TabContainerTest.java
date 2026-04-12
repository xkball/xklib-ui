package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Label;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;

public class TabContainerTest {

    public static void main(String[] ignored) throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new ContainerWidget();
            root.asTreeRoot();
            root.inlineStyle("background-color: 0xFF0F172A;");
            root.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.justifyContent = AlignContent.CENTER;
                s.alignItems = AlignItems.CENTER;
                s.size = TaffySize.all(TaffyDimension.percent(1f));
            });

            var tab = new TabContainer();
            tab.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.8f), TaffyDimension.percent(0.8f)));

            var page1 = new ContainerWidget();
            page1.inlineStyle("background-color: 0xFF1E3A5F;");
            page1.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.justifyContent = AlignContent.CENTER;
                s.alignItems = AlignItems.CENTER;
                s.size = TaffySize.all(TaffyDimension.percent(1f));
            });
            var label1 = new Label("第一页内容", 0xFFE2E8F0);
            label1.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.8f), TaffyDimension.length(40)));
            page1.addChild(label1);
            for (int i = 1; i <= 3; i++) {
                int idx = i;
                var btn = new Button("页1按钮" + i, () -> System.out.println("页1点击按钮" + idx));
                btn.setStyle(s -> s.size = TaffySize.of(TaffyDimension.length(120), TaffyDimension.length(32)));
                page1.addChild(btn);
            }

            var page2 = new ContainerWidget();
            page2.inlineStyle("background-color: 0xFF3A1E5F;");
            page2.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.justifyContent = AlignContent.CENTER;
                s.alignItems = AlignItems.CENTER;
                s.size = TaffySize.all(TaffyDimension.percent(1f));
            });
            var label2 = new Label("第二页内容",  0xFFE2E8F0);
            label2.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.8f), TaffyDimension.length(40)));
            page2.addChild(label2);

            var page3 = new ContainerWidget();
            page3.inlineStyle("background-color: 0xFF1E5F3A;");
            page3.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.justifyContent = AlignContent.CENTER;
                s.alignItems = AlignItems.CENTER;
                s.size = TaffySize.all(TaffyDimension.percent(1f));
            });
            var label3 = new Label("第三页内容", 0xFFE2E8F0);
            label3.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.8f), TaffyDimension.length(40)));
            page3.addChild(label3);

            tab.addTabPage(page1, "第一页")
               .addTabPage(page2, "第二页")
               .addTabPage(page3, "第三页");

            root.addChild(tab);
            return root;
        })) {
            frame.run();
        }
    }
}

