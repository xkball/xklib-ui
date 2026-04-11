package com.xkball.xklibmc.client.terrain;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.xklibmc.api.client.b3d.ICloseOnExit;
import com.xkball.xklibmc.api.client.b3d.ISTD140Writer;
import com.xkball.xklibmc.client.TextureSpriteAvgColorCache;
import com.xkball.xklibmc.client.b3d.IndirectDrawCommand;
import com.xkball.xklibmc.client.b3d.buffer.ManagedGpuBuffer;
import com.xkball.xklibmc.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

public class TerrainChunkManager implements ICloseOnExit<TerrainChunkManager> {
    
    private static final TextureSpriteAvgColorCache textureColorCache = new TextureSpriteAvgColorCache();
    private static final Map<Block, Identifier> BLOCK_SPRITE_OVERRIDE = Map.of(
            Blocks.GRASS_BLOCK, Identifier.parse("block/grass_block_top")
    );
    public static final int BLOCK_SIZE = new ABlock().byteSize();
    public final Map<ChunkPos, TerrainChunkBuffer> chunkMap = new LinkedHashMap<>();
    public final Queue<ChunkPos> updateQueue = new ArrayDeque<>();
    public final ManagedGpuBuffer gpuBuffer;
    
    public TerrainChunkManager() {
        this.gpuBuffer = new ManagedGpuBuffer(BLOCK_SIZE * 16 * 16);
    }
    
    public RenderInfo generateRenderInfo(Predicate<ChunkPos> cullFunc, int indexCount){
        var cmdList = new ArrayList<IndirectDrawCommand>();
        for(var chunkPos : chunkMap.keySet()){
            if(cullFunc.test(chunkPos)){
                var chunkBuffer =  chunkMap.get(chunkPos);
                for(var entry : chunkBuffer.inChunkMap.int2IntEntrySet()){
                    var offset = gpuBuffer.getOffset(entry.getIntKey());
                    cmdList.add(new IndirectDrawCommand(indexCount, entry.getIntValue(),(int) (offset / BLOCK_SIZE)));
                }
            }
        }
        var cmdBuffer = IndirectDrawCommand.buildCommandList(cmdList);
        return new RenderInfo(cmdList.size(),cmdBuffer);
    }
    
    public Map<Integer, Integer> generateRenderOffsetAndInstance(Predicate<ChunkPos> cullFunc){
        var result = new HashMap<Integer, Integer>();
        for(var chunkPos : chunkMap.keySet()){
            if(cullFunc.test(chunkPos)){
                var chunkBuffer =  chunkMap.get(chunkPos);
                for(var entry : chunkBuffer.inChunkMap.int2IntEntrySet()){
                    var buffer = gpuBuffer.get(entry.getIntKey()).slice();
                    result.put((int) buffer.offset(),  entry.getIntValue());
                }
            }
        }
        return result;
    }
    
    public void runUpdateFor10ms(ClientLevel level) {
        var time = System.nanoTime();
        while (!updateQueue.isEmpty() && System.nanoTime() - time < 10_000_000L){
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
        level.getBlockState(chunkPos.getBlockAt(0,0,0));
        if(level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,chunkPos.getMinBlockX(),chunkPos.getMinBlockZ()) == level.getMinY()) return;
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
                    TextureAtlasSprite sprite;
                    var rl = BLOCK_SPRITE_OVERRIDE.get(bs.getBlock());
                    if(rl != null){
                        sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(rl);
                    }
                    else{
                        sprite = model.particleMaterial(level, pos, bs).sprite();
                    }
                    var color = textureColorCache.getAvgColor(sprite);
                    color = VanillaUtils.mulColor(color, getBlockColor(level, pos, bs));
                    result.add(new ABlock(new BlockPos(px,y, pz), color));
                }
            }
        }
        if(this.chunkMap.containsKey(chunkPos)){
            for(var entry : this.chunkMap.get(chunkPos).inChunkMap.int2IntEntrySet()){
                gpuBuffer.remove(entry.getIntKey());
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
    
    public record RenderInfo(int drawCount, GpuBuffer commandBuffer) implements AutoCloseable{
        
        @Override
        public void close() {
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
