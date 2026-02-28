import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.widget.widgets.EditBox;
import com.xkball.xklib.ui.widget.layout.FlexLayout;

public class EditBoxTest {

    public static void main(String[] args) {
        new WidgetTestFrame(() -> {
            var rootFlex = new FlexLayout(new FlexParam.Builder()
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
            
            var editBox1 = new EditBox();
            editBox1.setText("Hello World! 你好世界!");
            editBox1.setLineHeight(24);
            editBox1.setMargin(10, 10, 10, 10);
            rootFlex.addChild(editBox1, FlexElementParam.of(0, "80%", "60px"));
            
            var editBox2 = new EditBox();
            editBox2.setText("This is a longer text that will scroll when it exceeds the visible area. 这是一段很长的文字，当它超出可见区域时会滚动。");
            editBox2.setLineHeight(20);
            editBox2.setBoxHeight(50);
            editBox2.setMargin(10, 10, 10, 10);
            editBox2.setEnabled(false);
            rootFlex.addChild(editBox2, FlexElementParam.of(1, "80%", "70px"));
            
            var editBox3 = new EditBox();
            editBox3.setLineHeight(16);
            editBox3.setBackgroundColor(0xFF1A202C);
            editBox3.setTextColor(0xFFFFFFFF);
            editBox3.setCursorColor(0xFFFFFFFF);
            editBox3.setBorderColor(0xFF4A5568);
            editBox3.setFocusedBorderColor(0xFF63B3ED);
            editBox3.setSelectionColor(0x6063B3ED);
            editBox3.setMargin(10, 10, 10, 10);
            rootFlex.addChild(editBox3, FlexElementParam.of(2, "80%", "50px"));
            
            return rootFlex;
        }).run();
    }
}
