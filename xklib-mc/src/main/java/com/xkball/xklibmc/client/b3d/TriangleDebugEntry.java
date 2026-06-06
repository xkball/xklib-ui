package com.xkball.xklibmc.client.b3d;

import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class TriangleDebugEntry implements DebugScreenEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        TriangleCounter.INSTANCE.markShouldQuery();

        long current = TriangleCounter.INSTANCE.getCurrentResult();
        double avg = TriangleCounter.INSTANCE.getAverage();
        int count = TriangleCounter.INSTANCE.getHistoryCount();

        displayer.addLine(String.format(Locale.ROOT, "Triangles: %d (avg %d over %d frames)", current, (long) avg, count));
    }

    @Override
    public DebugEntryCategory category() {
        return DebugEntryCategory.RENDERER;
    }
}
