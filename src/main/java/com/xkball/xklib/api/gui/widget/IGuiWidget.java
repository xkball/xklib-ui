package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.ui.navigation.ScreenRectangle;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IGuiWidget {
    void setX(int x);

    void setY(int y);

    int getX();

    int getY();

    int getWidth();

    int getHeight();
    
    void setEnabled(boolean enabled);
    
    boolean enabled();

    void setVisible(boolean visible);
    
    boolean visible();
    
    boolean isHovered();
    
    /**
     * runnable内可以进行对widget树的操作, 被操作的对象应该markDirty来重写计算布局
     */
    default void submitTreeUpdate(Runnable runnable){
    
    }
    
    default void submitLayoutUpdate(Supplier<Iterable<IGuiWidget>> runnable){
    
    }
    
    /**
     *  仅用于提交一些其他任务, 不应在其中进行UI操作, 可以在运算完成后通过其他submit方法来通知UI并在下一帧进行更新
     */
    default void submitAsync(Runnable runnable){
    
    }
    
    default ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    default void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    default void visitWidgets(final Consumer<IGuiWidget> widgetVisitor){
        widgetVisitor.accept(this);
    }
}