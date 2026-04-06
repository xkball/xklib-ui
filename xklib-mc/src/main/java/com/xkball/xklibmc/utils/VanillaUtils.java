package com.xkball.xklibmc.utils;

import com.xkball.xklibmc.XKLibMC;
import com.xkball.xklib.resource.ResourceLocation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class VanillaUtils {
    
    public static final Identifier MISSING_TEXTURE = Identifier.withDefaultNamespace("missingno");
    
    public static Identifier convertRL(ResourceLocation rl){
        return Identifier.fromNamespaceAndPath(rl.namespace(),rl.path());
    }
    
    public static ResourceLocation convertId(Identifier id){
        return new ResourceLocation(id.getNamespace(),id.getPath());
    }
    
    public static Identifier modRL(String path) {
        return resourceLocationOf(XKLibMC.MODID, path);
    }
    
    public static Identifier resourceLocationOf(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
    
    public static <T> ResourceKey<T> modResourceKey(ResourceKey< ? extends Registry<T>> key, String value){
        return ResourceKey.create(key,modRL(value));
    }
    
    public static EquipmentSlot equipmentSlotFromHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
    
    public static void runCommand(String command, LivingEntity livingEntity) {
        if(!(livingEntity instanceof ServerPlayer sp)) return;
        // Raise permission level to 2, akin to what vanilla sign does
        
        CommandSourceStack cmdSrc = sp.createCommandSourceStack().withPermission(LevelBasedPermissionSet.GAMEMASTER);
        var server = livingEntity.level().getServer();
        if (server != null) {
            server.getCommands().performPrefixedCommand(cmdSrc, command);
        }
    }
    
    public static void runCommand(String command, MinecraftServer server, UUID playerUUID){
        var player = server.getPlayerList().getPlayer(playerUUID);
        if(player != null){
            server.getCommands().performPrefixedCommand(player.createCommandSourceStack().withPermission(LevelBasedPermissionSet.GAMEMASTER),command);
        }
    }
    
    //irrelevant vanilla(笑)
    public static int getColor(int r,int g,int b,int a){
        return a << 24 | r << 16 | g << 8 | b;
    }
    
    public static int parseColorHEX(String color) throws IllegalArgumentException {
        if(color.length() == 6){
            return getColor(
                    Integer.parseInt(color.substring(0,2),16),
                    Integer.parseInt(color.substring(2,4),16),
                    Integer.parseInt(color.substring(4,6),16),
                    255);
        }
        if(color.length() == 8){
            return getColor(
                    Integer.parseInt(color.substring(0,2),16),
                    Integer.parseInt(color.substring(2,4),16),
                    Integer.parseInt(color.substring(4,6),16),
                    Integer.parseInt(color.substring(6,8),16)
            );
        }
        throw new IllegalArgumentException("Format of color must be RGB or RGBA digits");
    }
    
    public static String hexColorFromInt(int color){
        var a = color >>> 24;
        var r = (color >> 16) & 0xFF;
        var g = (color >> 8) & 0xFF;
        var b = color & 0xFF;
        return String.format("%02X%02X%02X%02X", r, g, b, a).toUpperCase();
    }
}
