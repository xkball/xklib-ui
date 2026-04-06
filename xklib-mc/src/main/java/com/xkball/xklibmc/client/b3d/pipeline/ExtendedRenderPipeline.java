package com.xkball.xklibmc.client.b3d.pipeline;

import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;

import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.client.b3d.uniform.UpdatableUBO;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.stencil.StencilTest;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
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
    public final List<Pair<Integer, Supplier<GpuTextureView>>> multiTargetBindings;
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ExtendedRenderPipeline(Identifier location,
                                  Identifier vertexShader,
                                  Identifier fragmentShader,
                                  ShaderDefines shaderDefines,
                                  List<String> samplers,
                                  List<RenderPipeline.UniformDescription> uniforms,
                                  ColorTargetState colorTargetState,
                                  @Nullable DepthStencilState depthStencilState,
                                  PolygonMode polygonMode,
                                  boolean cull,
                                  VertexFormat vertexFormat,
                                  VertexFormat.Mode vertexFormatMode,
                                  int sortKey,
                                  Optional<StencilTest> stencilTest,
                                  Map<String, UpdatableUBO> UBOBindings,
                                  Map<String, Supplier<Pair<GpuTextureView, GpuSampler>>> samplerBindings,
                                  List<String> SSBOs,
                                  List<Pair<Integer, Supplier<GpuTextureView>>> multiTargetBindings) {
        super(location, vertexShader, fragmentShader, shaderDefines, samplers, uniforms, colorTargetState, depthStencilState, polygonMode, cull, vertexFormat, vertexFormatMode, sortKey, stencilTest);
        this.UBOBindings = UBOBindings;
        this.samplerBindings = samplerBindings;
        this.SSBOs = SSBOs;
        this.multiTargetBindings = multiTargetBindings;
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
    
    public static Builder extendedbuilder(ExtendedRenderPipeline renderPipeline) {
        var  builder = new Builder();
        builder.location = Optional.of(renderPipeline.getLocation());
        builder.fragmentShader = Optional.of(renderPipeline.getFragmentShader());
        builder.vertexShader = Optional.of(renderPipeline.getVertexShader());
        if (!renderPipeline.getShaderDefines().isEmpty()) {
            ShaderDefines.Builder defBuilder = ShaderDefines.builder();
            for (Map.Entry<String, String> entry : renderPipeline.getShaderDefines().values().entrySet()) {
                defBuilder.define(entry.getKey(), entry.getValue());
            }
            for (String flag : renderPipeline.getShaderDefines().flags()) {
                defBuilder.define(flag);
            }
            builder.definesBuilder = Optional.of(defBuilder);
        }
        if (!renderPipeline.getSamplers().isEmpty()) {
            builder.samplers = Optional.of(new ArrayList<>(renderPipeline.getSamplers()));
        }
        if (!renderPipeline.getUniforms().isEmpty()) {
            builder.uniforms = Optional.of(new ArrayList<>(renderPipeline.getUniforms()));
        }
        builder.depthStencilState = Optional.ofNullable(renderPipeline.getDepthStencilState());
        builder.polygonMode = Optional.of(renderPipeline.getPolygonMode());
        builder.cull = Optional.of(renderPipeline.isCull());
        builder.colorTargetState = Optional.of(renderPipeline.getColorTargetState());
        builder.vertexFormat = Optional.of(renderPipeline.getVertexFormat());
        builder.vertexFormatMode = Optional.of(renderPipeline.getVertexFormatMode());
        builder.stencilTest = renderPipeline.getStencilTest();
        builder.UBOBindings.putAll(renderPipeline.UBOBindings);
        builder.samplerBindings.putAll(renderPipeline.samplerBindings);
        builder.SSBOs.addAll(renderPipeline.SSBOs);
        builder.multiTargetBindings.addAll(renderPipeline.multiTargetBindings);
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
    
    public static class Builder extends RenderPipeline.Builder {
        
        private final Map<String, UpdatableUBO> UBOBindings = new HashMap<>();
        public final Map<String, Supplier<Pair<GpuTextureView, GpuSampler>>> samplerBindings = new HashMap<>();
        private final List<String> SSBOs = new ArrayList<>();
        private final List<Pair<Integer, Supplier<GpuTextureView>>> multiTargetBindings = new ArrayList<>();
        
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
        
        public Builder bindMultiTarget(int index, Supplier<GpuTextureView> texture){
            if(index < 1 || index > 31) throw new IllegalArgumentException("Invalid multi-target index: " + index + ", must between 1 and 31");
            this.multiTargetBindings.add(Pair.of(index, texture));
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
        public Builder withShaderDefine(String flag) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            
            this.definesBuilder.get().define(flag);
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
        public Builder withSampler(String sampler) {
            if (this.samplers.isEmpty()) {
                this.samplers = Optional.of(new ArrayList<>());
            }
            
            this.samplers.get().add(sampler);
            return this;
        }
        
        @Override
        public Builder withUniform(String uniform, UniformType type) {
            if (this.uniforms.isEmpty()) {
                this.uniforms = Optional.of(new ArrayList<>());
            }
            
            if (type == UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Cannot use texel buffer without specifying texture format");
            } else {
                this.uniforms.get().add(new UniformDescription(uniform, type));
                return this;
            }
        }
        
        @Override
        public Builder withUniform(String uniform, UniformType type, TextureFormat format) {
            if (this.uniforms.isEmpty()) {
                this.uniforms = Optional.of(new ArrayList<>());
            }
            
            if (type != UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Only texel buffer can specify texture format");
            } else {
                this.uniforms.get().add(new UniformDescription(uniform, format));
                return this;
            }
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
        public Builder withVertexFormat(VertexFormat vertexFormat, VertexFormat.Mode vertexFormatMode) {
            this.vertexFormat = Optional.of(vertexFormat);
            this.vertexFormatMode = Optional.of(vertexFormatMode);
            return this;
        }
        
        @Override
        public Builder withStencilTest(StencilTest stencilTest) {
            this.stencilTest = Optional.of(stencilTest);
            return this;
        }
        
        @Override
        public Builder withoutStencilTest(){
            this.stencilTest = Optional.empty();
            return this;
        }
        
        @Override
        public Builder withColorTargetState(ColorTargetState colorTargetState) {
            this.colorTargetState = Optional.of(colorTargetState);
            return this;
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
        public RenderPipeline build() {
            return this.buildExtended();
        }
        
        public ExtendedRenderPipeline buildExtended(){
            if (this.location.isEmpty()) {
                throw new IllegalStateException("Missing location");
            } else if (this.vertexShader.isEmpty()) {
                throw new IllegalStateException("Missing vertex shader");
            } else if (this.fragmentShader.isEmpty()) {
                throw new IllegalStateException("Missing fragment shader");
            } else if (this.vertexFormat.isEmpty()) {
                throw new IllegalStateException("Missing vertex buffer format");
            } else if (this.vertexFormatMode.isEmpty()) {
                throw new IllegalStateException("Missing vertex mode");
            } else {
                return new ExtendedRenderPipeline(
                        this.location.get(),
                        this.vertexShader.get(),
                        this.fragmentShader.get(),
                        this.definesBuilder.orElse(ShaderDefines.builder()).build(),
                        List.copyOf(this.samplers.orElse(new ArrayList<>())),
                        this.uniforms.orElse(Collections.emptyList()),
                        this.colorTargetState.orElse(ColorTargetState.DEFAULT),
                        this.depthStencilState.orElse(null),
                        this.polygonMode.orElse(PolygonMode.FILL),
                        this.cull.orElse(true),
                        this.vertexFormat.get(),
                        this.vertexFormatMode.get(),
                        nextPipelineSortKey++,
                        this.stencilTest,
                        this.UBOBindings,
                        this.samplerBindings,
                        this.SSBOs,
                        this.multiTargetBindings);
            }
        }
        
    }
    
}
