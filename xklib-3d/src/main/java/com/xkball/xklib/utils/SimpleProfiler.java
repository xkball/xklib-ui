package com.xkball.xklib.utils;

import com.xkball.xklib.api.IProfiler;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.ArrayDeque;
import java.util.Deque;

public class SimpleProfiler implements IProfiler {
    
    private final AdjacencyList<String,Long> tree = new AdjacencyList<>();
    private final Deque<String> labelStack = new ArrayDeque<>();
    private final LongList timeStartStack = new LongArrayList();
    private AdjacencyList<String,Long> lastTick = new AdjacencyList<>();
    
    public void push(String label){
        this.timeStartStack.add(System.nanoTime());
        this.tree.addNode(label,labelStack.isEmpty() ? null : labelStack.peek());
        this.labelStack.push(label);
    }
    
    public void pop(){
        var name = labelStack.pop();
        var time = timeStartStack.removeLast();
        tree.setData(name, System.nanoTime() - time);
    }
    
    public long getTime(String label){
        Long time = lastTick.getData(label);
        return time == null ? 0 : time;
    }
    
    @Override
    public void endTick() {
        this.lastTick = tree.copy();
        this.labelStack.clear();
        this.timeStartStack.clear();
        this.tree.clear();
    }
    
    public AdjacencyList<String,Long> getData(){
        return this.lastTick;
    }
}
