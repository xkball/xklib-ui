package com.xkball.xklib.ui.layout;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.ContainerWidget;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.SplitContainer;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.CalcExpression;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyStyle;
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
            root.setFirst(leftSplit);

            var topLeft = createTextPanel("测试1");
            leftSplit.setFirst(topLeft);

            var bottomLeft = createTextPanel("测试2");
            leftSplit.setSecond(bottomLeft);

            var rightPanel = createTextPanel("测试3");
            root.setSecond(rightPanel);

            return root;
        })) {
            frame.run();
        }
    }

    private static ContainerWidget createTextPanel(String title) {
        var panel = new ContainerWidget();
        panel.addDecoration(new Background(0xFF1E293B));
        panel.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.justifyContent = AlignContent.START;
            s.alignItems = AlignItems.STRETCH;
            s.size = TaffySize.all(TaffyDimension.percent(1f));
        });

        var titleLabel = new Label(title, TextAlign.CENTER, 0xFFE2E8F0);
        titleLabel.addDecoration(new Background(0xFF334155));
        titleLabel.setStyle(s -> {
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(30));
            s.flexShrink = 0;
        });
        panel.addChild(titleLabel);

        var textContainer = new TextDisplayContainer(TEST_ARTICLE);
        panel.addChild(textContainer);

        return panel;
    }

    private static class TextDisplayContainer extends ContainerWidget {
        private final String text;
        private int lastWidth = -1;

        public TextDisplayContainer(String text) {
            this.text = text;
        }

        @Override
        public void init() {
            super.init();
            this.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.justifyContent = AlignContent.START;
                s.alignItems = AlignItems.STRETCH;
                s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.calc(CalcExpression.percentMinusLength(1,30)));
            });
            this.setYScroll(true);
        }
        
        @Override
        public void resize(float offsetX, float offsetY) {
            super.resize(offsetX, offsetY);
            int currentWidth = (int) this.width;
            if (currentWidth > 0 && currentWidth != lastWidth) {
                lastWidth = currentWidth;
                this.submitTreeUpdate(this::rebuildLines);
            }
        }
        
        private void rebuildLines() {
            this.clearChildren();
            
            var graphics = GuiSystem.INSTANCE.get().getGuiGraphics();
            if (graphics != null && lastWidth > 0) {
                var font = graphics.defaultFont();
                var splitter = new SimpleTextSplitter();
                var lines = splitter.split(font, this.text, lastWidth);

                for (int i = 0; i < lines.size(); i++) {
                    var line = lines.get(i);
                    var lineLabel = new Label(line, TextAlign.LEFT, 0xFFE2E8F0);
                    lineLabel.addDecoration(new Background(i % 2 == 0 ? 0xFF475569 : 0xFF3F4A5B));
                    lineLabel.setStyle(s -> {
                        s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(32 * (1/0.9f)));
                        s.flexShrink = 0;
                    });
                    this.addChild(lineLabel);
                }
            }
        }
    }
}

