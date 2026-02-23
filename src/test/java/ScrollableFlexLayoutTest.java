import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.layout.FlexLayout;
import com.xkball.xklib.ui.widget.layout.ScrollableFlexLayout;

public class ScrollableFlexLayoutTest {
    
    public static void verticalScrollDemo() {
        new WidgetTestFrame(() -> {
            var layer1 = new ScrollableFlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
                    .justify(FlexParam.Align.START)
                    .align(FlexParam.Align.CENTER)
                    .overflow(false)
                    .build()) {
                @Override
                public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                    graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF2D3748);
                    super.render(graphics, mouseX, mouseY, a);
                }
            };
            layer1.yScrollable = true;
            
            var header = new Button("Layer 1 Header", () -> {});
            header.setMargin(5, 5, 5, 5);
            layer1.addChild(header, FlexElementParam.of(0, "100%", "50px"));
            
            for (int i = 0; i < 5; i++) {
                int idx = i;
                var layer2 = new ScrollableFlexLayout(new FlexParam.Builder()
                        .direction(FlexParam.Direction.COL)
                        .justify(FlexParam.Align.START)
                        .align(FlexParam.Align.CENTER)
                        .overflow(false)
                        .build()) {
                    @Override
                    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF4A5568);
                        super.render(graphics, mouseX, mouseY, a);
                    }
                };
                layer2.yScrollable = true;
                layer2.setMargin(5, 5, 5, 5);
                
                var layer2Header = new Button("Layer 2-" + idx + " Header", () -> {});
                layer2Header.setMargin(3, 3, 3, 3);
                layer2.addChild(layer2Header, FlexElementParam.of(0, "100%", "40px"));
                
                for (int j = 0; j < 8; j++) {
                    int jdx = j;
                    var layer3 = new ScrollableFlexLayout(new FlexParam.Builder()
                            .direction(FlexParam.Direction.COL)
                            .justify(FlexParam.Align.START)
                            .align(FlexParam.Align.CENTER)
                            .overflow(false)
                            .build()) {
                        @Override
                        public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                            graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF718096);
                            super.render(graphics, mouseX, mouseY, a);
                        }
                    };
                    layer3.yScrollable = true;
                    layer3.setMargin(3, 3, 3, 3);
                    
                    var layer3Header = new Button("L3-" + idx + "-" + jdx, () ->
                            System.out.println("Clicked L3-" + idx + "-" + jdx));
                    layer3Header.setMargin(2, 2, 2, 2);
                    layer3.addChild(layer3Header, FlexElementParam.of(0, "100%", "30px"));
                    
                    for (int k = 0; k < 20; k++) {
                        int kdx = k;
                        var btn = new Button("Item " + k, () ->
                                System.out.println("Clicked " + idx + "-" + jdx + "-" + kdx));
                        btn.setMargin(2, 2, 2, 2);
                        layer3.addChild(btn, FlexElementParam.of(k + 1, "90%", "35px"));
                    }
                    
                    layer2.addChild(layer3, FlexElementParam.of(j + 1, "90%", "200px"));
                }
                
                layer1.addChild(layer2, FlexElementParam.of(i + 1, "90%", "300px"));
            }
            
            return layer1;
        }).run();
    }
    
    public static void horizontalScrollDemo() {
        new WidgetTestFrame(() -> {
            var container = new ScrollableFlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.ROW)
                    .justify(FlexParam.Align.START)
                    .align(FlexParam.Align.CENTER)
                    .overflow(false)
                    .build()) {
                @Override
                public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                    graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF2D3748);
                    super.render(graphics, mouseX, mouseY, a);
                }
            };
            container.xScrollable = true;
            container.xScrollBarSize = 12;
            
            for (int i = 0; i < 20; i++) {
                int idx = i;
                var btn = new Button("Column " + i, () -> System.out.println("Clicked Column " + idx));
                btn.setMargin(5, 5, 5, 5);
                container.addChild(btn, FlexElementParam.of(i, "150px", "80%"));
            }
            
            return container;
        }).run();
    }
    
    public static void bothAxisScrollDemo() {
        new WidgetTestFrame(() -> {
            var container = new ScrollableFlexLayout(new FlexParam.Builder()
                    .direction(FlexParam.Direction.COL)
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
            container.xScrollable = true;
            container.yScrollable = true;
            container.xScrollBarSize = 10;
            container.yScrollBarSize = 10;
            
            for (int i = 0; i < 30; i++) {
                int idx = i;
                var row = new FlexLayout(new FlexParam.Builder()
                        .direction(FlexParam.Direction.ROW)
                        .justify(FlexParam.Align.START)
                        .align(FlexParam.Align.CENTER)
                        .overflow(false)
                        .build()) {
                    @Override
                    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
                        int color = (idx % 2 == 0) ? 0xFF4A5568 : 0xFF718096;
                        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, color);
                        super.render(graphics, mouseX, mouseY, a);
                    }
                };
                
                for (int j = 0; j < 15; j++) {
                    int jdx = j;
                    var btn = new Button("(" + i + "," + j + ")", () ->
                            System.out.println("Clicked (" + idx + "," + jdx + ")"));
                    btn.setMargin(3, 3, 3, 3);
                    row.addChild(btn, FlexElementParam.of(j, "120px", "90%"));
                }
                
                container.addChild(row, FlexElementParam.of(i, "1800px", "50px"));
            }
            
            return container;
        }).run();
    }
    
    public static void main(String[] args) {
        Thread.currentThread().setName("xklib-ui-test-main");
//        horizontalScrollDemo();
//        bothAxisScrollDemo();
        verticalScrollDemo();
    
    }
}
