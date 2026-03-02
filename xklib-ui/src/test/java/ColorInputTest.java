import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.widgets.ColorInput;
import com.xkball.xklib.ui.widget.widgets.Label;

public class ColorInputTest {

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

            var resultLabel = new Label("color: 0xFF6495ED", 16, 0xFFFFFFFF);

            var colorInput = new ColorInput(0xFF6495ED);
            colorInput.setOnConfirm(c -> resultLabel.setText(String.format("confirmed: 0x%08X", c)));
            colorInput.setOnCancel(c -> resultLabel.setText(String.format("cancelled: 0x%08X", c)));

            root.addChild(resultLabel, FlexElementParam.of(0, "400px", "24px"));
            root.addChild(colorInput, FlexElementParam.of(1, "400px", "200px"));

            return root;
        }).run();
    }
}
