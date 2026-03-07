package com.xkball.xklib.ui.layout;

import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.Set;
import java.util.HashSet;
public class FocusNode {
    
    public FocusNode parent;
    public IGuiWidget widget;
    public Set<FocusNode> children = new HashSet<>();
    public boolean focused = false;
    public boolean canTakePrimaryFocus = false;
    public boolean primaryFocused = false;
    
    public FocusNode() {
        this(null);
    }
    
    public FocusNode(FocusNode parent) {
        this.parent = parent;
    }
    
    public boolean isFocused() {
        return this.focused;
    }
    
    public boolean isPrimaryFocused() {
        return this.primaryFocused;
    }
    
    public boolean canTakePrimaryFocus() {
        return this.canTakePrimaryFocus;
    }
    
    public void setParent(FocusNode parent) {
        this.parent = parent;
    }
    
    public void setCanTakePrimaryFocus(boolean canTakePrimaryFocus) {
        this.canTakePrimaryFocus = canTakePrimaryFocus;
    }
    
    public void setFocused(boolean focused) {
        var changed = this.focused != focused;
        this.focused = focused;
        if(changed && this.widget != null) widget.onFocusChanged(focused);
    }
    
    public void setPrimaryFocused(boolean primaryFocused) {
        this.primaryFocused = primaryFocused;
    }
    
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
    
    public void addChild(FocusNode child) {
        child.setParent(this);
        child.setFocused(false);
        child.setPrimaryFocused(false);
        this.children.add(child);
    }
    
    public void removeChild(FocusNode child) {
        if(this.children.contains(child)) {
            child.setParent(null);
            child.setFocused(false);
            child.setPrimaryFocused(false);
            this.children.remove(child);
        }
    }
}
