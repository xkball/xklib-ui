package com.xkball.xklib.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdjacencyList<T,D>{
    
    private final Map<T, List<T>> childMap = new HashMap<>();
    private final Map<T, T> parentMap = new HashMap<>();
    private final Map<T, D> dataMap = new HashMap<>();
    private final Set<T> roots = new HashSet<>();
    
    public void addNode(T node) {
        childMap.putIfAbsent(node, new ArrayList<>());
        parentMap.put(node, null);
        dataMap.put(node, null);
        roots.add(node);
    }
    
    public void addNode(T child, T parent){
        if(childMap.containsKey(child)){
            removeNode(child);
        }
        if(parent == null || !childMap.containsKey(parent)) {
            this.addNode(child);
        }
        else {
            childMap.computeIfPresent(parent,(k,v) -> {
                v.add(child);
                return v;
            });
            parentMap.put(child, parent);
            dataMap.put(child, null);
        }
    }
    
    public void removeNode(T node) {
        var parent = parentMap.remove(node);
        if(parent == null) return;
        childMap.get(parent).remove(node);
        dataMap.remove(node);
        roots.remove(node);
        for(var child : childMap.get(node)) {
            this.removeNode(child);
        }
    }
    
    public void setData(T node, D data) {
        dataMap.put(node, data);
    }
    
    public void removeData(T node) {
        dataMap.remove(node);
    }
    
    public D getData(T node) {
        return dataMap.get(node);
    }
    
    public Iterable<T> getChild(T node) {
        if(node == null) return roots;
        return childMap.get(node);
    }
    
    public boolean hasNode(T node) {
        return childMap.containsKey(node);
    }
    
    public Set<T> getNodes() {
        return childMap.keySet();
    }
    
    public int nodeCount() {
        return childMap.size();
    }
    
    public void clear() {
        childMap.clear();
        parentMap.clear();
        dataMap.clear();
    }
    
    //浅复制
    public AdjacencyList<T,D> copy(){
        var newAdjacencyList = new AdjacencyList<T,D>();
        newAdjacencyList.childMap.putAll(this.childMap);
        newAdjacencyList.parentMap.putAll(this.parentMap);
        newAdjacencyList.dataMap.putAll(this.dataMap);
        return newAdjacencyList;
    }
}
