package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Label;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;

public class WindowedContainerTest {

    public static void main(String[] ignored) throws Exception {
        try (var frame = new WidgetTestFrame(WindowedContainerTest::createWindow)) {
            frame.run();
        }
    }

    private static WindowedContainer createWindow() {
        var root = new WindowedContainer();
        root.asTreeRoot();
        root.inlineStyle("background-color: 0xFF020617;");
        root.setStyle(s -> s.size = TaffySize.all(TaffyDimension.percent(1f)));

        root.addSubWindow(createPanel("第一个窗口", 0xFF1E293B), 320, 220);
        root.addSubWindow(createPanel("自定义外框 CSS", 0xFF312E81), 420, 260, """
                background-color: 0xFF172554;
                border: 2px;
                border-color: 0xFF60A5FA;
                """);
        root.addSubWindow(createPanel("指定位置窗口", 0xFF064E3B), 520, 120, 300, 200);

        return root;
    }

    private static ContainerWidget createPanel(String title, int color) {
        var panel = new ContainerWidget();
        panel.inlineStyle("background-color: " + color + ";");
        panel.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.justifyContent = AlignContent.CENTER;
            s.alignItems = AlignItems.CENTER;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });

        var label = new Label(title, 0xFFE2E8F0);
        label.inlineStyle("text-align: center;");
        label.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.9f), TaffyDimension.length(32)));
        panel.addChild(label);

        var hint = new Label("拖动顶部移动窗口，拖动边框调整大小，点击 X 关闭", 0xFFCBD5E1);
        hint.inlineStyle("text-align: center;");
        hint.setStyle(s -> s.size = TaffySize.of(TaffyDimension.percent(0.9f), TaffyDimension.length(32)));
        panel.addChild(hint);

        var button = new Button("内容按钮", () -> System.out.println("window content button clicked"));
        button.inlineStyle("""
                background-color: 0xFF334155;
                text-color: 0xFFE2E8F0;
                text-align: center;
                """);
        button.setStyle(s -> s.size = TaffySize.of(TaffyDimension.length(120), TaffyDimension.length(32)));
        panel.addChild(button);

        return panel;
    }
}
