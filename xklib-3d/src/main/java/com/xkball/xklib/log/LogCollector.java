package com.xkball.xklib.log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class LogCollector {
    
    private static final LogCollector INSTANCE = new LogCollector();
    
    private final List<LogEntry> logEntries = new ArrayList<>();
    private final List<Consumer<LogEntry>> listeners = new CopyOnWriteArrayList<>();
    private int maxEntries = 1000;
    
    private LogCollector() {
    }
    
    public static LogCollector getInstance() {
        return INSTANCE;
    }
    
    public void addLogEntry(LogEntry entry) {
        synchronized (logEntries) {
            logEntries.add(entry);
            if (logEntries.size() > maxEntries) {
                logEntries.remove(0);
            }
        }
        for (Consumer<LogEntry> listener : listeners) {
            listener.accept(entry);
        }
    }
    
    public List<LogEntry> getLogEntries() {
        synchronized (logEntries) {
            return new ArrayList<>(logEntries);
        }
    }
    
    public void addListener(Consumer<LogEntry> listener) {
        listeners.add(listener);
    }
    
    public void removeListener(Consumer<LogEntry> listener) {
        listeners.remove(listener);
    }
    
    public void clear() {
        synchronized (logEntries) {
            logEntries.clear();
        }
    }
    
    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }
    
    public static class LogEntry {
        private final long timestamp;
        private final String level;
        private final String logger;
        private final String message;
        private final String threadName;
        
        public LogEntry(long timestamp, String level, String logger, String message, String threadName) {
            this.timestamp = timestamp;
            this.level = level;
            this.logger = logger;
            this.message = message;
            this.threadName = threadName;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public String getLevel() {
            return level;
        }
        
        public String getLogger() {
            return logger;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getThreadName() {
            return threadName;
        }
        
        public String getFormattedMessage() {
            return String.format("[%s][%s][%s] %s", level, threadName, logger, message);
        }
    }
}

