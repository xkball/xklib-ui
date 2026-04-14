package com.xkball.xklibmc.client.b3d;

import com.mojang.blaze3d.systems.RenderSystem;

import com.xkball.xklibmc.api.client.b3d.IEndFrameListener;
import com.xkball.xklibmc.api.client.b3d.IUpdatable;
import com.xkball.xklibmc.client.b3d.uniform.XKLibUniforms;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientResourceLoadFinishedEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppedEvent;
import org.lwjgl.opengl.GLCapabilities;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class ClientRenderObjects {
    
    public static boolean SUPPORT_NV_COMMAND_LIST = false;
    public static boolean SUPPORT_NV_SHADER_BUFFER_LOAD = false;

    public final List<AutoCloseable> closeOnExit = new ArrayList<>();
    public final List<IEndFrameListener> endFrame = new ArrayList<>();
    public final List<IUpdatable> everyFrame = new ArrayList<>();
    public final List<IUpdatable> reload = new ArrayList<>();
    
    public static ClientRenderObjects INSTANCE;
    
    public ClientRenderObjects() {
    
    }
    
    public static void init(GLCapabilities capabilities){
        INSTANCE = new ClientRenderObjects();
        SUPPORT_NV_COMMAND_LIST = capabilities.GL_NV_command_list;
        SUPPORT_NV_SHADER_BUFFER_LOAD = capabilities.GL_NV_shader_buffer_load;
    }
    
    public void addCloseOnExit(AutoCloseable obj) {
        RenderSystem.assertOnRenderThread();
        closeOnExit.add(obj);
        
    }
    
    public void addEndFrameListener(IEndFrameListener listener) {
        RenderSystem.assertOnRenderThread();
        endFrame.add(listener);
    }
    
    public void addEveryFrameListener(IUpdatable obj) {
        RenderSystem.assertOnRenderThread();
        everyFrame.add(obj);
    }
    
    public void addReloadListener(IUpdatable obj) {
        RenderSystem.assertOnRenderThread();
        reload.add(obj);
    }
    
    @SubscribeEvent
    public static void onGameExit(ClientStoppedEvent event) {
        try {
            for(var closeable : INSTANCE.closeOnExit) {
                closeable.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        INSTANCE.closeOnExit.clear();
    }
    
    @SubscribeEvent
    public static void afterReloadFinish(ClientResourceLoadFinishedEvent event){
        XKLibUniforms.init();
        //在此预先加载材质, 避免在renderPass里面加载导致爆炸
        for(var updatable : INSTANCE.reload) {
            updatable.update();
        }
    }
}
