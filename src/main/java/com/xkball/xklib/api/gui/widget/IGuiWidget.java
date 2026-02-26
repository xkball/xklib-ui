package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.layout.ScreenRectangle;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IGuiWidget {
    void setX(int x);

    void setY(int y);

    int getX();

    int getY();
    
    void setWidth(int width);
    
    void setHeight(int height);

    int getWidth();

    int getHeight();
    
    void setEnabled(boolean enabled);
    
    boolean enabled();

    void setVisible(boolean visible);
    
    boolean visible();
    
    boolean isHovered();
    
    default void markDirty(){
        this.markDirty(true);
    }
    
    void markDirty(boolean dirty);
    
    boolean isDirty();
    
    void setOverflow(boolean overflow);
    
    boolean overflow();
    
    int expectWidth();
    
    int expectHeight();
    
    void addDecoration(IDecoration deco);
    
    /**
     * 在此方法创建子组件
     */
    default void init(){
    
    }
    
    
    /**
     * 应该计算子组件的大小, 不用调用子组件resize
     * 自己或者子组件的resize可能导致自己继续被markDirty, 但是总应该在一定帧数后达到稳定, 不应该无限markDirty
     * 需要格外注意状态变化, 防止无限更新, 由于更新是按帧进行的, 无限更新不会造成递归或者卡死, 但是会严重影响性能
     */
    default void resize(){
    
    }
    
    /**
     * runnable内可以进行对widget树的操作, 被操作的对象应该markDirty来重写计算布局
     */
    default void submitTreeUpdate(Runnable runnable){
        XKLib.gui.submitTreeUpdate(runnable);
    }
    
    default void submitLayoutUpdate(Supplier<Iterable<IGuiWidget>> layoutUpdate){
        layoutUpdate.get().forEach(IGuiWidget::markDirty);
    }
    
    /**
     *  仅用于提交一些其他任务, 不应在其中进行UI操作, 可以在运算完成后通过其他submit方法来通知UI并在下一帧进行更新
     */
    default void submitAsync(Runnable runnable){
        Thread.startVirtualThread(runnable);
    }
    
    default ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    default void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }
    
    default void setSize(int width, int height) {
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
}