package com.xkball.xklibmc.client.b3d.postprocess;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.xklibmc.api.client.b3d.SamplerCacheCache;
import com.xkball.xklibmc.client.b3d.mesh.CachedMesh;
import com.xkball.xklibmc.client.b3d.uniform.XKLibUniforms;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.GLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiConsumer;

public class PostProcess {
    
    protected int xSize;
    protected int ySize;
    
    protected final String name;
    protected final ProjectionMatrixBuffer projMatCache = new ProjectionMatrixBuffer("post process");
    protected CachedMesh cachedMesh;
    
    private final Map<String, RenderTarget> renderTarget = new HashMap<>();
    private final List<DrawData> drawData = new ArrayList<>();
    private boolean swapBack = false;
    private GpuTextureView inputTexture;
    private GpuTextureView inputDepth;
    private GpuTextureView outputTexture;
    private GpuTextureView outputDepth;
    
    public PostProcess(String name) {
        this.name = name;
        this.xSize = Minecraft.getInstance().getWindow().getWidth();
        this.ySize = Minecraft.getInstance().getWindow().getHeight();
        this.cachedMesh = createScreenQuad(xSize, ySize);
    }
    
    public CachedMesh createScreenQuad(int xSize, int ySize){
        return new CachedMesh("screen_blit", VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION, b -> {
            b.addVertex(0.0f, 0.0f, 500.0f);
            b.addVertex(xSize, 0.0f, 500.0f);
            b.addVertex(xSize,  ySize, 500.0f);
            b.addVertex(0.0f, ySize, 500.0f);
        });
    }
    
    public void drawcall(RenderPass renderpass){
        renderpass.drawIndexed(0,0, cachedMesh.getIndexCount(), 1);
    }
    
    public void resize(int xSize, int ySize){
        this.xSize = xSize;
        this.ySize = ySize;
        for(var entry : renderTarget.entrySet()){
            entry.getValue().resize(xSize, ySize);
        }
        this.cachedMesh.close();
        this.cachedMesh = createScreenQuad(xSize, ySize);
    }
    
    public void apply(GpuTextureView color, GpuTextureView depth){
        this.apply(color,depth,color,null);
    }
    
    public void apply(GpuTextureView color, GpuTextureView depth,GpuTextureView target,@Nullable GpuTextureView targetDepth){
        var w = color.getWidth(0);
        var h = color.getHeight(0);
        if(w != this.xSize || h != this.ySize){
                this.resize(w, h);
        }
        boolean override =w != Minecraft.getInstance().getMainRenderTarget().width || h != Minecraft.getInstance().getMainRenderTarget().height;
        if(override){
            XKLibUniforms.SCREEN_SIZE.startOverride(
                    b -> b.putVec2(w,h)
            );
        }
        this.inputTexture = color;
        this.inputDepth = depth;
        this.outputTexture = target;
        this.outputDepth = targetDepth;
        for (DrawData drawDatum : drawData) {
            applyOnce(drawDatum);
        }
        if(this.swapBack){
            var dest = getTextureView("output",false);
            var src = getTextureView("swap", false);
            GLUtils.copyFrameBufferColorTo(Objects.requireNonNull(src), Objects.requireNonNull(dest));
        }
        this.inputTexture = null;
        this.inputDepth = null;
        this.outputTexture = null;
        this.outputDepth = null;
        if(override){
            XKLibUniforms.SCREEN_SIZE.endOverride();
        }
    }
    
    public void applyOnce(DrawData drawData){
        RenderSystem.backupProjectionMatrix();
        var projMat = new Matrix4f().setOrtho(0,xSize,0,ySize,0.1f,1000f,false);
        RenderSystem.setProjectionMatrix(projMatCache.getBuffer(projMat), ProjectionType.ORTHOGRAPHIC);
        if(drawData.uniformSetup != null) drawData.uniformSetup.run();
        try(var renderpass = ClientUtils.getCommandEncoder().createRenderPass(() -> name + " processing",
                Objects.requireNonNull(getTextureView(drawData.to, false)), OptionalInt.of(0),
                getTextureView(drawData.to, true), OptionalDouble.empty())){
            RenderSystem.bindDefaultUniforms(renderpass);
            for(var ts : drawData.textureSetups){
                renderpass.bindTexture(ts.target,getTextureView(ts.target, ts.depth), ts.sampler);
            }
            renderpass.setPipeline(drawData.pipeline);
            renderpass.setVertexBuffer(0, cachedMesh.getVertexBuffer());
            renderpass.setIndexBuffer(cachedMesh.getIndexBuffer(),cachedMesh.getIndexType());
            drawData.drawFunc.accept(this,renderpass);
        }
        RenderSystem.restoreProjectionMatrix();
    }
    
    public @Nullable GpuTextureView getTextureView(String name, boolean depth){
        if("input".equals(name)){
            return depth ? this.inputDepth : this.inputTexture;
        }
        if("output".equals(name)){
            return depth ? this.outputDepth : this.outputTexture;
        }
        var r = this.renderTarget.get(name);
        return depth ? r.getDepthTextureView() : r.getColorTextureView();
    }
    
    public static Builder builder(){
        return new Builder();
    }
    
    public record DrawData(RenderPipeline pipeline, String to,Runnable uniformSetup, List<TextureSetup> textureSetups, BiConsumer<PostProcess, RenderPass> drawFunc){
    
    }
    
    public record TextureSetup(String target, boolean depth, GpuSampler sampler){}
    
    public static class Builder{
        
        private final Map<String, Boolean> regRenderTargets = new HashMap<>();
        private Runnable uniformSetup = null;
        private final List<TextureSetup> textureSetups = new ArrayList<>();
        private final List<DrawData> drawData = new ArrayList<>();
        private boolean swapBack = false;
        
        public Builder(){
            this.regRenderTargets.put("swap",false);
        }
        
        public Builder regRenderTarget(String name, boolean useDepth){
            this.regRenderTargets.put(name, useDepth);
            return this;
        }
        
        public Builder setupUniform(Runnable setup){
            this.uniformSetup  = setup;
            return this;
        }
        
        public Builder withTexture(String target, boolean useDepth, GpuSampler sampler){
            this.checkRenderTargetExist(target);
            this.textureSetups.add(new TextureSetup(target, useDepth, sampler));
            return this;
        }
        
        public Builder applyOnce(RenderPipeline pipeline, String to, BiConsumer<PostProcess, RenderPass> drawFunc){
            this.checkRenderTargetExist(to);
            this.drawData.add(new DrawData(pipeline, to, uniformSetup, new ArrayList<>(textureSetups), drawFunc));
            this.uniformSetup = null;
            this.textureSetups.clear();
            return this;
        }
        
        public Builder swapBack(){
            this.swapBack = true;
            return this;
        }
        
        public PostProcess build(String name){
            var result = new PostProcess(name);
            for(var entry : regRenderTargets.entrySet()){
                result.renderTarget.put(entry.getKey(), new TextureTarget(entry.getKey(),result.xSize, result.ySize, entry.getValue()));
            }
            result.drawData.addAll(this.drawData);
            result.swapBack = this.swapBack;
            return result;
        }
        
        private void checkRenderTargetExist(String name){
            if("output".equals(name) || "input".equals(name)) return;
            if(!regRenderTargets.containsKey(name)){
                throw new IllegalArgumentException("Render target " + name + " is unknown.");
            }
        }
    }
}
