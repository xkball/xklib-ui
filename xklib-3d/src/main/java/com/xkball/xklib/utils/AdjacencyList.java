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
            childMap.computeIfPresent(parent,(ignored,v) -> {
                v.add(child);
                return v;
            });
            childMap.put(child, new ArrayList<>());
            parentMap.put(child, parent);
            dataMap.put(child, null);
            roots.remove(child);
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
        this.childMap.forEach((k, v) -> newAdjacencyList.childMap.put(k, new ArrayList<>(v)));
        newAdjacencyList.parentMap.putAll(this.parentMap);
        newAdjacencyList.dataMap.putAll(this.dataMap);
        newAdjacencyList.roots.addAll(this.roots);
        return newAdjacencyList;
    }
}
