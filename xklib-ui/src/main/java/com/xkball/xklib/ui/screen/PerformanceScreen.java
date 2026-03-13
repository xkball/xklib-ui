package com.xkball.xklib.ui.screen;

import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.utils.AdjacencyList;

import java.util.ArrayDeque;
import java.util.Deque;

public class PerformanceScreen extends ContainerWidget {
    
    private final IntLayoutVariable size = new IntLayoutVariable(1000);
    private AdjacencyList<String,Deque<Long>> data = new AdjacencyList<>();
    
    public void updateData(AdjacencyList<String,Long> data){
        var newTree = new AdjacencyList<String,Deque<Long>>();
        for(var node : data.getChild(null)){
            syncTree(newTree, this.data, node, null, data);
        }
        this.data = newTree;
        this.buildPage();
    }
    
    public void syncTree(AdjacencyList<String,Deque<Long>> newTree, AdjacencyList<String,Deque<Long>> oldTree, String node, String parent, AdjacencyList<String,Long> rawData){
        newTree.addNode(node, parent);
        var data = oldTree.getData(node);
        if(data == null) data = new ArrayDeque<>();
        data.push(rawData.getData(node));
        if(data.size() > size.get()){
            data.removeFirst();
        }
        newTree.setData(node, data);
        for(var next : rawData.getChild(node)){
            syncTree(newTree, oldTree, next, node, rawData);
        }
    }
    
    public void buildPage(){
        this.submitTreeUpdate(() -> {
        
        });
    }
}
