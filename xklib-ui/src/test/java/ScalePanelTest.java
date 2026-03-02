import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.widget.layout.ScalePanel;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.widgets.Button;

public class ScalePanelTest {

    public static void main(String[] args) {
        new WidgetTestFrame(() -> {
            var root = new FlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.ROW)
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
            var innerFlex = new FlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.START)
                    .align(FlexParam.Align.START)
                    .overflow(true)
                    .build());

            for (int i = 0; i < 20; i++) {
                int idx = i;
                var btn = new Button("Btn " + i, () -> System.out.println("Clicked Btn " + idx));
                btn.setMargin(2, 2, 2, 2);
                innerFlex.addChild(btn, FlexElementParam.of(i, "200px", "40px"));
            }

            scalePanel.setChild(innerFlex);
            root.addChild(scalePanel, FlexElementParam.of(0, "80%", "80%"));

            return root;
        }).run();
    }
}
