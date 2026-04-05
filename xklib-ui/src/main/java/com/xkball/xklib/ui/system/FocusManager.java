package com.xkball.xklib.ui.system;

import com.xkball.xklib.ui.layout.FocusNode;

import java.util.ArrayList;
import java.util.List;

public class FocusManager {

    public FocusNode root;
    private FocusNode currentFocusedNode = null;
    private FocusNode primaryFocusedNode = null;

    public void takeFocus(FocusNode node) {
        if (node == null) return;

        if (currentFocusedNode != null && node != currentFocusedNode) {
            clearPath(currentFocusedNode);
        }

        currentFocusedNode = node;
        applyPath(node);

        if (node.canTakePrimaryFocus()) {
            primaryFocusedNode = node;
            node.setFocused(true,true);
        } else {
            primaryFocusedNode = null;
        }
    }

    public void clearFocus() {
        if (currentFocusedNode != null) {
            clearPath(currentFocusedNode);
            currentFocusedNode = null;
            primaryFocusedNode = null;
        }
    }

    public FocusNode getPrimaryFocusedNode() {
        return primaryFocusedNode;
    }

    public FocusNode getCurrentFocusedNode() {
        return currentFocusedNode;
    }

    private void applyPath(FocusNode node) {
        for (FocusNode n : buildPath(node)) {
            n.setFocused(true,false);
        }
    }

    private void clearPath(FocusNode node) {
        for (FocusNode n : buildPath(node)) {
            n.setFocused(false,false);
        }
    }

    private List<FocusNode> buildPath(FocusNode node) {
        List<FocusNode> path = new ArrayList<>();
        FocusNode current = node;
        while (current != null && current != root) {
            path.add(current);
            current = current.parent;
        }
        if (root != null) {
            path.add(root);
        }
        return path;
    }
}
