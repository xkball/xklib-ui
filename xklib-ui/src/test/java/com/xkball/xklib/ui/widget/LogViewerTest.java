package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogViewerTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LogViewerTest.class);
    
    public static void main(String[] ignored) {
        try (var frame = new WidgetTestFrame(LogViewerTest::createLogViewerWindow)) {
            startLogGenerator();
            frame.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static ContainerWidget createLogViewerWindow() {
        var root = new ContainerWidget();
        root.asTreeRoot();
        root.inlineStyle("background-color: 0xFF0F172A;");
        var rootStyle = new dev.vfyjxf.taffy.style.TaffyStyle();
        rootStyle.flexDirection = FlexDirection.COLUMN;
        rootStyle.alignItems = AlignItems.STRETCH;
        rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        root.setStyle(rootStyle);
        
        var title = new Label("日志查看器", TextAlign.CENTER, 0xFFFFFFFF);
        var titleStyle = new dev.vfyjxf.taffy.style.TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(40));
        titleStyle.flexShrink = 0;
        root.addChild(title, titleStyle);
        
        var logViewer = new LogViewer();
        var logViewerStyle = new dev.vfyjxf.taffy.style.TaffyStyle();
        logViewerStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        root.addChild(logViewer, logViewerStyle);
        
        return root;
    }
    
    private static void startLogGenerator() {
        Thread logThread = new Thread(() -> {
            int counter = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    
                    switch (counter % 5) {
                        case 0:
                            LOGGER.trace("这是一条 TRACE 日志消息 #{}", counter);
                            break;
                        case 1:
                            LOGGER.debug("这是一条 DEBUG 日志消息 #{}", counter);
                            break;
                        case 2:
                            LOGGER.info("这是一条 INFO 日志消息 #{}", counter);
                            break;
                        case 3:
                            LOGGER.warn("这是一条 WARN 日志消息 #{}", counter);
                            break;
                        case 4:
                            LOGGER.error("这是一条 ERROR 日志消息 #{}", counter);
                            break;
                    }
                    
                    counter++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        logThread.setDaemon(true);
        logThread.start();
    }
}


