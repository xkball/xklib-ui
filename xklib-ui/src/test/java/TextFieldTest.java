import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.HorizontalAlign;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.widgets.Label;
import com.xkball.xklib.ui.widget.widgets.TextField;

public class TextFieldTest {

    public static void main(String[] args) {
        new WidgetTestFrame(() -> {
            var rootFlex = new FlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.ROW)
                    .justify(FlexParam.Align.START)
                    .align(FlexParam.Align.START)
                    .overflow(false)
                    .build()) {
                @Override
                public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                    graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF2D3748);
                    super.render(graphics, mouseX, mouseY, a);
                }
            };
            
            var leftColumn = new FlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.START)
                    .align(FlexParam.Align.CENTER)
                    .overflow(false)
                    .build());
            rootFlex.addChild(leftColumn, FlexElementParam.of(0, "50%", "100%"));
            
            var rightColumn = new FlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.START)
                    .align(FlexParam.Align.CENTER)
                    .overflow(false)
                    .build());
            rootFlex.addChild(rightColumn, FlexElementParam.of(1, "50%", "100%"));
            
            var label1 = new Label("1. 基础多行输入 (自动增长)", 16, 0xFFFFFFFF);
            leftColumn.addChild(label1, FlexElementParam.of(0, "90%", "20px"));
            
            var textField1 = new TextField();
            textField1.setText("Hello World!\n你好世界!\nThis is a multi-line text field.");
            textField1.setLineHeight(18);
            textField1.setLineSpacing(4);
            textField1.setAutoGrow(true);
            textField1.setDefaultLineCount(3);
            textField1.setMargin(5, 5, 5, 10);
            leftColumn.addChild(textField1, FlexElementParam.of(1, "90%", "100px"));
            
            var label2 = new Label("2. 显示行底线 + 最大5行", 16, 0xFFFFFFFF);
            leftColumn.addChild(label2, FlexElementParam.of(2, "90%", "20px"));
            
            var textField2 = new TextField();
            textField2.setText("每行下方显示底线\n最多只能输入5行");
            textField2.setLineHeight(20);
            textField2.setShowLineBottom(true);
            textField2.setLineBottomColor(0x60000000);
            textField2.setMaxLines(5);
            textField2.setAutoGrow(true);
            textField2.setMargin(5, 5, 5, 10);
            leftColumn.addChild(textField2, FlexElementParam.of(3, "90%", "150px"));
            
            var label3 = new Label("3. 自动换行 (禁止X轴溢出)", 16, 0xFFFFFFFF);
            leftColumn.addChild(label3, FlexElementParam.of(4, "90%", "20px"));
            
            var textField3 = new TextField();
            textField3.setText("这是一段很长的文字，当它超出可见区域时会自动换行而不是滚动。This is a long text that will wrap automatically instead of scrolling.");
            textField3.setLineHeight(16);
            textField3.setAllowXOverflow(false);
            textField3.setAutoGrow(true);
            textField3.setMargin(5, 5, 5, 10);
            leftColumn.addChild(textField3, FlexElementParam.of(5, "90%", "120px"));
            
            var label4 = new Label("4. 固定高度 + 可滚动", 16, 0xFFFFFFFF);
            rightColumn.addChild(label4, FlexElementParam.of(0, "90%", "20px"));
            
            var textField4 = new TextField();
            textField4.setText("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\nLine 6\nLine 7\nLine 8\nLine 9\nLine 10");
            textField4.setLineHeight(18);
            textField4.setAutoGrow(false);
            textField4.setMargin(5, 5, 5, 10);
            rightColumn.addChild(textField4, FlexElementParam.of(1, "90%", "100px"));
            
            var label5 = new Label("5. 居中对齐", 16, 0xFFFFFFFF);
            rightColumn.addChild(label5, FlexElementParam.of(2, "90%", "20px"));
            
            var textField5 = new TextField();
            textField5.setText("居中对齐的文字\nCentered Text\n第三行");
            textField5.setLineHeight(20);
            textField5.setHorizontalAlign(HorizontalAlign.CENTER);
            textField5.setAutoGrow(true);
            textField5.setMargin(5, 5, 5, 10);
            rightColumn.addChild(textField5, FlexElementParam.of(3, "90%", "100px"));
            
            var label6 = new Label("6. 右对齐", 16, 0xFFFFFFFF);
            rightColumn.addChild(label6, FlexElementParam.of(4, "90%", "20px"));
            
            var textField6 = new TextField();
            textField6.setText("右对齐的文字\nRight Aligned\n123456");
            textField6.setLineHeight(20);
            textField6.setHorizontalAlign(HorizontalAlign.RIGHT);
            textField6.setAutoGrow(true);
            textField6.setMargin(5, 5, 5, 10);
            rightColumn.addChild(textField6, FlexElementParam.of(5, "90%", "100px"));
            
            var label7 = new Label("7. 深色主题 + 禁用状态", 16, 0xFFFFFFFF);
            rightColumn.addChild(label7, FlexElementParam.of(6, "90%", "20px"));
            
            var textField7 = new TextField();
            textField7.setText("禁用状态\n可以选中复制\n但不能编辑");
            textField7.setLineHeight(18);
            textField7.setBackgroundColor(0xFF1A202C);
            textField7.setTextColor(0xFFFFFFFF);
            textField7.setCursorColor(0xFFFFFFFF);
            textField7.setBorderColor(0xFF4A5568);
            textField7.setFocusedBorderColor(0xFF63B3ED);
            textField7.setSelectionColor(0x6063B3ED);
            textField7.setEnabled(false);
            textField7.setAutoGrow(true);
            textField7.setMargin(5, 5, 5, 10);
            rightColumn.addChild(textField7, FlexElementParam.of(7, "90%", "100px"));
            
            var label8 = new Label("8. 使用最小宽高 (可居中)", 16, 0xFFFFFFFF);
            leftColumn.addChild(label8, FlexElementParam.of(6, "90%", "20px"));
            
            var textField8 = new TextField();
            textField8.setText("短文字");
            textField8.setLineHeight(20);
            textField8.setAutoGrow(true);
            textField8.setInnerUseMinWidth(true);
            textField8.setInnerUseMinHeight(true).setHorizontalAlign(HorizontalAlign.CENTER);
            textField8.setFlexParam(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.CENTER)
                    .align(FlexParam.Align.CENTER)
                    .overflow(false)
                    .build());
            textField8.setMargin(5, 5, 5, 10);
            leftColumn.addChild(textField8, FlexElementParam.of(7, "90%", "80px"));
            
            return rootFlex;
        }).run();
    }
}
