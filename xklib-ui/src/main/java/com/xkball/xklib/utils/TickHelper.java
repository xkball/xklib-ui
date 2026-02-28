package com.xkball.xklib.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

public final class TickHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickHelper.class);
    private static final long MAX_PARK_NANOS = 1_000_000L;

    private long nanosPerTick;
    private long lastTickNanos;

    public TickHelper(double tps) {
        setTps(tps);
        this.lastTickNanos = 0L;
    }

    public void setTps(double tps) {
        if (tps <= 0.0) {
            throw new IllegalArgumentException("TPS must be positive: " + tps);
        }
        this.nanosPerTick = (long) (1_000_000_000L / tps);
    }

    public void tick() {
        long now = System.nanoTime();
        if (lastTickNanos == 0L) {
            lastTickNanos = now;
            return;
        }

        long elapsedNanos = now - lastTickNanos;
        if (elapsedNanos < nanosPerTick) {
            long remaining = nanosPerTick - elapsedNanos;
            while (remaining > 0L) {
                LockSupport.parkNanos(Math.min(remaining, MAX_PARK_NANOS));
                now = System.nanoTime();
                remaining = nanosPerTick - (now - lastTickNanos);
            }
        } else {
            long lateNanos = elapsedNanos - nanosPerTick;
            long ticksBehind = Math.max(1L, (lateNanos / nanosPerTick) + 1L);
            LOGGER.warn("Tick behind by {} ticks ({} ms)", ticksBehind, lateNanos / 1_000_000L);
        }

        lastTickNanos = System.nanoTime();
    }
}
