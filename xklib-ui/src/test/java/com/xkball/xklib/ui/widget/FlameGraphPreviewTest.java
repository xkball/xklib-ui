package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.utils.AdjacencyList;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;

public class FlameGraphPreviewTest {

    private static long frameCount = 0;

    static void main(String[] ignored) throws Exception {
        var graph = new FlameGraph();
        graph.setStyle(s -> s.size = TaffySize.all(TaffyDimension.percent(1f)));

        try (var frame = new WidgetTestFrame(() -> {
            var root = new ContainerWidget();
            root.asTreeRoot();
            root.setStyle(s -> {
                s.flexDirection = FlexDirection.COLUMN;
                s.alignItems = AlignItems.STRETCH;
                s.size = TaffySize.all(TaffyDimension.percent(1f));
            });
            root.addChild(graph);
            return root;
        }) {
            @Override
            public void render() {
                frameCount++;
                if (frameCount % 12 == 0) {
                    graph.setData(buildData().copy());
                }
                super.render();
            }
        }) {
            frame.run();
        }
    }

    private static AdjacencyList<String, Long> buildData() {
        var data = new AdjacencyList<String, Long>();
        data.addNode("frame");
        data.addNode("gui", "frame");
        data.addNode("logic", "frame");
        data.addNode("render", "gui");
        data.addNode("layout", "gui");
        data.addNode("ai", "logic");
        data.addNode("physics", "logic");

        data.setData("frame", 16_000_000L);
        data.setData("gui", 6_000_000L);
        data.setData("logic", 4_000_000L);
        data.setData("render", 3_000_000L);
        data.setData("layout", 2_000_000L);
        data.setData("ai", 1_500_000L);
        data.setData("physics", 1_200_000L);
        return data;
    }
}

