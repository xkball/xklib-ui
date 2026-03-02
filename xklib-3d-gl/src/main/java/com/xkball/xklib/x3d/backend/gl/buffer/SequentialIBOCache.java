package com.xkball.xklib.x3d.backend.gl.buffer;

import com.xkball.xklib.x3d.api.render.IBufferSource;
import com.xkball.xklib.x3d.api.render.IGpuBuffer;
import com.xkball.xklib.x3d.backend.buffer.GpuBuffer;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import com.xkball.xklib.utils.MathUtils;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

public class SequentialIBOCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequentialIBOCache.class);

    private static final ConcurrentHashMap<VertexFormat.Mode, SequentialIBOCache> CACHE = new ConcurrentHashMap<>();

    static {
        CACHE.put(VertexFormat.Mode.TRIANGLES, new SequentialIBOCache(1, 1, IntConsumer::accept));
        CACHE.put(VertexFormat.Mode.QUADS, new SequentialIBOCache(4, 6, (consumer, accepted) -> {
            consumer.accept(accepted);
            consumer.accept(accepted + 1);
            consumer.accept(accepted + 2);
            consumer.accept(accepted + 2);
            consumer.accept(accepted + 3);
            consumer.accept(accepted);
        }));
    }

    public final int vertexStride;
    public final int indexStride;
    private final IndexGenerator generator;
    private @Nullable GpuBuffer buffer;
    private VertexFormat.IndexType type = VertexFormat.IndexType.SHORT;
    private int indexCount;

    private SequentialIBOCache(int vertexStride, int indexStride, IndexGenerator generator) {
        this.vertexStride = vertexStride;
        this.indexStride = indexStride;
        this.generator = generator;
    }

    public static SequentialIBOCache getFor(VertexFormat.Mode mode) {
        var cache = CACHE.get(mode);
        if (cache == null) {
            cache = CACHE.computeIfAbsent(mode, _ -> new SequentialIBOCache(1, 1, IntConsumer::accept));
        }
        return cache;
    }

    public boolean hasStorage(int index) {
        return index <= this.indexCount;
    }

    public GpuBuffer getBuffer(int neededIndexCount) {
        ensureStorage(neededIndexCount);
        return this.buffer;
    }

    public VertexFormat.IndexType type() {
        return this.type;
    }

    private void ensureStorage(int neededIndexCount) {
        if (!this.hasStorage(neededIndexCount)) {
            neededIndexCount = MathUtils.roundToward(neededIndexCount * 2, this.indexStride);
            LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.indexCount, neededIndexCount);
            int i = neededIndexCount / this.indexStride;
            int j = i * this.vertexStride;
            VertexFormat.IndexType indexType = VertexFormat.IndexType.least(j);
            int k = MathUtils.roundToward(neededIndexCount * indexType.bytes, 4);
            ByteBuffer bytebuffer = MemoryUtil.memAlloc(k);

            try {
                this.type = indexType;
                IntConsumer intconsumer = this.intConsumer(bytebuffer);

                for (int l = 0; l < neededIndexCount; l += this.indexStride) {
                    this.generator.accept(intconsumer, l * this.vertexStride / this.indexStride);
                }

                bytebuffer.flip();
                if (this.buffer != null) {
                    this.buffer.close();
                }

                this.buffer = IBufferSource.getInstance().createBuffer(
                        () -> "SequentialIBO",
                        IGpuBuffer.USAGE_INDEX,
                        bytebuffer
                );
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

    interface IndexGenerator {
        void accept(IntConsumer consumer, int index);
    }
}
