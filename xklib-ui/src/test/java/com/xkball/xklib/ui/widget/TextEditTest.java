package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.layout.DefaultStyles;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;

public class TextEditTest {

    private static final String INITIAL_TEXT = "这是一个测试文本编辑器的示例。\n可以进行多行编辑。\n支持选择、复制、粘贴等功能。";

    public static void main(String[] ignored) throws Exception {
        try (var frame = new WidgetTestFrame(() -> {
            var root = new SplitContainer(false);
            root.asTreeRoot();

//            var leftPanel = createEditPanel("单行编辑", false, true, true);
//            root.setFirst(new ContainerWidget());
           
            var rightSplit = new SplitContainer(false);

//            var topRight = createEditPanel("多行编辑 - 自动换行", true, true, true);
            var textEdit3 = createEditPanel("多行编辑 - 不换行", true, false, true);
//            rightSplit.setFirst(textEdit3);
//            rightSplit.setSecond(new ContainerWidget());
//            root.setSecond(rightSplit);
            return root;
        })) {
            frame.run();
        }
    }

    private static ContainerWidget createEditPanel(String title, boolean multiLine, boolean wrapLine, boolean allowEdit) {
        var panel = new ContainerWidget();
        panel.inlineStyle("background-color: 0xFF1E293B;");
        panel.applyStyle(DefaultStyles::fill);
        panel.setStyle(s -> s.flexDirection = FlexDirection.COLUMN);

        var titleLabel = new Label(title, TextAlign.CENTER, 0xFFE2E8F0);
        titleLabel.inlineStyle("background-color: 0xFF334155;");
        titleLabel.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(30)));
        panel.addChild(titleLabel);

        var scrollContainer = new ContainerWidget(){
            @Override
            public void resize(float offsetX, float offsetY) {
                super.resize(offsetX, offsetY);
                var layout = this.getLayout();
                var l = layout.contentBoxWidth();
            }
        };
        scrollContainer.inlineStyle("background-color: 0xFF0F172A;");
        scrollContainer.setStyle(s -> s.size = TaffySize.all(TaffyDimension.percent(1)));
        scrollContainer.setStyle(s -> s.minSize = TaffySize.of(TaffyDimension.ZERO,TaffyDimension.ZERO));
        if (multiLine) {
            if (wrapLine) {
                scrollContainer.setYScrollEnable();
            } else {
                scrollContainer.setXScrollEnable();
                scrollContainer.setYScrollEnable();
            }
        } else {
            scrollContainer.setXScrollEnable();
        }
        
        var textEdit = new TextEdit(multiLine ? INITIAL_TEXT : "单行文本编辑器");
        textEdit.setMultiLine(multiLine);
        textEdit.setWrapLine(wrapLine);
        textEdit.setAllowEdit(allowEdit);
        textEdit.setTextAlign(TextAlign.LEFT);
        
        scrollContainer.addChild(textEdit);
        panel.addChild(scrollContainer);
        
        return panel;
    }
}

