package com.xkball.xklib.x3d.backend.vertex;

import com.xkball.xklib.x3d.api.render.IGpuBuffer;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.function.LongConsumer;

public class BufferBuilder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BufferBuilder.class);
    
    private long bufferPointer;
    private long vertexPointer;
    private int vertices;
    private int bufferSize;
    private final VertexFormat format;
    private final VertexFormat.Mode mode;
    private final BitSet filledElements;
    private final BitSet expectedElements;
    private boolean building;
    
    private BufferBuilder(VertexFormat.Mode mode, VertexFormat format) {
        this.mode = mode;
        this.format = format;
        this.vertices = 0;
        this.vertexPointer = -1L;
        this.filledElements = new BitSet();
        this.building = true;
        int initialVertices = 4;
        this.bufferSize = initialVertices * format.getVertexSize();
        this.bufferPointer = MemoryUtil.nmemAlloc(this.bufferSize);
        this.expectedElements = new BitSet();
        for (VertexFormatElement element : format.getElements()) {
            expectedElements.set(element.id());
        }
        if(!format.contains(VertexFormatElement.POSITION)){
            throw new IllegalStateException("VertexFormat must contain POSITION element");
        }
    }
    
    public static BufferBuilder start(VertexFormat.Mode mode, VertexFormat format){
        return new BufferBuilder(mode, format);
    }
    
    public BufferBuilder addVertex(float x, float y, float z){
        if (!building) {
            throw new IllegalStateException("BufferBuilder is not building");
        }
        
        if (vertexPointer != -1L) {
            if (!filledElements.equals(expectedElements)) {
                LOGGER.warn("Previous vertex was not filled completely. Expected: {}, Got: {}", expectedElements, filledElements);
            }
        }
        
        ensureCapacity(format.getVertexSize());
        
        vertexPointer = bufferPointer + (long) vertices * format.getVertexSize();
        vertices += 1;
        filledElements.clear();
        
        VertexFormatElement posElement = VertexFormatElement.POSITION;
        int offset = format.getOffset(posElement);
        MemoryUtil.memPutFloat(vertexPointer + offset, x);
        MemoryUtil.memPutFloat(vertexPointer + offset + 4, y);
        MemoryUtil.memPutFloat(vertexPointer + offset + 8, z);
        filledElements.set(posElement.id());
        
        return this;
    }
    
    public BufferBuilder addVertexWith2DPose(Matrix3x2fc pose, float x, float y, float zOffset) {
        Vector2f pos = pose.transformPosition(x, y, new Vector2f());
        return this.addVertex(pos.x(), pos.y(), zOffset);
    }
    
    public BufferBuilder setColor(int red, int green, int blue, int alpha){
        if (vertexPointer == -1L) {
            throw new IllegalStateException("Must call addVertex first");
        }
        
        VertexFormatElement colorElement = VertexFormatElement.COLOR;
        if (!format.contains(colorElement)) {
            LOGGER.warn("VertexFormat does not contain COLOR element, skipping");
            return this;
        }
        
        int offset = format.getOffset(colorElement);
        MemoryUtil.memPutByte(vertexPointer + offset, (byte) red);
        MemoryUtil.memPutByte(vertexPointer + offset + 1, (byte) green);
        MemoryUtil.memPutByte(vertexPointer + offset + 2, (byte) blue);
        MemoryUtil.memPutByte(vertexPointer + offset + 3, (byte) alpha);
        filledElements.set(colorElement.id());
        
        return this;
    }
    
    public BufferBuilder setColor(int color){
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int alpha = (color >> 24) & 0xFF;
        return setColor(red, green, blue, alpha);
    }
    
    public BufferBuilder setUv(float u, float v){
        if (vertexPointer == -1L) {
            throw new IllegalStateException("Must call addVertex first");
        }
        
        VertexFormatElement uvElement = VertexFormatElement.UV;
        if (!format.contains(uvElement)) {
            LOGGER.warn("VertexFormat does not contain UV element, skipping");
            return this;
        }
        
        int offset = format.getOffset(uvElement);
        MemoryUtil.memPutFloat(vertexPointer + offset, u);
        MemoryUtil.memPutFloat(vertexPointer + offset + 4, v);
        filledElements.set(uvElement.id());
        
        return this;
    }
    
    public BufferBuilder setUv2(float u, float v){
        if (vertexPointer == -1L) {
            throw new IllegalStateException("Must call addVertex first");
        }
        
        VertexFormatElement uvElement = VertexFormatElement.UV2;
        if (!format.contains(uvElement)) {
            LOGGER.warn("VertexFormat does not contain UV2 element, skipping");
            return this;
        }
        
        int offset = format.getOffset(uvElement);
        MemoryUtil.memPutFloat(vertexPointer + offset, u);
        MemoryUtil.memPutFloat(vertexPointer + offset + 4, v);
        filledElements.set(uvElement.id());
        
        return this;
    }
    
    public BufferBuilder setNormal(float normalX, float normalY, float normalZ){
        if (vertexPointer == -1L) {
            throw new IllegalStateException("Must call addVertex first");
        }
        
        VertexFormatElement normalElement = VertexFormatElement.NORMAL;
        if (!format.contains(normalElement)) {
            LOGGER.warn("VertexFormat does not contain NORMAL element, skipping");
            return this;
        }
        
        int offset = format.getOffset(normalElement);
        MemoryUtil.memPutByte(vertexPointer + offset, (byte) (normalX * 127));
        MemoryUtil.memPutByte(vertexPointer + offset + 1, (byte) (normalY * 127));
        MemoryUtil.memPutByte(vertexPointer + offset + 2, (byte) (normalZ * 127));
        filledElements.set(normalElement.id());
        
        return this;
    }
    
    public BufferBuilder setUnsafe(VertexFormatElement element, LongConsumer ptr){
        if (vertexPointer == -1L) {
            throw new IllegalStateException("Must call addVertex first");
        }
        
        if (!format.contains(element)) {
            LOGGER.warn("VertexFormat does not contain element {}, skipping", element);
            return this;
        }
        
        int offset = format.getOffset(element);
        ptr.accept(vertexPointer + offset);
        filledElements.set(element.id());
        
        return this;
    }
    
    public ByteBuffer build(){
        if (!building) {
            throw new IllegalStateException("Already built");
        }
        
        building = false;
        int size = vertices * format.getVertexSize();
        return MemoryUtil.memByteBuffer(bufferPointer, size);
    }
    
    public int getVertexCount() {
        return vertices;
    }
    
    public IGpuBuffer buildAndUpload(){
        ByteBuffer buffer = build();
        //todo 实现方法
        this.free();
        return null;
    }
    
    private void ensureCapacity(int additional) {
        int needed = (int)(vertexPointer - bufferPointer) + format.getVertexSize() + additional;
        if (needed > bufferSize) {
            int newSize = Math.max(bufferSize * 2, needed);
            long newPointer = MemoryUtil.nmemRealloc(bufferPointer, newSize);
            if (newPointer == MemoryUtil.NULL) {
                throw new OutOfMemoryError("Failed to reallocate buffer");
            }
            bufferPointer = newPointer;
            bufferSize = newSize;
        }
    }
    
    public void free() {
        if (bufferPointer != MemoryUtil.NULL) {
            MemoryUtil.nmemFree(bufferPointer);
            bufferPointer = MemoryUtil.NULL;
            vertexPointer = -1L;
        }
    }
}
