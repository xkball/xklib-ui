package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.utils.AdjacencyList;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FlameGraph extends Widget {
    
    private AdjacencyList<String, Long> data;
    private final Map<String, Integer> colorMap = new HashMap<>();
    private long totalTime = 1;
    private String rootNode;
    private final IntLayoutVariable rowHeight = new IntLayoutVariable(20);

    public void setRowHeight(int rowHeight) {
        this.rowHeight.setAsInt(rowHeight);
    }

    public void setData(AdjacencyList<String, Long> data) {
        this.data = data;
        this.rootNode = null;
        this.totalTime = 1;
        if (data == null) return;

        if (data.hasNode("frame")) {
            this.rootNode = "frame";
            Long val = data.getData("frame");
            this.totalTime = (val != null) ? val : 1;
        } else {
            long max = 0;
            String firstRoot = null;
            for (String node : childrenOf(null)) {
                if (firstRoot == null) {
                    firstRoot = node;
                }
                Long val = data.getData(node);
                if (val != null) max += val;
            }
            this.rootNode = firstRoot;
            this.totalTime = max > 0 ? max : 1;

            if (this.rootNode == null) {
                for (String node : data.getNodes()) {
                    this.rootNode = node;
                    Long val = data.getData(node);
                    if (val != null && val > 0) {
                        this.totalTime = val;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        if (data == null || data.nodeCount() == 0 || this.width <= 1 || this.height <= 1) return;

        float startX = this.absoluteX;
        float startY = this.absoluteY;

        if (rootNode != null && data.hasNode(rootNode)) {
            Long rootVal = data.getData(rootNode);
            long rv = rootVal != null && rootVal > 0 ? rootVal : totalTime;
            drawNode(graphics, rootNode, startX, startY, this.width, rv);
            return;
        }

        float currentX = startX;
        for (String node : childrenOf(null)) {
            Long val = data.getData(node);
            if (val == null || val <= 0) continue;
            float w = Math.max(1f, (float) val / totalTime * this.width);
            drawNode(graphics, node, currentX, startY, w, val);
            currentX += w;
            if (currentX >= this.absoluteX + this.width) {
                break;
            }
        }
    }

    private void drawNode(IGUIGraphics graphics, String node, float x, float y, float w, long nodeTime) {
        if (w < 1f || nodeTime <= 0) return;

        float h = rowHeight.get();
        if (y >= this.absoluteY + this.height) return;

        int color = getColor(node);
        graphics.submitColoredRoundedRectangle(x, y, x + w - 1, y + h - 1, color, color, 0);

        if (w > 30) {
            String text = node + " " + String.format("%.2f ms", nodeTime / 1000000.0);
            float textHeight = Math.max(8f, h - 6f);
            float textY = y + (h - textHeight) * 0.5f;
            graphics.drawString(graphics.defaultFont(), text, x + 2, textY, 0xFFFFFFFF, textHeight);
        }

        drawChildren(graphics, node, x, y, 1, w, nodeTime);
    }

    private void drawChildren(IGUIGraphics graphics, String parent, float x, float y, int depth, float parentWidth, long parentTime) {
        float currentX = x;
        float h = rowHeight.get();
        float currentY = y + depth * h;

        if (currentY >= this.absoluteY + this.height || parentWidth < 1f || parentTime <= 0) return;

        for (String child : childrenOf(parent)) {
            Long val = data.getData(child);
            if (val == null || val <= 0) continue;

            float w = (float) val / parentTime * parentWidth;
            if (w < 1f) continue;

            int color = getColor(child);
            graphics.submitColoredRoundedRectangle(currentX, currentY, currentX + w - 1, currentY + h - 1, color, color, 0);

            if (w > 30) {
                String text = child + " " + String.format("%.2f ms", val / 1000000.0);
                float textHeight = Math.max(8f, h - 6f);
                float textY = currentY + (h - textHeight) * 0.5f;
                graphics.drawString(graphics.defaultFont(), text, currentX + 2, textY, 0xFFFFFFFF, textHeight);
            }

            drawChildren(graphics, child, currentX, y, depth + 1, w, val);
            currentX += w;

            if (currentX >= x + parentWidth) {
                break;
            }
        }
    }

    private Iterable<String> childrenOf(String parent) {
        Iterable<String> children = data.getChild(parent);
        return children == null ? Collections.emptyList() : children;
    }

    private int getColor(String node) {
        return colorMap.computeIfAbsent(node, k -> {
            int h = k.hashCode();
            int r = (h & 0xFF0000) >> 16;
            int g = (h & 0x00FF00) >> 8;
            int b = (h & 0x0000FF);
             r = (r % 128) + 128;
             g = (g % 128) + 128;
             b = (b % 128) + 128;
            return 0xFF000000 | (r << 16) | (g << 8) | b;
        });
    }
}


