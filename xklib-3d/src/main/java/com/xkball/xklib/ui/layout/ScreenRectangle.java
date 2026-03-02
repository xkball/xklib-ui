package com.xkball.xklib.ui.layout;

import com.xkball.xklib.utils.MathUtils;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

public record ScreenRectangle(ScreenPosition pos, int width, int height) {
    
    private static final ScreenRectangle EMPTY = new ScreenRectangle(0, 0, 0, 0);
    
    public ScreenRectangle(int x, int y, int width, int height) {
        this(new ScreenPosition(x, y), width, height);
    }
    
    public static ScreenRectangle empty() {
        return EMPTY;
    }
    
    public @Nullable ScreenRectangle intersection(ScreenRectangle other) {
        int left = Math.max(this.left(), other.left());
        int top = Math.max(this.top(), other.top());
        int right = Math.min(this.right(), other.right());
        int bottom = Math.min(this.bottom(), other.bottom());
        return left < right && top < bottom ? new ScreenRectangle(left, top, right - left, bottom - top) : null;
    }
    
    public ScreenRectangle offset(int dx, int dy) {
        return new ScreenRectangle(this.left() + dx, this.top() + dy, this.width, this.height);
    }
    
    public boolean intersects(ScreenRectangle other) {
        return this.left() < other.right() && this.right() > other.left() && this.top() < other.bottom() && this.bottom() > other.top();
    }
    
    public boolean encompasses(ScreenRectangle other) {
        return other.left() >= this.left() && other.top() >= this.top() && other.right() <= this.right() && other.bottom() <= this.bottom();
    }
    
    public int top() {
        return this.pos.y();
    }
    
    public int bottom() {
        return this.pos.y() + this.height;
    }
    
    public int left() {
        return this.pos.x();
    }
    
    public int right() {
        return this.pos.x() + this.width;
    }
    
    public boolean containsPoint(int x, int y) {
        return x >= this.left() && x < this.right() && y >= this.top() && y < this.bottom();
    }
    
    public ScreenRectangle transformAxisAligned(Matrix3x2fc matrix) {
        Vector2f topLeft = matrix.transformPosition(this.left(), this.top(), new Vector2f());
        Vector2f bottomRight = matrix.transformPosition(this.right(), this.bottom(), new Vector2f());
        return new ScreenRectangle(MathUtils.floor(topLeft.x), MathUtils.floor(topLeft.y), MathUtils.floor(bottomRight.x - topLeft.x), MathUtils.floor(bottomRight.y - topLeft.y));
    }
    
    public ScreenRectangle transformMaxBounds(Matrix3x2fc matrix) {
        Vector2f topLeft = matrix.transformPosition(this.left(), this.top(), new Vector2f());
        Vector2f topRight = matrix.transformPosition(this.right(), this.top(), new Vector2f());
        Vector2f bottomLeft = matrix.transformPosition(this.left(), this.bottom(), new Vector2f());
        Vector2f bottomRight = matrix.transformPosition(this.right(), this.bottom(), new Vector2f());
        float minX = Math.min(Math.min(topLeft.x(), bottomLeft.x()), Math.min(topRight.x(), bottomRight.x()));
        float maxX = Math.max(Math.max(topLeft.x(), bottomLeft.x()), Math.max(topRight.x(), bottomRight.x()));
        float minY = Math.min(Math.min(topLeft.y(), bottomLeft.y()), Math.min(topRight.y(), bottomRight.y()));
        float maxY = Math.max(Math.max(topLeft.y(), bottomLeft.y()), Math.max(topRight.y(), bottomRight.y()));
        return new ScreenRectangle(MathUtils.floor(minX), MathUtils.floor(minY), MathUtils.ceil(maxX - minX), MathUtils.ceil(maxY - minY));
    }
}
