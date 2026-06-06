package com.xkball.xklibmc.client.b3d;

import com.xkball.xklibmc.XKLibMC;
import com.xkball.xklibmc.utils.VanillaUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL30C;

@EventBusSubscriber(value = Dist.CLIENT, modid = XKLibMC.MODID)
public class TriangleCounter {

    public static final TriangleCounter INSTANCE = new TriangleCounter();
    private static final int HISTORY_SIZE = 100;

    private final int[] queryIds = new int[2];
    private boolean initialized = false;
    private int frameIndex = 0;
    private boolean shouldQuery = false;
    private boolean queryActive = false;
    private long currentResult = 0;
    private final long[] history = new long[HISTORY_SIZE];
    private int historyIndex = 0;
    private int historyCount = 0;

    private void ensureInit() {
        if (initialized) return;
        queryIds[0] = GL15C.glGenQueries();
        queryIds[1] = GL15C.glGenQueries();
        initialized = true;
    }

    public void beginFrame() {
        ensureInit();
        if (!shouldQuery) return;

        int prevIndex = 1 - frameIndex;
        int available = GL15C.glGetQueryObjectui(queryIds[prevIndex], GL15C.GL_QUERY_RESULT_AVAILABLE);
        if (available != 0) {
            int raw = GL15C.glGetQueryObjectui(queryIds[prevIndex], GL15C.GL_QUERY_RESULT);
            currentResult = Integer.toUnsignedLong(raw);
            history[historyIndex] = currentResult;
            historyIndex = (historyIndex + 1) % HISTORY_SIZE;
            if (historyCount < HISTORY_SIZE) historyCount++;
        }

        GL15C.glBeginQuery(GL30C.GL_PRIMITIVES_GENERATED, queryIds[frameIndex]);
        queryActive = true;
        shouldQuery = false;
    }

    public void endFrame() {
        if (!queryActive) return;
        GL15C.glEndQuery(GL30C.GL_PRIMITIVES_GENERATED);
        queryActive = false;
        frameIndex = 1 - frameIndex;
    }

    public void markShouldQuery() {
        shouldQuery = true;
    }

    public long getCurrentResult() {
        return currentResult;
    }

    public double getAverage() {
        if (historyCount == 0) return 0;
        long sum = 0;
        for (int i = 0; i < historyCount; i++) {
            sum += history[i];
        }
        return (double) sum / historyCount;
    }

    public int getHistoryCount() {
        return historyCount;
    }

    @SubscribeEvent
    public static void onRegisterDebugEntries(RegisterDebugEntriesEvent event) {
        event.register(
                VanillaUtils.modRL("triangle_counter"),
                new TriangleDebugEntry()
        );
    }
}
