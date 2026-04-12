package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.ui.css.CascadingStyleSheets;
import com.xkball.xklib.ui.layout.FocusNode;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.ui.layout.TaffySizeParser;
import com.xkball.xklib.ui.system.GuiSystem;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.tree.Layout;
import dev.vfyjxf.taffy.tree.NodeId;
import dev.vfyjxf.taffy.tree.TaffyTree;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public interface IGuiWidget {
    void setX(float x);

    void setY(float y);

    float getX();

    float getY();
    
    void setWidth(float width);
    
    void setHeight(float height);

    float getWidth();

    float getHeight();
    
    void setEnabled(boolean enabled);
    
    boolean enabled();

    void setVisible(boolean visible);
    
    boolean visible();
    
    String getCSSType();
    
    IGuiWidget setCSSClassName(String name);
    
    String getCSSClassName();
    
    IGuiWidget setCSSId(String name);
    
    String getCSSId();
    
    /*
    仅供GuiSystem更新, 不应该使用
     */
    void setHovered(boolean hovered);
    
    boolean isHovered();
    
    FocusNode getFocusNode();
    
    /*
    仅供GuiSystem更新, 不应该使用
    */
    void setNodeId(NodeId nodeId);
    
    NodeId getNodeId();
    
    boolean isFocused();
    
    boolean isPrimaryFocused();
    
    void markDirty();
    
    boolean isDirty();
    
    /*
    只控制是否裁剪渲染, 不会影响布局是否溢出
     */
    void setOverflow(boolean overflow);
    
    boolean overflow();
    
    void setStyle(TaffyStyle style);
    
    TaffyStyle getStyle();
    
    
    /*
    仅供GuiSystem更新, 不应该使用
    */
    void setTree(TaffyTree tree);
    

    TaffyTree getTree();
    
    
    @Nullable
    IGuiWidget getParent();
    
    /**
     * 不可在初始化时调用
     * 为多线程情况准备, 调用成本较高, 如果确认在UI线程请使用GuiSystem.INSTANCE.get()
     */
    GuiSystem getGuiSystemAsync();

    IStyleSheet getStyleSheet();

    void setStyleSheet(IStyleSheet styleSheet);
    
    CascadingStyleSheets getStyleSheetAsRoot();
    
    CascadingStyleSheets getStyleSheetAsSelf();
    
    IGuiWidget inlineStyle(String style);
    
    IGuiWidget asRootStyle(String style);
    
    default List<? extends IGuiWidget> getChildren(){
        return List.of();
    }
    
    default void resize(float offsetX, float offsetY){
    
    }
    
    /**
     * 在此方法创建子组件,和创建样式, 应该在设置Tree和ID后调用
     */
    default void init(){
    
    }
    
    /**
     * runnable内可以进行对widget树的操作, 被操作的对象应该markDirty来重写计算布局
     */
    default void submitTreeUpdate(Runnable runnable){
        GuiSystem.INSTANCE.get().submitTreeUpdate(runnable);
    }
    
    void submitTreeUpdateAsync(Runnable runnable);
    
    /**
     *  仅用于提交一些其他任务, 不应在其中进行UI操作, 可以在运算完成后通过其他submit方法来通知UI并在下一帧进行更新
     */
    default void submitAsync(Runnable runnable){
        Thread.startVirtualThread(runnable);
    }
    
    default ScreenRectangle getRectangle() {
        return new ScreenRectangle((int) this.getX(), (int) this.getY(), (int) this.getWidth(), (int) this.getHeight());
    }
    
    default void onFocusChanged(boolean focused) {
    
    }

    default void onStyleSheetChanged() {

    }
    
    default void onRemove(){
        for(var child : this.getChildren()){
            child.onRemove();
        }
    }
    
    /**
     * 除了在绝对布局Container下或者resize里传递布局结果, 不要使用此方法改变组件位置和大小, 会被布局的结果覆盖
     */
    default void setPosition(float x, float y) {
        this.setX(x);
        this.setY(y);
    }
    
    /**
     * 除了在绝对布局Container下或者resize里传递布局结果, 不要使用此方法改变组件位置和大小, 会被布局的结果覆盖
     */
    default void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }
    
    default void setRectangle(ScreenRectangle rectangle) {
        this.setX(rectangle.left());
        this.setY(rectangle.top());
        this.setWidth(rectangle.width());
        this.setHeight(rectangle.height());
    }

    default void visitWidgets(final Consumer<IGuiWidget> widgetVisitor){
        widgetVisitor.accept(this);
    }
    
    default float getMaxX(){
        return this.getX() + this.getWidth();
    }
    
    default float getMaxY(){
        return this.getY() + this.getHeight();
    }
    
    default void asTreeRoot(){
        var tree = new TaffyTree();
        var id = tree.newLeaf(this.getStyle());
        this.setTree(tree);
        this.setNodeId(id);
    }
    
    default IGuiWidget setStyle(Consumer<TaffyStyle> styleUpdate){
        var style = this.getStyle();
        styleUpdate.accept(style);
        this.setStyle(style);
        return this;
    }
    
    default IGuiWidget applyStyle(UnaryOperator<TaffyStyle> styleUpdate){
        this.setStyle(styleUpdate.apply(this.getStyle()));
        return this;
    }
    
    default Layout getLayout(){
        return this.getTree().getLayout(this.getNodeId());
    }
    
    default IGuiWidget getRoot(){
        if(this.getParent() != null) return this.getParent().getRoot();
        else return this;
    }
    
    default IGuiWidget setSize(String width, String height){
        this.setStyle(s ->
                s.size = TaffySize.of(TaffySizeParser.of(width).toDimension(), TaffySizeParser.of(height).toDimension())
        );
        return this;
    }
    
}