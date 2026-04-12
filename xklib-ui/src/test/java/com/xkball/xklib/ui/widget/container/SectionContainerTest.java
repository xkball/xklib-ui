package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.layout.DefaultStyles;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Label;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;

public class SectionContainerTest {

    public static void main(String[] ignored) throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new ContainerWidget();
            root.asTreeRoot();
            root.setCSSId("root");
            root.inlineStyle("background-color: 0xFF0F172A;");
            root.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.justifyContent = AlignContent.START;
                s.alignItems = AlignItems.CENTER;
                s.size = TaffySize.all(TaffyDimension.percent(1f));
            });
            root.setYScrollEnable(true);

            var section1 = new SectionContainer("第一个分组");
            section1.setCSSId("section1");
            section1.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.content()));
            section1.getHeader().inlineStyle("background-color: 0xFF1E293B;");
            var label1 = new Label("这是第一个分组的内容",  0xFFE2E8F0);
            label1.inlineStyle("background-color: 0xFF334155;");
            label1.setCSSId("label1");
            label1.style.size = TaffySize.of(TaffyDimension.percent(1),TaffyDimension.length(200));
            section1.setContent(label1);
            root.addChild(section1);

            var section2 = new SectionContainer("第二个分组 - 默认展开");
            section2.setCSSId("section2");
            section2.getHeader().inlineStyle("background-color: 0xFF1E293B;");
            section2.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.auto()));
            var content2 = new ContainerWidget();
            content2.inlineStyle("background-color: 0xFF475569;");
            content2.setCSSId("content2");
            content2.applyStyle(DefaultStyles::flexCenteredColum);
            content2.setStyle(s -> {
                s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.auto());
            });
            for (int i = 1; i <= 3; i++) {
                int idx = i;
                var btn = new Button("按钮 " + i, () -> System.out.println("press " + idx));
                btn.setStyle(s -> {
                    s.size = new TaffySize<>(TaffyDimension.length(100), TaffyDimension.length(32));
                    s.flexShrink = 0;
                });
                content2.addChild(btn);
            }
            section2.setContent(content2);
            root.addChild(section2);
//            root.addChild(content2);
            var section3 = new SectionContainer("第三个分组 - 初始折叠");
            section3.setCSSId("section3");
            section3.getHeader().inlineStyle("background-color: 0xFF1E293B;");
            section3.setExpanded(false);
            section3.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.5f),TaffyDimension.auto()));
            var content3 = new ContainerWidget();
            content3.setCSSId("content3");
            content3.inlineStyle("background-color: 0xFF64748B;");
            content3.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(180)));
            section3.setContent(content3);
            root.addChild(section3);

            var hint = new Label("提示：点击分组标题可以展开/折叠内容", 0xFF94A3B8);
            hint.setStyle(s -> {
                s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(30));
                s.flexShrink = 0;
            });
            root.addChild(hint);

            return root;
        })) {
            frame.run();
        }
    }
}

