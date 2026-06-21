package com.xkball.xklibmc.utils;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.xkball.xklibmc.XKLibMCClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalDouble;

public class ClientUtils {
    
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static GpuDevice getGpuDevice(){
        return RenderSystem.getDevice();
    }
    
    public static CommandEncoder getCommandEncoder(){
        return RenderSystem.getDevice().createCommandEncoder();
    }
    
    public static RenderPass createRenderPass(String name){
        var colorTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTextureView();
        var depthTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget().getDepthTextureView();
        //noinspection DataFlowIssue
        return getCommandEncoder().createRenderPass(() -> name, colorTarget, Optional.empty(), depthTarget, OptionalDouble.empty());
    }
    
    public static PoseStack fromPose(PoseStack.Pose pose){
        var result = new PoseStack();
        result.last().set(pose);
        return result;
    }
    
    public static float clientTickWithPartialTick(){
        return XKLibMCClient.tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
    }
    
//    public static void putModelToBuffer(PoseStack poseStack, BufferBuilder builder, Collection<BakedQuad> quads, int color){
//        var color_ = ColorUtils.Vectorization.argbColor(color);
//        for(var quad : quads){
//            builder.putBulkData(poseStack.last(),quad,color_.x,color_.y,color_.z,color_.w, LightTexture.pack(15,15), OverlayTexture.NO_OVERLAY);
//        }
//    }
    
    public static AbstractTexture getTexture(String texture){
        return Minecraft.getInstance().getTextureManager().getTexture(VanillaUtils.modRL(texture));
    }
    
    public static AbstractTexture getTexture(Identifier texture){
        return Minecraft.getInstance().getTextureManager().getTexture(texture);
    }
    
    public static TextureAtlasSprite getTextureFromAtlas(Identifier atlas, String id){
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(atlas).getSprite(VanillaUtils.modRL(id));
    }
    
    public static TextureAtlasSprite getTextureFromAtlas(String id){
        return getTextureFromAtlas(AtlasIds.BLOCKS, id);
    }
    
    public static @Nullable NativeImage readImage(Identifier rl){
        NativeImage result;
        var resource = Minecraft.getInstance().getResourceManager().getResource(rl);
        if(resource.isEmpty()) return null;
        try(var stream = resource.get().open()) {
            result = NativeImage.read(stream);
        } catch (IOException e) {
            LOGGER.error("Failed to read image {}", rl, e);
            return null;
        }
        return result;
    }
    
    public static void getGLError(){
        int error = GL11.glGetError();
        while(error != GL11.GL_NO_ERROR){
            LOGGER.error("GL Error: {}", error);
            error = GL11.glGetError();
        }
    }
    
    public static String getSaveOrServerName(){
        var player = Minecraft.getInstance().player;
        if(player == null) return "unknown";
        var serverData = player.connection.getServerData();
        var dataName = serverData == null ? "unkonwn" : serverData.name;
        var server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? dataName : server.getWorldData().getLevelName();
    }
}
