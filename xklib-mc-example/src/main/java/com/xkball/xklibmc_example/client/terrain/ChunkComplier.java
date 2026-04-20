package com.xkball.xklibmc_example.client.terrain;

import com.xkball.xklibmc.client.TextureSpriteAvgColorCache;
import com.xkball.xklibmc.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

public class ChunkComplier {
    
    private static final TextureSpriteAvgColorCache textureColorCache = new TextureSpriteAvgColorCache();
    private static final Map<BlockState, Identifier> BLOCK_STATE_OVERRIDE = Map.of(
            Blocks.GRASS_BLOCK.defaultBlockState().setValue(BlockStateProperties.SNOWY,true),Identifier.parse("block/snow")
    );
    private static final Map<Block, Identifier> BLOCK_SPRITE_OVERRIDE = Map.of(
            Blocks.GRASS_BLOCK, Identifier.parse("block/grass_block_top"),
            Blocks.SNOW, Identifier.parse("block/snow")
            
    );
    
    public @Nullable ChunkStorage compile(LevelChunkStorage storage, ClientLevel level, ChunkPos chunkPos) {
        var chunkStorage = new ChunkStorage(chunkPos, storage);
        if (level.getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL, false) == null)  return null;
        var directions = Direction.values();
        var mc = Minecraft.getInstance();
        var modelManager = mc.getModelManager().getBlockStateModelSet();
        var result = new ArrayList<ABlock.ABlockData>();
        var pos = new BlockPos(0,0,0).mutable();
        var chunkMinY = level.getMinY();
        var chunkMaxY = level.getMaxY();
        var heightMap = new ChunkHeightMap();
        for (int px = chunkPos.getMinBlockX(); px <= chunkPos.getMaxBlockX(); px++) {
            for (int pz = chunkPos.getMinBlockZ(); pz <= chunkPos.getMaxBlockZ(); pz++) {
                var hMax = level.getHeight(Heightmap.Types.WORLD_SURFACE,px,pz);
                var hMin = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz) - 2;
                var hm = hMax - 1;
                heightMap.set(px,pz,hm);
                pos.set(px,hm,pz);
                var bs_ = level.getBlockState(pos);
                heightMap.setColor(px,pz,processBlockColor(level,bs_,pos,modelManager));
                var h1 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px+1,pz) - 1;
                if(h1 > level.getMinY()) hMin = Math.min(hMin,h1);
                var h2 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px-1,pz) - 1;
                if(h2 > level.getMinY()) hMin = Math.min(hMin,h2);
                var h3 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz+1) - 1;
                if(h3 > level.getMinY()) hMin = Math.min(hMin,h3);
                var h4 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz-1) - 1;
                if(h4 > level.getMinY()) hMin = Math.min(hMin,h4);
                hMin = Math.clamp(hMin, level.getMinY(), hMax);
                chunkMinY = Math.min(chunkMinY,hMin);
                chunkMaxY = Math.max(chunkMaxY,hMax);
                for (int y = hMin; y < hMax; y++) {
                    pos.set(px, y, pz);
                    var bs = level.getBlockState(pos);
                    if (bs.isAir()) {
                        continue;
                    }
                    var allBlock = true;
                    int mask = 0;
                    for(var dir : directions){
                        var b = pos.move(dir);
                        if(!level.getBlockState(b).isSolidRender()){
                            allBlock = false;
                            mask = mask | (1 << dir.get3DDataValue());
                        }
                        pos.move(dir,-1);
                    }
                    if(allBlock){
                        continue;
                    }
                    var color = processBlockColor(level, bs, pos, modelManager);
                    result.add(new ABlock.ABlockData(px,y, pz, color, mask));
                }
            }
        }
        chunkStorage.chunkAABB = new AABB(chunkPos.getMinBlockX(), chunkMinY, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), chunkMaxY, chunkPos.getMaxBlockZ());
        chunkStorage.writeData(result);
        chunkStorage.heightMap = heightMap;
        chunkStorage.markDirty();
        return chunkStorage;
    }
    
    private static int processBlockColor(ClientLevel level,BlockState bs, BlockPos pos, BlockStateModelSet modelManager){
        var model = modelManager.get(bs);
        TextureAtlasSprite sprite;
        var rl = BLOCK_STATE_OVERRIDE.get(bs);
        if(rl == null) rl = BLOCK_SPRITE_OVERRIDE.get(bs.getBlock());
        if(rl != null){
            sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(rl);
        }
        else{
            sprite = model.particleMaterial(level, pos, bs).sprite();
        }
        var color = textureColorCache.getAvgColor(sprite);
        return VanillaUtils.mulColor(color, getBlockColor(level, pos, bs));
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
}
