package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklib.ui.widget.TextDisplay;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;


public class SimpleTextSplitterTest {

    private static final String TEST_ARTICLE = """
            文本分割算法测试
            
            这是一个测试中文文本分割的示例文章。在中文排版中，有一些特殊的规则需要遵守。例如，逗号、句号、感叹号、问号等标点符号不能出现在行首。这是为了保持文本的美观和可读性。
            
            English text should also be handled properly. Long words like "internationalization" and "telecommunication" might need to be broken if they exceed the line width by more than 10%. Spaces and punctuation marks can serve as natural break points.
            
            成对的标点符号（如括号「引号」【方括号】〈书名号〉）需要特殊处理。前半部分可以出现在行首，但后半部分不应该出现在行首。这样可以确保成对标点符号的完整性。
            
            混合文本 Mixed Text：当中英文混排时，算法需要正确处理两种语言的断行规则。This is important for internationalization (i18n) and localization (l10n) purposes.
            
            连续标点符号的处理：如果标点符号连续出现，如"……"、"——"等，当它们太长时也可以分行。但一般情况下应该保持在同一行。
            
            数字和符号：1234567890、ABCDEFGHIJKLMNOPQRSTUVWXYZ、abcdefghijklmnopqrstuvwxyz等也需要正确处理。
            
            超长单词测试：antidisestablishmentarianism、pneumonoultramicroscopicsilicovolcanoconiosis、hippopotomonstrosesquippedaliophobia这些超长单词需要在必要时进行分割。
            
            最后，让我们用一些古诗词来测试：
            "床前明月光，疑是地上霜。举头望明月，低头思故乡。"
            "春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少？"
            """;

    public static void main(String[] ignored) throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new SplitContainer(false);
            root.asTreeRoot();

            var leftSplit = new SplitContainer(false);
            root.setPanel(0,leftSplit);

          

            return root;
        })) {
            frame.run();
        }
    }

    private static ContainerWidget createTextPanel(String title) {
        var panel = new ContainerWidget();
        panel.inlineStyle("background-color: 0xFF1E293B;");
        panel.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.justifyContent = AlignContent.START;
            s.alignItems = AlignItems.STRETCH;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });

        var titleLabel = new Label(title, TextAlign.CENTER, 0xFFE2E8F0);
        titleLabel.inlineStyle("background-color: 0xFF334155;");
        titleLabel.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(30));
            s.flexShrink = 0;
        });
        panel.addChild(titleLabel);

        var textContainer = new TextDisplay(TEST_ARTICLE);
        panel.addChild(textContainer);

        return panel;
    }
    
}

