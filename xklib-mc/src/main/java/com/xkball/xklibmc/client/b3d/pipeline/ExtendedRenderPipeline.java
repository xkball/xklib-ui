package com.xkball.xklibmc.client.b3d.pipeline;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;

import com.xkball.xklib.utils.Lazy;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.client.b3d.uniform.UpdatableUBO;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.stencil.StencilTest;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@NonNullByDefault
public class ExtendedRenderPipeline extends RenderPipeline {
    
    public final Map<String, UpdatableUBO> UBOBindings;
    public final Map<String, Supplier<Pair<GpuTextureView, GpuSampler>>> samplerBindings;
    public final List<String> SSBOs;
    public final Lazy<RenderType> asRenderType = Lazy.of(this::toRenderType);
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ExtendedRenderPipeline(
                            Identifier location,
                            Identifier vertexShader,
                            Identifier fragmentShader,
                            ShaderDefines shaderDefines,
                            List<BindGroupLayout> bindGroupLayouts,
                            @Nullable ColorTargetState[] colorTargetStates,
                            @Nullable DepthStencilState depthStencilState,
                            PolygonMode polygonMode,
                            boolean cull,
                            @Nullable VertexFormat[] vertexFormatPerBuffer,
                            PrimitiveTopology primitiveTopology,
                            Optional<StencilTest> stencilTest,
                            int sortKey,
                            Map<String, UpdatableUBO> UBOBindings,
                            Map<String, Supplier<Pair<GpuTextureView, GpuSampler>>> samplerBindings,
                            List<String> SSBOs,
                            List<Pair<Integer, Supplier<GpuTextureView>>> multiTargetBindings) {
        super(location, vertexShader, fragmentShader, shaderDefines, bindGroupLayouts, colorTargetStates, depthStencilState, polygonMode, cull, vertexFormatPerBuffer, primitiveTopology, stencilTest, sortKey);
        this.UBOBindings = UBOBindings;
        this.samplerBindings = samplerBindings;
        this.SSBOs = SSBOs;
    }
    
    public void apply(RenderPassBackend renderPass) {
        for(var entry : UBOBindings.entrySet()) {
            renderPass.setUniform(entry.getKey(),entry.getValue().getBuffer());
        }
        for(var entry : samplerBindings.entrySet()) {
            var texture = entry.getValue().get().getFirst();
            renderPass.bindTexture(entry.getKey(), texture, entry.getValue().get().getSecond());
        }
    }
    
    public static Builder extendedbuilder(Snippet... snippets) {
        var  builder = new Builder();
        
        for (Snippet renderpipeline$snippet : snippets) {
            builder.withSnippet(renderpipeline$snippet);
        }
        
        return builder;
    }
    
    public Builder extendedbuilder() {
        var builder = new Builder();
        builder.location = Optional.of(this.getLocation());
        builder.fragmentShader = Optional.of(this.getFragmentShader());
        builder.vertexShader = Optional.of(this.getVertexShader());
        if (!this.getShaderDefines().isEmpty()) {
            ShaderDefines.Builder defBuilder = ShaderDefines.builder();
            for (Map.Entry<String, String> entry : this.getShaderDefines().values().entrySet()) {
                defBuilder.define(entry.getKey(), entry.getValue());
            }
            for (String flag : this.getShaderDefines().flags()) {
                defBuilder.define(flag);
            }
            builder.definesBuilder = Optional.of(defBuilder);
        }
        if (!this.getBindGroupLayouts().isEmpty()) {
            builder.bindGroupLayouts = Optional.of(new ArrayList<>(this.getBindGroupLayouts()));
        }
        builder.depthStencilState = Optional.ofNullable(this.getDepthStencilState());
        builder.polygonMode = Optional.of(this.getPolygonMode());
        builder.cull = Optional.of(this.isCull());
        System.arraycopy(this.getColorTargetStates(), 0, builder.colorTargetStates, 0, this.getColorTargetStates().length);
        System.arraycopy(this.getVertexFormatBindings(), 0, builder.vertexFormatPerBuffer, 0, this.getVertexFormatBindings().length);
        builder.primitiveTopology = Optional.of(this.getPrimitiveTopology());
        builder.stencilTest = this.getStencilTest();
        builder.UBOBindings.putAll(this.UBOBindings);
        builder.samplerBindings.putAll(this.samplerBindings);
        builder.SSBOs.addAll(this.SSBOs);
        return builder;
    }
    
    public static boolean haveSSBO(RenderPipeline pipeline){
        return pipeline instanceof ExtendedRenderPipeline epp && !epp.SSBOs.isEmpty();
    }
    
    public static Builder builder(RenderPipeline.Snippet... snippets) {
        Builder builder = new Builder();
        for (RenderPipeline.Snippet snippet : snippets) {
            builder.withSnippet(snippet);
        }
        return builder;
    }
    
    private RenderType toRenderType(){
        return RenderType.create(this.getLocation().toString(),RenderSetup.builder(this).createRenderSetup());
    }
    
    public RenderType asRenderType(){
        return this.asRenderType.get();
    }
    
    public static class Builder extends RenderPipeline.Builder {
        
        private final Map<String, UpdatableUBO> UBOBindings = new HashMap<>();
        public final Map<String, Supplier<Pair<GpuTextureView, GpuSampler>>> samplerBindings = new HashMap<>();
        private final List<String> SSBOs = new ArrayList<>();
        
        public Builder(){
            super();
        }
        
        public Builder bindSampler(String sampler, Supplier<Pair<GpuTextureView, GpuSampler>> texture){
            this.samplerBindings.put(sampler, texture);
            return this;
        }
        
        public Builder bindUniform(String uniform, UpdatableUBO ubo){
            this.UBOBindings.put(uniform, ubo);
            return this;
        }
        
        public Builder withSSBO(String name){
            this.SSBOs.add(name);
            return this;
        }
        
        @Override
        public Builder withLocation(String location) {
            this.location = Optional.of(Identifier.withDefaultNamespace(location));
            return this;
        }
        
        @Override
        public Builder withLocation(Identifier location) {
            this.location = Optional.of(location);
            return this;
        }
        
        @Override
        public Builder withFragmentShader(String fragmentShader) {
            this.fragmentShader = Optional.of(Identifier.withDefaultNamespace(fragmentShader));
            return this;
        }
        
        @Override
        public Builder withFragmentShader(Identifier fragmentShader) {
            this.fragmentShader = Optional.of(fragmentShader);
            return this;
        }
        
        @Override
        public Builder withVertexShader(String vertexShader) {
            this.vertexShader = Optional.of(Identifier.withDefaultNamespace(vertexShader));
            return this;
        }
        
        @Override
        public Builder withVertexShader(Identifier vertexShader) {
            this.vertexShader = Optional.of(vertexShader);
            return this;
        }
        
        @Override
        public Builder withShaderDefine(String key) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            
            this.definesBuilder.get().define(key);
            return this;
        }
        
        @Override
        public Builder withShaderDefine(String key, int value) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            
            this.definesBuilder.get().define(key, value);
            return this;
        }
        
        @Override
        public Builder withShaderDefine(String key, float value) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            
            this.definesBuilder.get().define(key, value);
            return this;
        }
        
        @Override
        public Builder withBindGroupLayout(BindGroupLayout bindGroupLayout) {
            if (this.bindGroupLayouts.isEmpty()) {
                this.bindGroupLayouts = Optional.of(new ArrayList<>());
            }
            
            this.bindGroupLayouts.get().add(bindGroupLayout);
            return this;
        }
        
        @Override
        public Builder withPolygonMode(PolygonMode polygonMode) {
            this.polygonMode = Optional.of(polygonMode);
            return this;
        }
        
        @Override
        public Builder withCull(boolean cull) {
            this.cull = Optional.of(cull);
            return this;
        }
        
        @Override
        public Builder withColorTargetState(int index, ColorTargetState colorTargetState) {
            this.colorTargetStates[index] = colorTargetState;
            this.activeColorTargetStateCount = Math.max(this.activeColorTargetStateCount, index + 1);
            return this;
        }
        
        @Override
        public Builder withUnusedColorTargetState(int index) {
            this.colorTargetStates[index] = null;
            this.activeColorTargetStateCount = Math.max(this.activeColorTargetStateCount, index + 1);
            return this;
        }
        
        @Override
        public Builder withColorTargetState(ColorTargetState colorTargetState) {
            return this.withColorTargetState(0, colorTargetState);
        }
        
        @Override
        public Builder withDepthStencilState(DepthStencilState depthStencilState) {
            this.depthStencilState = Optional.of(depthStencilState);
            return this;
        }
        
        @Override
        public Builder withDepthStencilState(Optional<DepthStencilState> depthStencilState) {
            this.depthStencilState = depthStencilState;
            return this;
        }
        
        @Override
        public Builder withVertexBinding(int bindingIndex, VertexFormat vertexFormat) {
            this.vertexFormatPerBuffer[bindingIndex] = vertexFormat;
            return this;
        }
        
        @Override
        public Builder withPrimitiveTopology(PrimitiveTopology primitiveTopology) {
            this.primitiveTopology = Optional.of(primitiveTopology);
            return this;
        }
        
        @Override
        public Builder withStencilTest(net.neoforged.neoforge.client.stencil.StencilTest stencilTest) {
            this.stencilTest = Optional.of(stencilTest);
            return this;
        }
        
        @Override
        public Builder withoutStencilTest(){
            this.stencilTest = Optional.empty();
            return this;
        }
        
        @Override
        public RenderPipeline build() {
            return this.buildExtended();
        }
        
        public ExtendedRenderPipeline buildExtended(){
                        if (this.location.isEmpty()) {
                throw new IllegalStateException("Missing location");
            }

            if (this.vertexShader.isEmpty()) {
                throw new IllegalStateException("Missing vertex shader");
            }

            if (this.fragmentShader.isEmpty()) {
                throw new IllegalStateException("Missing fragment shader");
            }

            if (this.primitiveTopology.isEmpty()) {
                throw new IllegalStateException("Missing primitive topology");
            }

            ColorTargetState[] activeColorTargetStates;
            if (this.activeColorTargetStateCount == 0) {
                activeColorTargetStates = new ColorTargetState[]{ColorTargetState.DEFAULT};
            } else {
                activeColorTargetStates = Arrays.copyOf(this.colorTargetStates, this.activeColorTargetStateCount);
                Optional<BlendFunction> lastBlend = Optional.empty();

                for (ColorTargetState activeColorTargetState : activeColorTargetStates) {
                    if (activeColorTargetState != null) {
                        Optional<BlendFunction> currentBlend = activeColorTargetState.blendFunction();
                        if (currentBlend.isPresent()) {
                            if (lastBlend.isEmpty()) {
                                lastBlend = currentBlend;
                            } else if (!currentBlend.equals(lastBlend)) {
                                throw new IllegalStateException("Blend functions must currently be the same for all color targets");
                            }
                        }
                    }
                }
            }

            int boundVertexAttribCount = 0;

            for (VertexFormat bindings : this.vertexFormatPerBuffer) {
                if (bindings != null) {
                    boundVertexAttribCount += bindings.getElements().size();
                }
            }

            if (boundVertexAttribCount > 16) {
                throw new IllegalStateException("Binding more than 16 vertex attributes is not supported");
            } else {
                return new ExtendedRenderPipeline(
                    this.location.get(),
                    this.vertexShader.get(),
                    this.fragmentShader.get(),
                    this.definesBuilder.orElse(ShaderDefines.builder()).build(),
                    List.copyOf(this.bindGroupLayouts.orElse(new ArrayList<>())),
                    activeColorTargetStates,
                    this.depthStencilState.orElse(null),
                    this.polygonMode.orElse(PolygonMode.FILL),
                    this.cull.orElse(true),
                    this.vertexFormatPerBuffer,
                    this.primitiveTopology.get(),
                    this.stencilTest,
                    nextPipelineSortKey++,
                        this.UBOBindings,
                        this.samplerBindings,
                        this.SSBOs
                );
            }
        }
    }
    
}
