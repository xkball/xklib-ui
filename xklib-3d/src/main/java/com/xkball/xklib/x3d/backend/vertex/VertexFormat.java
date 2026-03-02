package com.xkball.xklib.x3d.backend.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.List;

public class VertexFormat {
    public static final int UNKNOWN_ELEMENT = -1;
    private final List<VertexFormatElement> elements;
    private final List<String> names;
    private final int vertexSize;
    private final Int2IntMap elementsOffset = new Int2IntArrayMap();

    VertexFormat(List<VertexFormatElement> elements, List<String> names, IntList offsets, int vertexSize) {
        this.elements = elements;
        this.names = names;
        this.vertexSize = vertexSize;

        for(var ele : elements){
            this.elementsOffset.put(ele.id(),offsets.getInt(elements.indexOf(ele)));
        }
    }
    
    public static VertexFormat.Builder builder() {
        return new VertexFormat.Builder();
    }

    @Override
    public String toString() {
        return "VertexFormat" + this.names;
    }

    public int getVertexSize() {
        return this.vertexSize;
    }

    public List<VertexFormatElement> getElements() {
        return this.elements;
    }

    public List<String> getElementAttributeNames() {
        return this.names;
    }

    public int getOffset(VertexFormatElement element) {
        return this.elementsOffset.get(element.id());
    }

    public boolean contains(VertexFormatElement element) {
        return this.elementsOffset.containsKey(element.id());
    }
    
    public static class Builder {
        private final ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
        private final IntList offsets = new IntArrayList();
        private int offset;

        Builder() {
        }

        public VertexFormat.Builder add(String name, VertexFormatElement element) {
            this.elements.put(name, element);
            this.offsets.add(this.offset);
            this.offset = this.offset + element.byteSize();
            return this;
        }

        public VertexFormat.Builder padding(int padding) {
            this.offset += padding;
            return this;
        }

        public VertexFormat build() {
            ImmutableMap<String, VertexFormatElement> immutablemap = this.elements.buildOrThrow();
            ImmutableList<VertexFormatElement> immutablelist = immutablemap.values().asList();
            ImmutableList<String> immutablelist1 = immutablemap.keySet().asList();
            return new VertexFormat(immutablelist, immutablelist1, this.offsets, this.offset);
        }
    }
    
    public enum IndexType {
        SHORT(2),
        INT(4);

        public final int bytes;

        IndexType(int bytes) {
            this.bytes = bytes;
        }

        public static VertexFormat.IndexType least(int indexCount) {
            return (indexCount & -65536) != 0 ? INT : SHORT;
        }
        
        public int toGl(){
            return toGl(this);
        }
        
        public static int toGl(VertexFormat.IndexType indexType) {
            return switch (indexType) {
                case SHORT -> 5123;
                case INT -> 5125;
            };
        }
    }
    
    public enum Mode {
        LINES(2, 2, false),
        DEBUG_LINES(2, 2, false),
        DEBUG_LINE_STRIP(2, 1, true),
        POINTS(1, 1, false),
        TRIANGLES(3, 3, false),
        TRIANGLE_STRIP(3, 1, true),
        TRIANGLE_FAN(3, 1, true),
        QUADS(4, 4, false);

        public final int primitiveLength;
        public final int primitiveStride;
        public final boolean connectedPrimitives;

        Mode(int primitiveLength, int primitiveStride, boolean connectedPrimitives) {
            this.primitiveLength = primitiveLength;
            this.primitiveStride = primitiveStride;
            this.connectedPrimitives = connectedPrimitives;
        }

        public int indexCount(int vertices) {
            return switch (this) {
                case LINES, QUADS -> vertices / 4 * 6;
                case DEBUG_LINES, DEBUG_LINE_STRIP, POINTS, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> vertices;
                default -> 0;
            };
        }
        
        public int toGl(){
            return toGl(this);
        }
        
        private static int toGl(VertexFormat.Mode mode) {
            return switch (mode) {
                case LINES -> 4;
                case DEBUG_LINES -> 1;
                case DEBUG_LINE_STRIP -> 3;
                case POINTS -> 0;
                case TRIANGLES -> 4;
                case TRIANGLE_STRIP -> 5;
                case TRIANGLE_FAN -> 6;
                case QUADS -> 4;
            };
        }
    }

    public boolean hasPosition() {
        return elements.stream().anyMatch(e -> e.usage() == VertexFormatElement.Usage.POSITION);
    }

    public boolean hasNormal() {
        return elements.stream().anyMatch(e -> e.usage() == VertexFormatElement.Usage.NORMAL);
    }

    public boolean hasColor() {
        return elements.stream().anyMatch(e -> e.usage() == VertexFormatElement.Usage.COLOR);
    }

    public boolean hasUV(int which) {
        return elements.stream().anyMatch(e -> e.usage() == VertexFormatElement.Usage.UV && e.index() == which);
    }
}