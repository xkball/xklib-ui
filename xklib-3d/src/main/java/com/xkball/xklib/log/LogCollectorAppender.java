package com.xkball.xklib.log;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "LogCollectorAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class LogCollectorAppender extends AbstractAppender {
    
    protected LogCollectorAppender(String name, Filter filter) {
        super(name, filter, null, true, Property.EMPTY_ARRAY);
    }
    
    @Override
    public void append(LogEvent event) {
        String level = event.getLevel().name();
        String logger = event.getLoggerName();
        String message = event.getMessage().getFormattedMessage();
        String threadName = event.getThreadName();
        long timestamp = event.getTimeMillis();
        
        LogCollector.LogEntry entry = new LogCollector.LogEntry(
            timestamp,
            level,
            logger,
            message,
            threadName
        );
        
        LogCollector.getInstance().addLogEntry(entry);
    }
    
    @PluginFactory
    public static LogCollectorAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for LogCollectorAppender");
            return null;
        }
        return new LogCollectorAppender(name, filter);
    }
}

