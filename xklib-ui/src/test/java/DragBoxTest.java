import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.widgets.DragBox;
import com.xkball.xklib.ui.widget.widgets.Label;

public class DragBoxTest {

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
                    graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF2D3748);
                    super.render(graphics, mouseX, mouseY, a);
                }
            };

            var valueLabel = new Label("value: 0.00", 18, 0xFFFFFFFF);

            var drag1 = new DragBox(0, 100, 50);
            drag1.setOnChange(v -> valueLabel.setText(String.format("value: %.2f", v)));

            var drag2 = new DragBox(-1.0, 1.0);
            var label2 = new Label("range: -1 ~ 1", 16, 0xFFCCCCCC);

            root.addChild(valueLabel, FlexElementParam.of(0, "300px", "24px"));
            root.addChild(drag1, FlexElementParam.of(1, "300px", "20px"));
            root.addChild(label2, FlexElementParam.of(2, "300px", "20px"));
            root.addChild(drag2, FlexElementParam.of(3, "300px", "20px"));

            return root;
        }).run();
    }
}
