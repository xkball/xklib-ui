package com.xkball.xklibmc.client.b3d.mesh;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.api.client.b3d.ICloseOnExit;
import com.xkball.xklibmc.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;

@NonNullByDefault
public class CachedMesh implements ICloseOnExit<CachedMesh> {
    
    public final String name;
    public final PrimitiveTopology mode;
    public final VertexFormat format;
    public final Consumer<BufferBuilder> initFunc;
    
    private volatile int indexCount = -1;
    private boolean sequentialIndexBuffer = false;
    @Nullable
    private IndexType indexType;
    
    @Nullable
    private GpuBuffer vertexBuffer;
    
    @Nullable
    private GpuBuffer indexBuffer;
    
    public CachedMesh(String name, RenderPipeline pipeline, Consumer<BufferBuilder> initFunc, boolean immediate ){
        this(name, pipeline.getPrimitiveTopology(), Objects.requireNonNull(pipeline.getVertexFormatBinding(0)), initFunc);
        if(immediate){
            this.init();
        }
    }
    
    public CachedMesh(String name, PrimitiveTopology mode, VertexFormat format, Consumer<BufferBuilder> initFunc) {
        this.name = name;
        this.mode = mode;
        this.format = format;
        this.initFunc = initFunc;
    }
    
    public void checkInit(){
        if(indexCount == -1){
            synchronized (this){
                if(indexCount == -1){
                    init();
                }
            }
        }
    }
    
    private void init(){
        try(var byteBufferBuilder = new ByteBufferBuilder(1024, 1024 * 1024 * 1024)) {
            var bufferBuilder = new BufferBuilder(byteBufferBuilder, mode, format);
            this.initFunc.accept(bufferBuilder);
            var mesh = bufferBuilder.buildOrThrow();
            this.vertexBuffer = ClientUtils.getGpuDevice().createBuffer(() -> name + "mesh vertex buffer",GpuBuffer.USAGE_VERTEX ,mesh.vertexBuffer());
            var state = mesh.drawState();
            this.indexCount = state.indexCount();
            this.indexType = state.indexType();
            if(mesh.indexBuffer() == null){
                this.sequentialIndexBuffer = true;
            }
            else {
                this.indexBuffer = ClientUtils.getGpuDevice().createBuffer(() -> name + "mesh index buffer", GpuBuffer.USAGE_INDEX , Objects.requireNonNull(mesh.indexBuffer()));
            }
            mesh.close();
        }
    }
    
    public void render(RenderPipeline pipeline, PoseStack poseStack) {
        var colorTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTextureView();
        var depthTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget().getDepthTextureView();
        if(colorTarget == null || depthTarget == null) return;
        
        var modelView = RenderSystem.getModelViewStack().mul(poseStack.last().pose(), new Matrix4f());
        var transformUBO = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(1,1,1,1), new Vector3f(), new Matrix4f());
        try (var renderpass = ClientUtils.getCommandEncoder()
                .createRenderPass(() -> name + " mesh rendering",colorTarget, Optional.empty(), depthTarget, OptionalDouble.empty())){
            RenderSystem.bindDefaultUniforms(renderpass);
            renderpass.setPipeline(pipeline);
            renderpass.setUniform("DynamicTransforms", transformUBO);
            renderpass.setVertexBuffer(0, this.getVertexBuffer().slice());
            renderpass.setIndexBuffer(this.getIndexBuffer(),this.getIndexType());
            renderpass.drawIndexed(indexCount, 1, 0, 0, 0);
        }
    }
    
    public MeshBundle<RenderPipeline> toMeshBundle(RenderPipeline pipeline){
        return new MeshBundleWithRenderPipeline(name, pipeline, List.of(new MeshBundle.MeshBlock(p -> {},InstanceInfo.EMPTY,this)));
    }
    
    public MeshBundle<RenderType> toMeshBundle(RenderType renderType){
        return new MeshBundleWithRenderType(name, renderType, List.of(new MeshBundle.MeshBlock(p -> {},InstanceInfo.EMPTY,this)));
    }
    
    public GpuBuffer getVertexBuffer(){
        checkInit();
        //noinspection DataFlowIssue
        return vertexBuffer;
    }
    
    public GpuBuffer getIndexBuffer(){
        checkInit();
        if(sequentialIndexBuffer){
            return RenderSystem.getSequentialBuffer(mode).getBuffer(indexCount);
        }
        //noinspection DataFlowIssue
        return indexBuffer;
    }
    
    public IndexType getIndexType(){
        checkInit();
        if(sequentialIndexBuffer){
            return RenderSystem.getSequentialBuffer(mode).type();
        }
        //noinspection DataFlowIssue
        return indexType;
    }
    
    public int getIndexCount(){
        checkInit();
        return indexCount;
    }
    
    @Override
    public void close() {
        if(vertexBuffer != null){
            vertexBuffer.close();
        }
        if(indexBuffer != null){
            indexBuffer.close();
        }
    }
}
