package com.xkball.xklibmc.client.terrain;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.xklibmc.api.client.b3d.ICloseOnExit;
import com.xkball.xklibmc.api.client.b3d.ISTD140Writer;
import com.xkball.xklibmc.client.TextureSpriteAvgColorCache;
import com.xkball.xklibmc.client.b3d.IndirectDrawCommand;
import com.xkball.xklibmc.client.b3d.buffer.ManagedGpuBuffer;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.VanillaUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

public class TerrainChunkManager implements ICloseOnExit<TerrainChunkManager> {
    
    private static final TextureSpriteAvgColorCache textureColorCache = new TextureSpriteAvgColorCache();
    public final Map<ChunkPos, TerrainChunkBuffer> chunkMap = new HashMap<>();
    public final Queue<ChunkPos> updateQueue = new ArrayDeque<>();
    public final ManagedGpuBuffer gpuBuffer;
    
    public TerrainChunkManager() {
        this.gpuBuffer = new ManagedGpuBuffer(new ABlock().byteSize() * 16 * 16);
    }
    
    public RenderInfo generateRenderOffsetAndInstance(Predicate<ChunkPos> cullFunc, int indexCount){
        var cmdList = new ArrayList<IndirectDrawCommand>();
        var chunkIndexList = new ArrayList<IntSSBOData>();
        for(var chunkPos : chunkMap.keySet()){
            if(cullFunc.test(chunkPos)){
                var chunkBuffer =  chunkMap.get(chunkPos);
                for(var entry : chunkBuffer.chunkMap.int2IntEntrySet()){
                    var buffer = gpuBuffer.get(entry.getIntKey()).slice();
                    cmdList.add(new IndirectDrawCommand(indexCount, entry.getIntValue()));
                    chunkIndexList.add(new IntSSBOData((int) (buffer.offset()/gpuBuffer.chunkSize)));
                }
            }
        }
        var cmdBuffer = IndirectDrawCommand.buildCommandList(cmdList);
        var chunkBuffer = ISTD140Writer.batchBuildStd140Block(chunkIndexList);
        return new RenderInfo(cmdList.size(), chunkBuffer, cmdBuffer);
    }
    
    public void runUpdateFor10ms(ClientLevel level) {
        var time = System.nanoTime();
        while (!updateQueue.isEmpty() && System.nanoTime() - time < 10_000_000_000L){
            this.compileChunk(level, Objects.requireNonNull(updateQueue.poll()));
        }
    }
    
    public void submitUpdate(BlockPos center, int range){
        var centerChunk = ChunkPos.containing(center);
        for(var dx = -range; dx <= range; dx++){
            for(var dz = -range; dz <= range; dz++){
                this.submitUpdate(new ChunkPos(centerChunk.x()+dx,centerChunk.z()+dz));
            }
        }
    }
    
    public void submitUpdate(ChunkPos chunkPos){
        this.updateQueue.add(chunkPos);
    }
    
    public void clear(){
        this.chunkMap.clear();
        this.updateQueue.clear();
        this.gpuBuffer.clear();
    }
    
    public void compileChunk(ClientLevel level, ChunkPos chunkPos){
        var directions = Direction.values();
        var mc = Minecraft.getInstance();
        var modelManager = mc.getModelManager().getBlockStateModelSet();
        var result = new ArrayList<ABlock>();
        var pos = new BlockPos(0,0,0).mutable();
        for (int px = chunkPos.getMinBlockX(); px <= chunkPos.getMaxBlockX(); px++) {
            for (int pz = chunkPos.getMinBlockZ(); pz <= chunkPos.getMaxBlockZ(); pz++) {
                var hMax = level.getHeight(Heightmap.Types.MOTION_BLOCKING,px,pz);
                var hMin = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz) - 1;
                var h1 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px+1,pz);
                if(h1 > level.getMinY()) hMin = Math.min(hMin,h1);
                var h2 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px-1,pz);
                if(h2 > level.getMinY()) hMin = Math.min(hMin,h2);
                var h3 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz+1);
                if(h3 > level.getMinY()) hMin = Math.min(hMin,h3);
                var h4 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz-1);
                if(h4 > level.getMinY()) hMin = Math.min(hMin,h4);
                hMin = Math.clamp(hMin, level.getMinY(), hMax);
                for (int y = hMin; y < hMax; y++) {
                    pos.set(px, y, pz);
                    var bs = level.getBlockState(pos);
                    if (bs.isAir()) {
                        continue;
                    }
                    var allBlock = true;
                    for(var dir : directions){
                        var b = pos.move(dir);
                        if(!level.getBlockState(b).isSolidRender()){
                            allBlock = false;
                            break;
                        }
                        pos.move(dir,-1);
                    }
                    if(allBlock){
                        continue;
                    }
                    var model = modelManager.get(bs);
                    var sprite = model.particleMaterial(level, pos, bs).sprite();
                    var color = textureColorCache.getAvgColor(sprite);
                    color = VanillaUtils.mulColor(color, getBlockColor(level, pos, bs));
                    result.add(new ABlock(new BlockPos(px,y, pz), color));
                }
            }
        }
        this.chunkMap.put(chunkPos, new TerrainChunkBuffer(gpuBuffer, result));
    }
    
    private static int getBlockColor(ClientLevel level, BlockPos pos, BlockState state){
        var mc = Minecraft.getInstance();
        var tintSource = mc.getBlockColors().getTintSources(state);
        if (tintSource.isEmpty()) return -1;
        var color = -1;
        for(var source : tintSource){
            color = VanillaUtils.mulColor(color,source.colorInWorld(state, level, pos));
        }
        return color;
    }
    
    @Override
    public void close() throws Exception {
        this.gpuBuffer.close();
    }
    
    public record RenderInfo(int drawCount, GpuBuffer chunkIndexBuffer, GpuBuffer commandBuffer) implements AutoCloseable{
        
        @Override
        public void close() throws Exception {
            this.chunkIndexBuffer.close();
            this.commandBuffer.close();
        }
    }
    
    public record IntSSBOData(int data) implements ISTD140Writer{
        
        @Override
        public void calculateSize(Std140SizeCalculator calculator) {
            calculator.putInt();
        }
        
        @Override
        public void writeToBuffer(Std140Builder builder) {
            builder.putInt(data);
        }
    }
}
