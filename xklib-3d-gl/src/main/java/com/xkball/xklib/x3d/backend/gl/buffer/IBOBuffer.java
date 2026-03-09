package com.xkball.xklib.x3d.backend.gl.buffer;

import com.xkball.xklib.x3d.backend.gl.GLStateManager;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import com.xkball.xklib.utils.MathUtils;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15C.*;

@Deprecated
public class IBOBuffer implements AutoCloseable {
    
    private static final AutoStorageIBO sharedSequential = new AutoStorageIBO(1, 1, java.util.function.IntConsumer::accept);
    private static final AutoStorageIBO sharedSequentialQuad = new AutoStorageIBO(4, 6, (consumer, accepted) -> {
        consumer.accept(accepted);
        consumer.accept(accepted + 1);
        consumer.accept(accepted + 2);
        consumer.accept(accepted + 2);
        consumer.accept(accepted + 3);
        consumer.accept(accepted);
    });
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IBOBuffer.class);
    private final int id;
    private final int usage;
    private int size;
    private boolean destroyed;
    
    public IBOBuffer(int usage) {
        this.id = glGenBuffers();
        this.usage = usage;
        this.size = 0;
        this.destroyed = false;
    }
    
    public IBOBuffer() {
        this(GL_DYNAMIC_DRAW);
    }
    
    public void bind() {
        if (destroyed) {
            throw new IllegalStateException("IBO has been destroyed");
        }
        GLStateManager.INSTANCE.get().bindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    public static void unbind() {
        GLStateManager.INSTANCE.get().bindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    public void upload(ByteBuffer data) {
        if (destroyed) {
            throw new IllegalStateException("IBO has been destroyed");
        }
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage);
        size = data.remaining();
    }
    
    public void upload(long offset, ByteBuffer data) {
        if (destroyed) {
            throw new IllegalStateException("IBO has been destroyed");
        }
        bind();
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, offset, data);
    }
    
    public void allocate(int size) {
        if (destroyed) {
            throw new IllegalStateException("IBO has been destroyed");
        }
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, usage);
        this.size = size;
    }
    
    public int getId() {
        return id;
    }
    
    public int getSize() {
        return size;
    }

    public void destroy() {
        if (!destroyed) {
            glDeleteBuffers(id);
            destroyed = true;
        }
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void close() {
        destroy();
    }
    
    public static AutoStorageIBO getSequentialBuffer(VertexFormat.Mode formatMode) {
        return switch (formatMode) {
            case QUADS -> sharedSequentialQuad;
//            case LINES -> sharedSequentialLines;
            default -> sharedSequential;
        };
    }
    
    public static final class AutoStorageIBO {
        public final int vertexStride;
        public final int indexStride;
        private final IndexGenerator generator;
        private @Nullable IBOBuffer buffer;
        private VertexFormat.IndexType type = VertexFormat.IndexType.SHORT;
        private int indexCount;
        
        AutoStorageIBO(int vertexStride, int indexStride, IndexGenerator generator) {
            this.vertexStride = vertexStride;
            this.indexStride = indexStride;
            this.generator = generator;
        }
        
        public boolean hasStorage(int index) {
            return index <= this.indexCount;
        }
        
        public IBOBuffer getBuffer(int index) {
            this.ensureStorage(index);
            return this.buffer;
        }
        
        private void ensureStorage(int neededIndexCount) {
            if (!this.hasStorage(neededIndexCount)) {
                neededIndexCount = MathUtils.roundToward(neededIndexCount * 2, this.indexStride);
                LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.indexCount, neededIndexCount);
                int i = neededIndexCount / this.indexStride;
                int j = i * this.vertexStride;
                VertexFormat.IndexType vertexformat$indextype = VertexFormat.IndexType.least(j);
                int k = MathUtils.roundToward(neededIndexCount * vertexformat$indextype.bytes, 4);
                ByteBuffer bytebuffer = MemoryUtil.memAlloc(k);
                
                try {
                    this.type = vertexformat$indextype;
                    IntConsumer intconsumer = this.intConsumer(bytebuffer);
                    
                    for (int l = 0; l < neededIndexCount; l += this.indexStride) {
                        this.generator.accept(intconsumer, l * this.vertexStride / this.indexStride);
                    }
                    
                    bytebuffer.flip();
                    if (this.buffer != null) {
                        this.buffer.close();
                    }
                    
                    this.buffer = new IBOBuffer();
                    this.buffer.upload(bytebuffer);
                } finally {
                    MemoryUtil.memFree(bytebuffer);
                }
                
                this.indexCount = neededIndexCount;
            }
        }
        
        private IntConsumer intConsumer(ByteBuffer buffer) {
            return switch (this.type) {
                case SHORT -> i -> buffer.putShort((short) i);
                default -> buffer::putInt;
            };
        }
        
        public VertexFormat.IndexType type() {
            return this.type;
        }
        
        interface IndexGenerator {
            void accept(IntConsumer consumer, int index);
        }
    }
}
