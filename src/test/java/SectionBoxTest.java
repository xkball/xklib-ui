import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.layout.ScrollableFlexLayout;
import com.xkball.xklib.ui.widget.layout.SectionBox;
import com.xkball.xklib.ui.widget.widgets.Button;
import com.xkball.xklib.ui.widget.widgets.Label;

public class SectionBoxTest {

    public static void main(String[] args) {
        new WidgetTestFrame(() -> {
            var root = new ScrollableFlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.START)
                    .align(FlexParam.Align.CENTER)
                    .overflow(true)
                    .build()) {
                @Override
                public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                    graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF2D3748);
                    super.render(graphics, mouseX, mouseY, a);
                }
            };
            root.setYScrollable();

            var section1 = new SectionBox("基本信息");
            for (int i = 0; i < 3; i++) {
                int idx = i;
                var label = new Label("字段 " + idx + ": 值 " + idx, 20, 0xFF000000);
                section1.setMargin("10px");
                section1.addContent(label, FlexElementParam.of(idx, "100%", "24px"));
            }

            var section2 = new SectionBox("操作");
            for (int i = 0; i < 5; i++) {
                int idx = i;
                var btn = new Button("按钮 " + idx, () -> System.out.println("clicked " + idx));
                btn.setMargin(2, 2, 2, 2);
                section2.setMargin("10px");
                section2.addContent(btn, FlexElementParam.of(idx, "90%", "28px"));
            }

            var section3 = new SectionBox("扩展选项");
            for (int i = 0; i < 8; i++) {
                int idx = i;
                var label = new Label("选项 " + idx, 18, 0xFF000000);
                section3.setMargin("10px");
                section3.addContent(label, FlexElementParam.of(idx, "100%", "22px"));
            }

            root.addChild(section1, FlexElementParam.of(0, "80%", "28px"));
            root.addChild(section2, FlexElementParam.of(1, "80%", "28px"));
            root.addChild(section3, FlexElementParam.of(2, "80%", "28px"));

            return root;
        }).run();
    }
}
