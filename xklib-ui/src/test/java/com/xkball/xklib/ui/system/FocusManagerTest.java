package com.xkball.xklib.ui.system;

import com.xkball.xklib.ui.layout.FocusNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FocusManagerTest {

    private FocusManager manager;
    private FocusNode root;
    private FocusNode mid;
    private FocusNode leaf;
    private FocusNode nonFocusable;

    @BeforeEach
    void setUp() {
        manager = new FocusManager();

        root = new FocusNode();
        mid = new FocusNode();
        leaf = new FocusNode();
        nonFocusable = new FocusNode();

        root.addChild(mid);
        mid.addChild(leaf);
        mid.addChild(nonFocusable);

        leaf.setCanTakePrimaryFocus(true);
        nonFocusable.setCanTakePrimaryFocus(false);

        manager.root = root;
    }

    @Test
    void testTakeFocusSetsPathFocused() {
        manager.takeFocus(leaf);

        assertTrue(leaf.isFocused());
        assertTrue(leaf.isPrimaryFocused());
        assertTrue(mid.isFocused());
        assertTrue(root.isFocused());
        assertEquals(leaf, manager.getPrimaryFocusedNode());
        assertEquals(leaf, manager.getCurrentFocusedNode());
    }

    @Test
    void testNonPrimaryFocusableNodeStillGetsFocusedWithPath() {
        manager.takeFocus(nonFocusable);

        assertTrue(nonFocusable.isFocused());
        assertFalse(nonFocusable.isPrimaryFocused());
        assertTrue(mid.isFocused());
        assertTrue(root.isFocused());

        assertNull(manager.getPrimaryFocusedNode());
        assertEquals(nonFocusable, manager.getCurrentFocusedNode());
    }

    @Test
    void testClearFocusRemovesFocusFromPath() {
        manager.takeFocus(leaf);
        manager.clearFocus();

        assertNull(manager.getPrimaryFocusedNode());
        assertNull(manager.getCurrentFocusedNode());
        assertFalse(leaf.isFocused());
        assertFalse(leaf.isPrimaryFocused());
        assertFalse(mid.isFocused());
        assertFalse(root.isFocused());
    }

    @Test
    void testSwitchFocusClearsPreviousPath() {
        FocusNode leaf2 = new FocusNode();
        leaf2.setCanTakePrimaryFocus(true);
        root.addChild(leaf2);

        manager.takeFocus(leaf);
        manager.takeFocus(leaf2);

        assertEquals(leaf2, manager.getPrimaryFocusedNode());
        assertEquals(leaf2, manager.getCurrentFocusedNode());
        assertFalse(leaf.isFocused());
        assertFalse(leaf.isPrimaryFocused());
        assertFalse(mid.isFocused());
        assertTrue(leaf2.isFocused());
        assertTrue(leaf2.isPrimaryFocused());
        assertTrue(root.isFocused());
    }

    @Test
    void testSwitchFromNonPrimaryToNonPrimaryHasNoPrimaryFocus() {
        manager.takeFocus(nonFocusable);
        manager.takeFocus(mid);

        assertFalse(nonFocusable.isFocused());
        assertFalse(nonFocusable.isPrimaryFocused());
        assertTrue(mid.isFocused());
        assertFalse(mid.isPrimaryFocused());
        assertNull(manager.getPrimaryFocusedNode());
    }

    @Test
    void testTakeFocusNullDoesNothing() {
        manager.takeFocus(null);

        assertNull(manager.getPrimaryFocusedNode());
        assertNull(manager.getCurrentFocusedNode());
    }

    @Test
    void testOnlyOnePrimaryFocusAtATime() {
        FocusNode other = new FocusNode();
        other.setCanTakePrimaryFocus(true);
        mid.addChild(other);

        manager.takeFocus(leaf);
        manager.takeFocus(other);

        assertFalse(leaf.isPrimaryFocused());
        assertTrue(other.isPrimaryFocused());
        assertEquals(other, manager.getPrimaryFocusedNode());
    }
}

