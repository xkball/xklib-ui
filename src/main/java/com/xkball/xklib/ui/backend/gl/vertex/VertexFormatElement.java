package com.xkball.xklib.ui.backend.gl.vertex;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public record VertexFormatElement(int id, int index, VertexFormatElement.Type type, VertexFormatElement.Usage usage, int count) {
    
    private static final Int2ObjectMap<VertexFormatElement> BY_ID = new Int2ObjectArrayMap<>();
    private static final List<VertexFormatElement> ELEMENTS = new ArrayList<>(32);
    public static final VertexFormatElement POSITION = register(0, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
    public static final VertexFormatElement COLOR = register(1, 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
    public static final VertexFormatElement UV = register(2, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
    public static final VertexFormatElement NORMAL = register(5, 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
    public static final VertexFormatElement LINE_WIDTH = register(6, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 1);
    
    public VertexFormatElement(int id, int index, VertexFormatElement.Type type, VertexFormatElement.Usage usage, int count) {
        if (id < 0 ) {
            throw new IllegalArgumentException("Element ID must be non-negative");
        } else if (!this.supportsUsage(index, usage)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
        } else {
            this.id = id;
            this.index = index;
            this.type = type;
            this.usage = usage;
            this.count = count;
        }
    }
    
    public static VertexFormatElement register(
            int id, int index, VertexFormatElement.Type type, VertexFormatElement.Usage usage, int count
    ) {
        VertexFormatElement vertexformatelement = new VertexFormatElement(id, index, type, usage, count);
        if (BY_ID.get(id) != null) {
            throw new IllegalArgumentException("Duplicate element registration for: " + id);
        } else {
            BY_ID.put(id, vertexformatelement);
            ELEMENTS.add(vertexformatelement);
            return vertexformatelement;
        }
    }
    
    private boolean supportsUsage(int index, VertexFormatElement.Usage usage) {
        return index == 0 || usage == VertexFormatElement.Usage.UV;
    }
    
    @NotNull
    @Override
    public String toString() {
        return this.count + "," + this.usage + "," + this.type + " (" + this.id + ")";
    }
    
    public int byteSize() {
        return this.type.size() * this.count;
    }
    
    public static @Nullable VertexFormatElement byId(int id) {
        return BY_ID.get(id);
    }
    
    public static int findNextId() {
        return BY_ID.size();
    }
    
    public enum Type {
        FLOAT(4, "Float"),
        UBYTE(1, "Unsigned Byte"),
        BYTE(1, "Byte"),
        USHORT(2, "Unsigned Short"),
        SHORT(2, "Short"),
        UINT(4, "Unsigned Int"),
        INT(4, "Int");
        
        private final int size;
        private final String name;
        
        Type(int size, String name) {
            this.size = size;
            this.name = name;
        }
        
        public int size() {
            return this.size;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public enum Usage {
        POSITION("Position"),
        NORMAL("Normal"),
        COLOR("Vertex Color"),
        UV("UV"),
        GENERIC("Generic");
        
        private final String name;
        
        Usage(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
}