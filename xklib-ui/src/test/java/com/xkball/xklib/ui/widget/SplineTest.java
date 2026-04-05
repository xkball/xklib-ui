package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.DraggableContainer;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklib.ui.widget.container.ScalableContainer;
import com.xkball.xklib.utils.math.CubicSpline2D;
import dev.vfyjxf.taffy.style.TextAlign;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SplineTest {

    private static final int INTERPOLATION_COUNT = 100;
    private static final int CONTROL_POINT_SIZE = 24;
    private static final int[] CONTROL_POINT_COLORS = {
            0xFFE74C3C,
            0xFF3498DB,
            0xFF2ECC71,
            0xFFF39C12,
            0xFF9B59B6,
            0xFF1ABC9C,
            0xFFE91E63,
            0xFF00BCD4,
            0xFFFFEB3B,
            0xFF795548
    };
    private static final String ROOT_CSS = """
            .spline-root {
                size: 100% 100%;
            }
            .spline-left {
                flex-direction: column;
                justify-content: start;
                align-items: stretch;
                size: 100% 100%;
            }
            .spline-title {
                size: 100% 35;
                text-align: center;
            }
            .spline-hint {
                size: 100% 50;
                text-align: left;
            }
            .spline-combo {
                size: 100%-16 32;
                flex-shrink: 0;
                margin: 8;
            }
            .spline-drag {
                size: 100%-8 24;
                flex-shrink: 0;
                margin: 8;
            }
            .spline-value {
                size: 100% 24;
                flex-shrink: 0;
                text-align: left;
            }
            .spline-right {
                size: 100% 100%;
                scalable-grid-enabled: true;
            }
            .spline-renderer {
                size: 800 600;
            }
            .spline-point-container {
                flex-direction: column;
                justify-content: center;
                align-items: center;
                size: 24 24;
            }
            .spline-point-label {
                size: 100% 100%;
            }
            """;

    private static final String[] SPLINE_TYPES = {"BEZIER", "HERMITE", "CATMULL_ROM", "B_SPLINE"};
    private static final CubicSpline2D[] SPLINE_IMPLS = {
            CubicSpline2D.BEZIER,
            CubicSpline2D.HERMITE,
            CubicSpline2D.CATMULL_ROM,
            CubicSpline2D.B
    };

    public static void main(String[] ignored) {
        try (var frame = new WidgetTestFrame(SplineTest::createSplineWindow)) {
            frame.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ContainerWidget createSplineWindow() {
        var root = new SplitContainer(false) {
            @Override
            public String createCSSAsRoot() {
                return ROOT_CSS;
            }
        };
        root.asTreeRoot();
        root.setCSSClassName("spline-root");

        var state = new SplineState();

        var leftPanel = createLeftPanel(state);
        root.setPanel(0, leftPanel);

        var rightPanel = createRightPanel(state);
        root.setPanel(1, rightPanel);

        return root;
    }

    private static class SplineState {
        CubicSpline2D currentSpline = CubicSpline2D.BEZIER;
        int controlPointCount = 4;
        final List<DraggableControlPoint> controlPoints = new ArrayList<>();
        SplineRendererWidget renderer;
        Label pointCountLabel;
    }

    private static ContainerWidget createLeftPanel(SplineState state) {
        var panel = new ContainerWidget();
        panel.setCSSClassName("spline-left");
        panel.inlineStyle("background-color: 0xFF1E293B;");
        var title = new Label("CubicSpline2D 样条曲线测试", TextAlign.CENTER, 0xFFFFFFFF);
        title.setCSSClassName("spline-title");
        panel.addChild(title);

        var typeTitle = new Label("样条曲线类型", TextAlign.LEFT, 0xFFE2E8F0);
        typeTitle.setCSSClassName("spline-subtitle");
        panel.addChild(typeTitle);

        var comboBox = new ComboBox<>(List.of(SPLINE_TYPES), t -> t, false);
        comboBox.setCSSClassName("spline-combo");
        comboBox.setOnChange(selected -> {
            int idx = -1;
            for (int i = 0; i < SPLINE_TYPES.length; i++) {
                if (SPLINE_TYPES[i].equals(selected)) {
                    idx = i;
                    break;
                }
            }
            if (idx >= 0) {
                state.currentSpline = SPLINE_IMPLS[idx];
                if (state.renderer != null) {
                    state.renderer.markDirty();
                }
            }
        });
        panel.addChild(comboBox);

        var countTitle = new Label("控制点数量", TextAlign.LEFT, 0xFFE2E8F0);
        countTitle.setCSSClassName("spline-subtitle");
        panel.addChild(countTitle);

        var dragBox = new DragBox(4, 10, 4);
        dragBox.setCSSClassName("spline-drag");
        panel.addChild(dragBox);

        var valueLabel = new Label("当前: 4 个控制点", TextAlign.LEFT, 0xFFE2E8F0);
        valueLabel.setCSSClassName("spline-value");
        panel.addChild(valueLabel);
        state.pointCountLabel = valueLabel;

        dragBox.setOnChange(v -> {
            int newCount = (int) Math.round(v);
            if (newCount != state.controlPointCount) {
                state.controlPointCount = newCount;
                valueLabel.setText("当前: " + newCount + " 个控制点");
                dragBox.submitTreeUpdate(() -> updateControlPoints(state));
            }
        });

        return panel;
    }

    private static void updateControlPoints(SplineState state) {
        if (state.renderer == null || state.renderer.getParent() == null) return;

        var scalableContainer = (ScalableContainer) state.renderer.getParent();

        
        var existingPositions = new ArrayList<float[]>();
        float sumX = 0, sumY = 0;
        for (var cp : state.controlPoints) {
            existingPositions.add(new float[]{cp.getCenterX(), cp.getCenterY()});
            sumX += cp.getCenterX();
            sumY += cp.getCenterY();
            scalableContainer.removeChild(cp);
        }
        
        float centerX = existingPositions.isEmpty() ? 400 : sumX / existingPositions.size();
        float centerY = existingPositions.isEmpty() ? 300 : sumY / existingPositions.size();

     
        state.controlPoints.clear();
    
        for (int i = 0; i < state.controlPointCount; i++) {
            float x, y;
            if (i < existingPositions.size()) {
                x = existingPositions.get(i)[0] - CONTROL_POINT_SIZE / 2f;
                y = existingPositions.get(i)[1] - CONTROL_POINT_SIZE / 2f;
            } else {
                x = centerX - CONTROL_POINT_SIZE / 2f;
                y = centerY - CONTROL_POINT_SIZE / 2f;
            }
            var point = new DraggableControlPoint("P" + i, CONTROL_POINT_COLORS[i % CONTROL_POINT_COLORS.length], x, y);
            state.controlPoints.add(point);
            scalableContainer.addChild(point);
        }

        state.renderer.markDirty();
    }

    private static ContainerWidget createRightPanel(SplineState state) {
        var scalableContainer = new ScalableContainer();
        scalableContainer.setCSSClassName("spline-right");
        scalableContainer.inlineStyle("background-color: 0xFF0F172A;");

        var renderer = new SplineRendererWidget(state);
        renderer.setAbsoluteSize(0, 0);
        renderer.setCSSClassName("spline-renderer");
        scalableContainer.addChild(renderer);
        state.renderer = renderer;

        float[][] initialPositions = {
                {100, 100},
                {200, 80},
                {300, 200},
                {400, 150}
        };
        for (int i = 0; i < 4; i++) {
            var point = new DraggableControlPoint(
                    "P" + i,
                    CONTROL_POINT_COLORS[i],
                    initialPositions[i][0],
                    initialPositions[i][1]
            );
            state.controlPoints.add(point);
            scalableContainer.addChild(point);
        }

        return scalableContainer;
    }

    private static class DraggableControlPoint extends DraggableContainer {

        public DraggableControlPoint(String name, int color, float x, float y) {
            this.setAbsoluteSize(x, y);
            this.setCSSClassName("spline-point-container");

            this.inlineStyle("background-color: " + color + ";");

            var label = new Label(name, TextAlign.CENTER, 0xFFFFFFFF);
            label.setCSSClassName("spline-point-label");
            this.addChild(label);
        }

        public float getCenterX() {
            return this.x + CONTROL_POINT_SIZE / 2f;
        }

        public float getCenterY() {
            return this.y + CONTROL_POINT_SIZE / 2f;
        }
    }

    private static class SplineRendererWidget extends Widget {
        private final SplineState state;
        private final List<Vector2f> interpolatedPoints = new ArrayList<>();

        public SplineRendererWidget(SplineState state) {
            this.state = state;
        }

        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            if (state.controlPoints.size() < 4) return;
            int lineColor = 0x40FFFFFF;
            for (int i = 0; i < state.controlPoints.size() - 1; i++) {
                graphics.renderLine(
                        state.controlPoints.get(i).getCenterX(), state.controlPoints.get(i).getCenterY(),
                        state.controlPoints.get(i + 1).getCenterX(), state.controlPoints.get(i + 1).getCenterY(),
                        lineColor
                );
            }
            updateInterpolatedPoints();
            int splineColor = 0xFF00FFFF;
            for (int i = 0; i < interpolatedPoints.size() - 1; i++) {
                Vector2f p1 = interpolatedPoints.get(i);
                Vector2f p2 = interpolatedPoints.get(i + 1);
                graphics.renderLine(p1.x, p1.y, p2.x, p2.y, splineColor);
            }
        }

        private void updateInterpolatedPoints() {
            interpolatedPoints.clear();

            var points = state.controlPoints;
            var spline = state.currentSpline;
            int step = Math.max(1, spline.getStep());

            if (points.size() >= 4) {
                for (int seg = 0; seg + 3 < points.size(); seg += step) {
                    Vector2f p0 = new Vector2f(points.get(seg).getCenterX(), points.get(seg).getCenterY());
                    Vector2f p1 = new Vector2f(points.get(seg + 1).getCenterX(), points.get(seg + 1).getCenterY());
                    Vector2f p2 = new Vector2f(points.get(seg + 2).getCenterX(), points.get(seg + 2).getCenterY());
                    Vector2f p3 = new Vector2f(points.get(seg + 3).getCenterX(), points.get(seg + 3).getCenterY());
                    int endT = INTERPOLATION_COUNT;

                    for (int i = 0; i <= endT; i++) {
                        float t = (float) i / INTERPOLATION_COUNT;
                        Vector2f point = spline.getPoint(t, p0, p1, p2, p3);
                        interpolatedPoints.add(point);
                    }
                }
            }
        }
    }
}
