import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.widgets.ComboBox;
import com.xkball.xklib.ui.widget.widgets.Label;

import java.util.List;

public class ComboBoxTest {

    enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public static void main(String[] args) {
        new WidgetTestFrame(() -> {
            var root = new FlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.SPACE_AROUND)
                    .align(FlexParam.Align.CENTER)
                    .overflow(true)
                    .build()) {
                @Override
                public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                    graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF2D3748);
                    super.render(graphics, mouseX, mouseY, a);
                }
            };

            var enumCombo = ComboBox.ofEnum(Direction.class, true);
            enumCombo.setFixSize(200, 32);

            var stringCombo = new ComboBox<>(List.of("Apple", "Banana", "Cherry", "Date"), s -> s, false);
            stringCombo.setFixSize(200, 32);

            var charCombo = ComboBox.ofString(List.of("A","B","C","D","E","F","G","H","I","J","K","LOOOOONNNNNNG"),true);
            charCombo.setFixSize(200, 32);
            
            root.addChild(enumCombo, FlexElementParam.of(0, "200px", "32px"));
            root.addChild(stringCombo, FlexElementParam.of(1, "200px", "32px"));
            root.addChild(charCombo, FlexElementParam.of(2, "200px", "32px"));
            

            return root;
        }).run();
    }
}
