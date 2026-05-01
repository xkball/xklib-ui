package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IInputWidget;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.widget.container.ContainerWidget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@GuiWidgetClass
public class ListInputWidget<V, T extends Widget & IInputWidget<V>> extends ContainerWidget implements IInputWidget<List<V>> {

    private final Supplier<T> inputFactory;
    private final BiFunction<IComponent, Runnable, Widget> buttonFactory;
    private final List<ILayoutVariable<List<V>>> bindings = new ArrayList<>();
    private final Map<Integer, ListRowWidget> rowWidgetsById = new HashMap<>();
    private final List<Integer> rowOrder = new ArrayList<>();
    private final ContainerWidget rowsContainer = new ContainerWidget().setCSSClassName("list_input_rows");
    private int nextId = 0;
    private Integer previewInsertIndex;
    private Integer draggingRowId;
    private boolean syncingBinding;

    public ListInputWidget(Supplier<T> inputFactory, BiFunction<IComponent, Runnable, Widget> buttonFactory) {
        this.inputFactory = inputFactory;
        this.buttonFactory = buttonFactory;
        Widget addButton = this.buttonFactory.apply(IComponent.literal("+"), this::addNextInput);
        addButton.setCSSClassName("list_input_btn");
        this.addChild(addButton.inlineStyle("margin-left: 95%-24rpx;"));
        this.addChild(this.rowsContainer);
        this.inlineStyle("flex-direction: column;").asRootStyle("""
                .list_input_rows {
                    flex-direction: column;
                    align-items: center;
                    justify-content: start;
                    overflow-y: scroll;
                    scrollbar-width: 8;
                    flex-grow: 1;
                    margin-top: 2rpx;
                }
                .list_input_btn {
                    size: 20rpx 20rpx;
                    flex-shrink: 0;
                }
                .list_input_row {
                    flex-direction: row;
                    align-items: stretch;
                    justify-content: space-between;
                    size: 90% 20rpx;
                    flex-shrink: 0;
                    margin-top: 1rpx;
                    margin-bottom: 1rpx;
                }
                .list_input_preview_row {
                    size: 100% 20rpx;
                    border: 1px;
                    border-color: 0x99FFFFFF;
                    background-color: 0x2200AAFF;
                    flex-shrink: 0;
                }
                .list_input_row_input {
                    size: 100%-24rpx 100%;
                    flex-shrink: 0;
                }
                .list_input_dragging_row {
                    size: 100% 20rpx;
                    background-color: 0x5500AAFF;
                }
                """);
    }

    @Override
    public List<V> getValue() {
        List<V> values = new ArrayList<>();
        for (Integer id : this.rowOrder) {
            ListRowWidget row = this.rowWidgetsById.get(id);
            if (row == null) {
                continue;
            }
            values.add(row.input.getValue());
        }
        return values;
    }
    
    public List<T> getInputWidgets(){
        List<T> widgets = new ArrayList<>();
        for (Integer id : this.rowOrder) {
            ListRowWidget row = this.rowWidgetsById.get(id);
            if (row == null) {
                continue;
            }
            widgets.add(row.input);
        }
        return widgets;
    }

    @Override
    public void setValue(List<V> value) {
        this.rowWidgetsById.clear();
        this.rowOrder.clear();
        if (value != null) {
            for (V v : value) {
                int rowId = this.nextId++;
                ListRowWidget row = new ListRowWidget(rowId, v);
                this.rowWidgetsById.put(rowId, row);
                this.rowOrder.add(rowId);
            }
        }
        this.previewInsertIndex = null;
        this.draggingRowId = null;
        this.refreshRows();
        this.pushBindings();
    }

    @Override
    public ListInputWidget<V, T> bind(ILayoutVariable<List<V>> variable) {
        this.setValue(variable.get());
        this.bindings.add(variable);
        variable.addCallback(v -> {
            if (this.syncingBinding) {
                return;
            }
            this.setValue(v);
        });
        return this;
    }

    public void addNextInput() {
        int rowId = this.nextId++;
        ListRowWidget row = new ListRowWidget(rowId, this.createDefaultValue());
        this.rowWidgetsById.put(rowId, row);
        this.rowOrder.add(rowId);
        this.refreshRows();
        this.pushBindings();
    }

    @Override
    public boolean widgetDraggingHovered(IMouseButtonEvent mousePos, IGuiWidget widget) {
        if (!(widget instanceof DraggingRowWidget dragging) || dragging.owner != this) {
            return false;
        }
        
        if (!this.rowsContainer.isMouseOver(mousePos.x(), mousePos.y())) {
            if (this.previewInsertIndex != null) {
                this.previewInsertIndex = null;
                this.refreshRows();
            }
            return false;
        }
        int newInsertIndex = this.computeInsertIndex((float) mousePos.y(), dragging.rowId);
        if (this.previewInsertIndex == null || this.previewInsertIndex != newInsertIndex) {
            this.previewInsertIndex = newInsertIndex;
            this.refreshRows();
        }
        return true;
    }

    @Override
    public boolean widgetDropped(IMouseButtonEvent mousePos, IGuiWidget widget) {
        if (!(widget instanceof DraggingRowWidget dragging) || dragging.owner != this) {
            return false;
        }
        if (!this.rowOrder.remove(Integer.valueOf(dragging.rowId))) {
            this.previewInsertIndex = null;
            this.draggingRowId = null;
            GuiSystem.INSTANCE.get().removeDraggingWidget();
            this.refreshRows();
            return true;
        }
        int insertIndex = this.previewInsertIndex == null
                ? this.computeInsertIndex((float) mousePos.y(), dragging.rowId)
                : this.previewInsertIndex;
        insertIndex = Math.clamp(insertIndex, 0, this.rowOrder.size());
        this.rowOrder.add(insertIndex, dragging.rowId);
        this.previewInsertIndex = null;
        this.draggingRowId = null;
        GuiSystem.INSTANCE.get().removeDraggingWidget();
        this.refreshRows();
        this.pushBindings();
        return true;
    }

    private V createDefaultValue() {
        T input = this.inputFactory.get();
        return input.getValue();
    }

    private void pushBindings() {
        this.syncingBinding = true;
        List<V> values = this.getValue();
        for (ILayoutVariable<List<V>> binding : this.bindings) {
            binding.set(new ArrayList<>(values));
        }
        this.syncingBinding = false;
    }

    private void refreshRows() {
        this.rowsContainer.clearChildren();
        Integer dragging = this.draggingRowId;
        List<Integer> renderingIds = this.rowOrder.stream().filter(id -> !id.equals(dragging)).toList();
        for (int i = 0; i < renderingIds.size(); i++) {
            if (this.previewInsertIndex != null && this.previewInsertIndex == i) {
                this.rowsContainer.addChild(new PreviewRowWidget());
            }
            Integer id = renderingIds.get(i);
            ListRowWidget row = this.rowWidgetsById.get(id);
            if (row == null) {
                continue;
            }
            this.rowsContainer.addChild(row);
        }
        if (this.previewInsertIndex != null && this.previewInsertIndex == renderingIds.size()) {
            this.rowsContainer.addChild(new PreviewRowWidget());
        }
        this.markDirty();
    }

    private int computeInsertIndex(float mouseY, int draggingId) {
        List<ListRowWidget> rows = this.rowWidgetsById.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(draggingId))
                .map(Map.Entry::getValue)
                .sorted(Comparator.comparingDouble(Widget::getY))
                .toList();
        int index = 0;
        for (ListRowWidget row : rows) {
            float middle = row.getY() + row.getHeight() * 0.5f;
            if (mouseY < middle) {
                return index;
            }
            index++;
        }
        return rows.size();
    }

    private void removeRow(int rowId) {
        ListRowWidget removed = this.rowWidgetsById.remove(rowId);
        if (removed == null) {
            return;
        }
        this.rowOrder.remove(Integer.valueOf(rowId));
        this.previewInsertIndex = null;
        this.draggingRowId = null;
        this.refreshRows();
        this.pushBindings();
    }

    private class ListRowWidget extends ContainerWidget {

        private final int rowId;
        private final T input;
        
        private ListRowWidget(int rowId, V value) {
            this.rowId = rowId;
            this.input = ListInputWidget.this.inputFactory.get();
            if (value != null) {
                this.input.setValue(value);
            }
            Widget removeButton = ListInputWidget.this.buttonFactory.apply(IComponent.literal("-"), () -> ListInputWidget.this.removeRow(this.rowId));
            this.setCSSClassName("list_input_row");
            this.input.setCSSClassName("list_input_row_input");
            removeButton.setCSSClassName("list_input_btn");
            this.addChild(this.input);
            this.addChild(removeButton);
        }

        @Override
        public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
            boolean consumed = super.mouseDragged(event, dx, dy);
            if (consumed) {
                return true;
            }
            if (!this.isMouseOver(event.x(), event.y())) {
                return false;
            }
            for(Widget child : this.children){
                if(child.isMouseOver(event.x(), event.y())){
                    return false;
                }
            }
            GuiSystem guiSystem = GuiSystem.INSTANCE.get();
            if (guiSystem.haveDraggingWidget()) {
                return false;
            }
            ListInputWidget.this.draggingRowId = this.rowId;
            ListInputWidget.this.previewInsertIndex = ListInputWidget.this.computeInsertIndex((float) event.y(), this.rowId);
            ListInputWidget.this.refreshRows();
            guiSystem.setDraggingWidget(new DraggingRowWidget(ListInputWidget.this, this.rowId ));
            return true;
        }
    }

    private static class PreviewRowWidget extends ContainerWidget {

        private PreviewRowWidget() {
            this.setCSSClassName("list_input_preview_row");
        }
    }

    private static class DraggingRowWidget extends ContainerWidget {

        private final ListInputWidget<?, ?> owner;
        private final int rowId;

        private DraggingRowWidget(ListInputWidget<?, ?> owner, int rowId) {
            this.owner = owner;
            this.rowId = rowId;
            this.setCSSClassName("list_input_dragging_row");
        }
    }
}
