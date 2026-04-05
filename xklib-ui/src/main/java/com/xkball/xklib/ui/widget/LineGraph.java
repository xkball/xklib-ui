package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.render.IGUIGraphics;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class LineGraph extends Widget {

    private static final String SELF_CSS = """
            linegraph-axis-color: 0xFF4A5568;
            linegraph-num-color: 0xFF718096;
            linegraph-line-color: 0xFF63B3ED;
            linegraph-bg-color: 0xFF1A202C;
            linegraph-grid-color: 0x22FFFFFF;
            """;

    public LineGraph() {
        this.inlineStyle(SELF_CSS);
    }

    public int axisColor = 0xFF4A5568;
    public int numColor = 0xFF718096;
    public int lineColor = 0xFF63B3ED;
    public int bgColor = 0xFF1A202C;
    public int gridColor = 0x22FFFFFF;
    public float numHeight = 10;

    public boolean renderAxis = true;
    public boolean renderTickNum = true;
    public boolean renderGrid = true;
    public AxisStrategy xAxisStrategy = AxisStrategy.LINEAR;
    public AxisStrategy yAxisStrategy = AxisStrategy.LINEAR;
    public YAxisMaxStrategy yAxisMaxStrategy = DefaultYAxisMaxStrategy.PERCENT110;
    public float lastYMax = 0;
    private float currentYMax = 0;
    private final FloatList xValues = new FloatArrayList();
    private final FloatList yValues = new FloatArrayList();

    private static final float AXIS_WIDTH = 1f;
    private static final float TICK_LEN = 4f;
    private static final float PADDING_LEFT = 40f;
    private static final float PADDING_BOTTOM = 18f;
    private static final float PADDING_TOP = 8f;
    private static final float PADDING_RIGHT = 8f;


    public void setData(FloatList xValues, FloatList yValues) {
        this.xValues.clear();
        this.xValues.addAll(xValues);
        this.yValues.clear();
        this.yValues.addAll(yValues);
        updateYMax();
    }

    public void setData(FloatList xValues) {
        this.xValues.clear();
        this.xValues.addAll(xValues);
        this.yValues.clear();
        for (int i = 0; i < xValues.size(); i++) {
            this.yValues.add(i);
        }
        updateYMax();
    }

    private void updateYMax() {
        float targetYMax = yAxisMaxStrategy.getAxisMax(yValues);
        if (currentYMax == 0) {
            currentYMax = targetYMax;
            lastYMax = targetYMax;
            return;
        }
        float diff = Math.abs(targetYMax - currentYMax);
        float threshold = currentYMax * 0.05f;
        if (diff <= threshold) {
            currentYMax = targetYMax;
        } else {
            currentYMax = currentYMax + (targetYMax - currentYMax) * 0.60f;
        }
        lastYMax = currentYMax;
    }

    private float graphX() { return this.x + PADDING_LEFT; }
    private float graphY() { return this.y + PADDING_TOP; }
    private float graphW() { return this.width - PADDING_LEFT - PADDING_RIGHT; }
    private float graphH() { return this.height - PADDING_TOP - PADDING_BOTTOM; }

    /**
     * 取 a * 10^b 作为刻度, 对于对数坐标轴, 对值取log10再进行类似计算
     * 当10^b对应x像素(10<x<=100), 则刻度按如下分类讨论:
     * 10<x<=25 取4x作为刻度
     * 25<x<=50 取2x作为刻度
     * 50<x<=100 取x作为刻度
     * 除非最大值离最高刻度不足25像素, 额外添加最大值作为刻度
     * @return (刻度值,渲染的x值)的列表
     */
    public List<Vector2f> xTickNums(float width) {
        if (xValues.isEmpty()) return List.of();
        float xMin = (float) xValues.doubleStream().min().orElse(0);
        float xMax = (float) xValues.doubleStream().max().orElse(1);
        return computeTicks(xMin, xMax, width, xAxisStrategy);
    }

    /**
     * 取 a * 10^b 作为刻度, 對於對數坐標軸, 對值取log10再進行類似計算
     * 當10^b對應x像素(10<x<=100), 則刻度按如下分類討論:
     * 10<x<=25 取4x作為刻度
     * 25<x<=50 取2x作為刻度
     * 50<x<=100 取x作為刻度
     * 除非最大值離最高刻度不足25像素, 額外添加最大值作為刻度
     * @return (刻度值,渲染的y值)的列表
     */
    public List<Vector2f> yTickNums(float height) {
        float yMax = currentYMax <= 0 ? 1f : currentYMax;
        return computeTicks(0, yMax, height, yAxisStrategy);
    }

    private List<Vector2f> computeTicks(float valMin, float valMax, float pixelSize, AxisStrategy strategy) {
        List<Vector2f> result = new ArrayList<>();
        if (valMax <= valMin || pixelSize <= 0) return result;

        float logMin = strategy == AxisStrategy.LOG10 ? (float) Math.log10(Math.max(valMin, 1e-10)) : valMin;
        float logMax = strategy == AxisStrategy.LOG10 ? (float) Math.log10(Math.max(valMax, 1e-10)) : valMax;
        float range = logMax - logMin;
        if (range <= 0) return result;

        float b = (float) Math.floor(Math.log10(range));
        float unit = (float) Math.pow(10, b);
        float pixelsPerUnit = pixelSize / range * unit;

        float step;
        if (pixelsPerUnit <= 25) {
            step = unit * 4;
        } else if (pixelsPerUnit <= 50) {
            step = unit * 2;
        } else {
            step = unit;
        }

        float firstTick = (float) (Math.ceil(logMin / step) * step);
        float lastTickVal = 0;
        for (float v = firstTick; v <= logMax + step * 0.001f; v += step) {
            if (v > logMax + step * 0.001f) break;
            float realVal = strategy == AxisStrategy.LOG10 ? (float) Math.pow(10, v) : v;
            float pixel = (v - logMin) / range * pixelSize;
            result.add(new Vector2f(realVal, pixel));
            lastTickVal = v;
        }

        float lastPixel = lastTickVal == 0 ? 0 : (lastTickVal - logMin) / range * pixelSize;
        if (pixelSize - lastPixel > 25) {
            float realMax = strategy == AxisStrategy.LOG10 ? (float) Math.pow(10, logMax) : logMax;
            result.add(new Vector2f(realMax, pixelSize));
        }

        return result;
    }

    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);

        float gx = graphX();
        float gy = graphY();
        float gw = graphW();
        float gh = graphH();

        if (gw <= 0 || gh <= 0) return;

        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, bgColor);

        if (renderAxis) {
            graphics.fill(gx, gy, gx + AXIS_WIDTH, gy + gh, axisColor);
            graphics.fill(gx, gy + gh - AXIS_WIDTH, gx + gw, gy + gh, axisColor);
        }

        if (renderTickNum) {
            List<Vector2f> xTicks = xTickNums(gw);
            List<Vector2f> yTicks = yTickNums(gh);

            for (Vector2f tick : xTicks) {
                float px = gx + tick.y;
                if (renderGrid) {
                    graphics.fill(px, gy, px + AXIS_WIDTH, gy + gh, gridColor);
                }
                if (renderAxis) {
                    graphics.fill(px, gy + gh - TICK_LEN, px + AXIS_WIDTH, gy + gh, axisColor);
                }
                String label = formatTickLabel(tick.x);
                float textW = graphics.defaultFont().width(label) * (numHeight / graphics.defaultFont().lineHeight());
                graphics.drawString(label, px - textW / 2, gy + gh + 2, numColor, numHeight);
            }

            for (Vector2f tick : yTicks) {
                float py = gy + gh - tick.y;
                if (renderGrid) {
                    graphics.fill(gx, py, gx + gw, py + AXIS_WIDTH, gridColor);
                }
                if (renderAxis) {
                    graphics.fill(gx, py, gx + TICK_LEN, py + AXIS_WIDTH, axisColor);
                }
                String label = formatTickLabel(tick.x);
                float textW = graphics.defaultFont().width(label) * (numHeight / graphics.defaultFont().lineHeight());
                graphics.drawString(label, gx - textW - 3, py - numHeight / 2, numColor, numHeight);
            }
        }

        if (xValues.size() < 2) return;

        float xMin = (float) xValues.doubleStream().min().orElse(0);
        float xMax = (float) xValues.doubleStream().max().orElse(1);
        float yMax = currentYMax <= 0 ? 1f : currentYMax;

        float prevPx = 0, prevPy = 0;
        for (int i = 0; i < xValues.size(); i++) {
            float xVal = xValues.getFloat(i);
            float yVal = i < yValues.size() ? yValues.getFloat(i) : 0;

            float normX = xAxisStrategy == AxisStrategy.LOG10
                    ? ((float) Math.log10(Math.max(xVal, 1e-10)) - (float) Math.log10(Math.max(xMin, 1e-10)))
                    / Math.max((float) Math.log10(Math.max(xMax, 1e-10)) - (float) Math.log10(Math.max(xMin, 1e-10)), 1e-10f)
                    : (xMax > xMin ? (xVal - xMin) / (xMax - xMin) : 0f);

            float normY = yAxisStrategy == AxisStrategy.LOG10
                    ? ((float) Math.log10(Math.max(yVal, 1e-10)) - 0)
                    / Math.max((float) Math.log10(Math.max(yMax, 1e-10)) - 0, 1e-10f)
                    : (yVal / yMax);

            float px = gx + normX * gw;
            float py = gy + gh - normY * gh;

            if (i > 0) {
                graphics.renderLine(prevPx, prevPy, px, py, lineColor);
            }
            prevPx = px;
            prevPy = py;
        }
    }

    private String formatTickLabel(float val) {
        if (Math.abs(val) >= 1000 || (Math.abs(val) < 0.01f && val != 0)) {
            return String.format("%.1e", val);
        }
        if (val == Math.floor(val)) {
            return String.valueOf((int) val);
        }
        return String.format("%.2f", val);
    }

    public enum AxisStrategy {
        LINEAR,
        LOG10
    }

    public interface YAxisMaxStrategy {
        float getAxisMax(FloatList yValues);
    }

    public record FixedYAxisMaxStrategy(float value) implements YAxisMaxStrategy {
        @Override
        public float getAxisMax(FloatList yValues) {
            return value;
        }
    }

    public enum DefaultYAxisMaxStrategy implements YAxisMaxStrategy {
        MAX {
            @Override
            public float getAxisMax(FloatList yValues) {
                return (float) yValues.doubleStream().max().orElse(1);
            }
        },
        PERCENT110 {
            @Override
            public float getAxisMax(FloatList yValues) {
                var max = (float) yValues.doubleStream().max().orElse(1);
                return max * 1.1f;
            }
        },
        PERCENT90 {
            @Override
            public float getAxisMax(FloatList yValues) {
                var max = (float) yValues.doubleStream().max().orElse(1);
                return max * 0.9f;
            }
        }
    }
}
