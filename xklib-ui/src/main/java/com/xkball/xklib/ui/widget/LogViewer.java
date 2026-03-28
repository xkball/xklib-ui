package com.xkball.xklib.ui.widget;

import com.xkball.xklib.log.LogCollector;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TextAlign;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class LogViewer extends ContainerWidget {

    private static final String SELF_CSS = """
            * {
                display: flex;
                flex-direction: column;
                align-items: stretch;
                size: 100% 100%;
            }
            """;
    
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private final ContainerWidget logContainer;
    private final Consumer<LogCollector.LogEntry> logListener;
    private int maxDisplayLines = 500;
    private boolean autoScroll = true;
    
    public LogViewer() {
        this.logContainer = new ContainerWidget();
        this.logListener = this::addLogEntry;
    }

    @Override
    public String createCSSAsSelf() {
        return super.createCSSAsSelf() + SELF_CSS;
    }
    
    @Override
    public void init() {
        super.init();
        this.addDecoration(new Background(0xFF0D1117));
        
        logContainer.setStyle(s -> {
            s.flexDirection = FlexDirection.COLUMN;
            s.justifyContent = AlignContent.START;
            s.alignItems = AlignItems.STRETCH;
            s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        });
        logContainer.setYScrollEnable(true);
        this.addChild(logContainer);
        
        LogCollector.getInstance().addListener(logListener);
        
        for (LogCollector.LogEntry entry : LogCollector.getInstance().getLogEntries()) {
            addLogEntry(entry);
        }
    }
    
    private void addLogEntry(LogCollector.LogEntry entry) {
        this.submitTreeUpdate(() -> {
            if (logContainer.getChildren().size() >= maxDisplayLines) {
                var firstChild = logContainer.getChildren().get(0);
                logContainer.removeChild(firstChild);
            }
            
            String time = timeFormat.format(new Date(entry.getTimestamp()));
            String formattedLog = String.format("[%s][%s][%s] %s",
                time,
                entry.getLevel(),
                entry.getLogger(),
                entry.getMessage());
            
            int color = getColorForLevel(entry.getLevel());
            int bgColor = logContainer.getChildren().size() % 2 == 0 ? 0xFF1C2128 : 0xFF161B22;
            
            var label = new Label(formattedLog, TextAlign.LEFT, color);
            label.addDecoration(new Background(bgColor));
            label.setStyle(s -> {
                s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(20));
                s.flexShrink = 0;
            });
            
            logContainer.addChild(label);
            
            if (autoScroll) {
                logContainer.autoScrollToBottom();
            }
        });
    }
    
    private int getColorForLevel(String level) {
        return switch (level) {
            case "ERROR", "FATAL" -> 0xFFFF4444;
            case "WARN" -> 0xFFFFAA00;
            case "INFO" -> 0xFF4CAF50;
            case "DEBUG" -> 0xFF2196F3;
            case "TRACE" -> 0xFFAAAAAA;
            default -> 0xFFFFFFFF;
        };
    }
    
    public void setMaxDisplayLines(int maxDisplayLines) {
        this.maxDisplayLines = maxDisplayLines;
    }
    
    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }
    
    public void clearLogs() {
        this.submitTreeUpdate(() -> {
            logContainer.clearChildren();
        });
    }
}



