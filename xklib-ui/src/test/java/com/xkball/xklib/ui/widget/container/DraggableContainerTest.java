package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.widget.Label;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;

public class DraggableContainerTest {

    public static void main(String[] ignored) {
        Thread thread1 = new Thread(() -> {
            try (var frame = new WidgetTestFrame(DraggableContainerTest::createAbsoluteContainerWindow)) {
                frame.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread2 = new Thread(() -> {
            try (var frame = new WidgetTestFrame(DraggableContainerTest::createScalableContainerWindow)) {
                frame.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

//        thread1.start();
        thread2.start();
    }

    private static ContainerWidget createAbsoluteContainerWindow() {
        var root = new ContainerWidget();
        root.asTreeRoot();
        root.inlineStyle("background-color: 0xFF1A1A2E;");
        var rootStyle = new TaffyStyle();
        rootStyle.flexDirection = FlexDirection.COLUMN;
        rootStyle.justifyContent = AlignContent.CENTER;
        rootStyle.alignItems = AlignItems.CENTER;
        rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        root.setStyle(rootStyle);

        var title = new Label("AbsoluteContainer - 拖动测试",  0xFFFFFFFF);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(35));
        titleStyle.flexShrink = 0;
        root.addChild(title, titleStyle);

        var hint = new Label("左键拖动可移动容器",  0xFFCCCCCC);
        var hintStyle = new TaffyStyle();
        hintStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(20));
        hintStyle.flexShrink = 0;
        root.addChild(hint, hintStyle);

        var absoluteContainer = new AbsoluteContainer();
        absoluteContainer.inlineStyle("background-color: 0xFF0D1117;");
        var absStyle = new TaffyStyle();
        absStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.percent(0.85f));
        root.addChild(absoluteContainer, absStyle);

        var box1 = createDraggableBox("拖动盒子 A", 0xFFE74C3C, 80, 80, 180, 140);
        absoluteContainer.addChild(box1);

        var box2 = createDraggableBox("拖动盒子 B", 0xFF3498DB, 320, 120, 200, 160);
        absoluteContainer.addChild(box2);

        var box3 = createDraggableBox("拖动盒子 C", 0xFF2ECC71, 180, 320, 190, 150);
        absoluteContainer.addChild(box3);

        var box4 = createDraggableBox("拖动盒子 D", 0xFF9B59B6, 550, 200, 210, 170);
        absoluteContainer.addChild(box4);

        var boxWithChildren = createDraggableBox("包含子组件的盒子", 0xFFF39C12, 100, 520, 300, 200);
        var childLabel1 = new Label("子标签 - 第一行",  0xFF000000);
        var childStyle1 = new TaffyStyle();
        childStyle1.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(22));
        boxWithChildren.addChild(childLabel1, childStyle1);

        var childLabel2 = new Label("子标签 - 第二行",  0xFF000000);
        var childStyle2 = new TaffyStyle();
        childStyle2.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(22));
        boxWithChildren.addChild(childLabel2, childStyle2);

        var childLabel3 = new Label("拖动外层容器移动",  0xFF000000);
        var childStyle3 = new TaffyStyle();
        childStyle3.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(22));
        boxWithChildren.addChild(childLabel3, childStyle3);

        absoluteContainer.addChild(boxWithChildren);

        return root;
    }

    private static ContainerWidget createScalableContainerWindow() {
        var root = new ContainerWidget();
        root.asTreeRoot();
        root.inlineStyle("background-color: 0xFF0F172A;");
        var rootStyle = new TaffyStyle();
        rootStyle.flexDirection = FlexDirection.COLUMN;
        rootStyle.justifyContent = AlignContent.CENTER;
        rootStyle.alignItems = AlignItems.CENTER;
        rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        root.setStyle(rootStyle);

        var title = new Label("ScalableContainer - 拖动测试",  0xFFFFFFFF);
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(35));
        titleStyle.flexShrink = 0;
        root.addChild(title, titleStyle);

        var hint = new Label("滚轮缩放画布 | Shift+滚轮垂直平移 | Ctrl+滚轮水平平移 | 左键拖动盒子移动",  0xFFCCCCCC);
        var hintStyle = new TaffyStyle();
        hintStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(20));
        hintStyle.flexShrink = 0;
        root.addChild(hint, hintStyle);

        var scalableContainer = new ScalableContainer();
        scalableContainer.inlineStyle("background-color: 0xFF1E293B;");
        var scStyle = new TaffyStyle();
        scStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.percent(0.85f));
        root.addChild(scalableContainer, scStyle);

        var box1 = createDraggableBox("拖动盒子 1", 0xFFFF6B6B, 100, 100, 180, 140);
        scalableContainer.addChild(box1);

        var box2 = createDraggableBox("拖动盒子 2", 0xFF4ECDC4, 350, 150, 200, 160);
        scalableContainer.addChild(box2);

        var box3 = createDraggableBox("拖动盒子 3", 0xFF45B7D1, 200, 380, 190, 150);
        scalableContainer.addChild(box3);

        var box4 = createDraggableBox("拖动盒子 4", 0xFF96CEB4, 600, 250, 210, 170);
        scalableContainer.addChild(box4);

        var box5 = createDraggableBox("拖动盒子 5", 0xFFFFAA5A, 450, 500, 180, 140);
        scalableContainer.addChild(box5);

        var boxWithChildren = createDraggableBox("复杂盒子", 0xFFFECEA8, 120, 650, 320, 230);
        var childLabel1 = new Label("这个盒子包含多个子组件",  0xFF000000);
        var childStyle1 = new TaffyStyle();
        childStyle1.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(22));
        boxWithChildren.addChild(childLabel1, childStyle1);

        var childLabel2 = new Label("子组件不响应拖动事件",  0xFF000000);
        var childStyle2 = new TaffyStyle();
        childStyle2.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(22));
        boxWithChildren.addChild(childLabel2, childStyle2);

        var childLabel3 = new Label("拖动容器本身可以移动",  0xFF000000);
        var childStyle3 = new TaffyStyle();
        childStyle3.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(22));
        boxWithChildren.addChild(childLabel3, childStyle3);

        var childLabel4 = new Label("测试缩放后的拖动行为",  0xFF000000);
        var childStyle4 = new TaffyStyle();
        childStyle4.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(22));
        boxWithChildren.addChild(childLabel4, childStyle4);

        scalableContainer.addChild(boxWithChildren);

        return root;
    }

    private static DraggableContainer createDraggableBox(String text, int color, float x, float y, float width, float height) {
        var container = new DraggableContainer();
        container.setAbsoluteSize(x,y);
        container.setWidth(width);
        container.setHeight(height);
        container.inlineStyle("background-color: " + color + ";");

        var containerStyle = new TaffyStyle();
        containerStyle.flexDirection = FlexDirection.COLUMN;
        containerStyle.justifyContent = AlignContent.CENTER;
        containerStyle.alignItems = AlignItems.CENTER;
        containerStyle.gap = TaffySize.of(LengthPercentage.length(8), LengthPercentage.length(8));
        containerStyle.size = new TaffySize<>(TaffyDimension.length(width), TaffyDimension.length(height));
        container.setStyle(containerStyle);

        var label = new Label(text,  0xFFFFFFFF);
        var labelStyle = new TaffyStyle();
        labelStyle.size = new TaffySize<>(TaffyDimension.percent(0.9f), TaffyDimension.length(24));
        container.addChild(label, labelStyle);

        return container;
    }
}


