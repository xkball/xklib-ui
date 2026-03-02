import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.HorizontalAlign;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.layout.ScalePanel;
import com.xkball.xklib.ui.widget.widgets.DragBox;
import com.xkball.xklib.ui.widget.widgets.Label;
import com.xkball.xklib.ui.widget.widgets.TextField;

public class ScalePanelTextFieldDragBoxTest {

    public static void main(String[] args) {
        new WidgetTestFrame(() -> {
            var root = new FlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.CENTER)
                    .align(FlexParam.Align.CENTER)
                    .overflow(true)
                    .build()) {
                @Override
                public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                    graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF1A202C);
                    super.render(graphics, mouseX, mouseY, a);
                }
            };

            var scalePanel = new ScalePanel();
            scalePanel.setAllowUserScale(false);
            

            var textField = new TextField();
            textField.setText("可缩放的文本框");
            textField.setLineHeight(20);
            textField.setAutoGrow(true);
            textField.setInnerUseMinWidth(true);
            textField.setInnerUseMinHeight(true).setHorizontalAlign(HorizontalAlign.CENTER);
            textField.setFlexParam(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.CENTER)
                    .align(FlexParam.Align.CENTER)
                    .overflow(false)
                    .build());
            
            scalePanel.setChild(textField);

            var controlLayout = new FlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.CENTER)
                    .align(FlexParam.Align.CENTER)
                    .overflow(false)
                    .build());

            var label = new Label("拖动下方控制条调整上方文本框大小", 16, 0xFFFFFFFF);
            var sizeLabel = new Label("size: 1.00", 14, 0xFFCCCCCC);

            var dragBox = new DragBox(0.5, 2.0, 1.0);
            dragBox.setOnChange(v -> {
                scalePanel.setScaleKeepCenter(v);
                sizeLabel.setText(String.format("size: %.2f", v));
            });

            controlLayout.addChild(label, FlexElementParam.of(0, "80%", "24px"));
            controlLayout.addChild(sizeLabel, FlexElementParam.of(1, "80%", "20px"));
            controlLayout.addChild(dragBox, FlexElementParam.of(2, "80%", "20px"));

            root.addChild(scalePanel, FlexElementParam.of(0, "80%", "70%"));
            root.addChild(controlLayout, FlexElementParam.of(1, "80%", "30%"));

            return root;
        }).run();
    }
}
