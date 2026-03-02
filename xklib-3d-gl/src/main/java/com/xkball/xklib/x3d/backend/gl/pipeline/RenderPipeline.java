package com.xkball.xklib.x3d.backend.gl.pipeline;

import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.IShaderProgram;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.x3d.backend.gl.GLStateManager;
import com.xkball.xklib.x3d.backend.gl.buffer.IBOBuffer;
import com.xkball.xklib.x3d.backend.gl.buffer.VAOBuffer;
import com.xkball.xklib.x3d.backend.gl.buffer.VBOBuffer;
import com.xkball.xklib.x3d.backend.gl.shader.ShaderProgram;
import com.xkball.xklib.x3d.backend.gl.shader.Uniform;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import com.xkball.xklib.utils.Pair;
import org.lwjgl.opengl.GL45;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class RenderPipeline implements IRenderPipeline {
    public final ResourceLocation location;
    public final ResourceLocation vertexShader;
    public final ResourceLocation fragmentShader;
    public final List<Pair<String, Supplier<ITexture>>> samplers;
    public final List<Pair<String, Consumer<Uniform>>> uniforms;
    public final VertexFormat format;
    public final VertexFormat.Mode mode;
    public final boolean depthTest;
    public final boolean depthMask;
    public final boolean cullFace;
    public final Optional<BlendFunction> blendFunction;
    
    private boolean init;
    private ShaderProgram shader;
    
    private RenderPipeline(ResourceLocation location, ResourceLocation vertexShader, ResourceLocation fragmentShader, List<Pair<String, Supplier<ITexture>>> samplers, List<Pair<String, Consumer<Uniform>>> uniforms, VertexFormat format, VertexFormat.Mode mode, boolean depthTest, boolean depthMask, boolean cullFace, Optional<BlendFunction> blendFunction) {
        this.location = location;
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.samplers = samplers;
        this.uniforms = uniforms;
        this.format = format;
        this.mode = mode;
        this.depthTest = depthTest;
        this.depthMask = depthMask;
        this.cullFace = cullFace;
        this.blendFunction = blendFunction;
    }
    
    public void init() {
        if (init) {
            return;
        }
        shader = new ShaderProgram(vertexShader, fragmentShader);
        init = true;
    }
    
    public void draw(VBOBuffer vbo){
        vbo.bind();
        this.apply();
        var count = vbo.getSize()/this.format.getVertexSize();
        var iboHolder = IBOBuffer.getSequentialBuffer(this.mode);
        count = (int) (count * ((float)iboHolder.indexStride/(float) iboHolder.vertexStride));
        var ibo = iboHolder.getBuffer(count);
        ibo.bind();
        GL45.glDrawElements(this.mode.toGl(), count, iboHolder.type().toGl(), 0);
        IBOBuffer.unbind();
        VBOBuffer.unbind();
        VAOBuffer.unbind();
    }
    
    @Override
    public void draw(BufferBuilder builder){
        //FIXME
//        this.format.uploadImmediateVertexBuffer(builder.build());
        builder.free();
        this.draw();
    }
    
    public void draw(){
        //FIXME
//        this.draw(this.format.getImmediateDrawVertexBuffer());
    }
    
    public void apply() {
        init();
        
        GLStateManager.useProgram(shader.getProgramId());
        
        if (depthTest) {
            GLStateManager.enableDepthTest();
        } else {
            GLStateManager.disableDepthTest();
        }
        
        GLStateManager.depthMask(depthMask);
        
        if (cullFace) {
            GLStateManager.enableCullFace();
        } else {
            GLStateManager.disableCullFace();
        }
        
        if (blendFunction.isPresent()) {
            GLStateManager.enableBlend();
            GLStateManager.setBlendFunction(blendFunction.get());
        } else {
            GLStateManager.disableBlend();
        }
        
        for (int i = 0; i < samplers.size(); i++) {
            var sampler = samplers.get(i);
            ITexture texture = sampler.getSecond().get();
            GLStateManager.activeTexture(GL_TEXTURE0 + i);
            GLStateManager.bindTexture(GL_TEXTURE_2D, texture.getId());
            shader.getUniform(sampler.getFirst()).set(i);
        }
        
        for (var uniform : uniforms) {
            Uniform u = shader.getUniform(uniform.getFirst());
            if (u != null) {
                uniform.getSecond().accept(u);
            }
        }
        
        shader.uploadUniforms();
        //FIXME
//        format.getFormatVertexArrayBuffer().bind();
    }
    
    @Override
    public void bindSampler(int binding, Supplier<ITexture> texture){
        this.samplers.set(binding, Pair.of(samplers.get(binding).getFirst(), texture));
    }
    
    public void destroy() {
        if (shader != null) {
            shader.destroy();
            shader = null;
        }
        init = false;
    }
    
    public ShaderProgram getShader() {
        this.init();
        return shader;
    }
    
    public static Builder builder(ResourceLocation location) {
        return new Builder(location);
    }
    
    @Override
    public ResourceLocation location() {
        return location;
    }
    
    @Override
    public VertexFormat.Mode mode() {
        return mode;
    }
    
    @Override
    public VertexFormat format() {
        return format;
    }
    
    @Override
    public IShaderProgram shader() {
        this.init();
        return shader;
    }
    
    public static class Builder {
        private final ResourceLocation location;
        private ResourceLocation vertexShader;
        private ResourceLocation fragmentShader;
        private final List<Pair<String, Supplier<ITexture>>> samplers;
        private final List<Pair<String, Consumer<Uniform>>> uniforms;
        private VertexFormat format;
        private VertexFormat.Mode mode;
        private boolean depthTest;
        private boolean depthMask = true;
        private boolean cullFace;
        private Optional<BlendFunction> blendFunction;
        
        private Builder(ResourceLocation location) {
            this.location = location;
            this.samplers = new ArrayList<>();
            this.uniforms = new ArrayList<>();
            this.depthTest = false;
            this.cullFace = false;
            this.blendFunction = Optional.empty();
            this.mode = VertexFormat.Mode.TRIANGLES;
        }
        
        public Builder vertexShader(ResourceLocation vertexShader) {
            this.vertexShader = vertexShader;
            return this;
        }
        
        public Builder fragmentShader(ResourceLocation fragmentShader) {
            this.fragmentShader = fragmentShader;
            return this;
        }
        
        public Builder sampler(String name, Supplier<ITexture> textureSupplier) {
            this.samplers.add(Pair.of(name, textureSupplier));
            return this;
        }
        
        public Builder uniform(String name, Consumer<Uniform> uniformSetter) {
            this.uniforms.add(Pair.of(name, uniformSetter));
            return this;
        }
        
        public Builder format(VertexFormat.Mode mode,VertexFormat format) {
            this.format = format;
            this.mode = mode;
            return this;
        }
        
        public Builder depthTest(boolean depthTest) {
            this.depthTest = depthTest;
            return this;
        }
        
        public Builder depthMask(boolean depthMask) {
            this.depthMask = depthMask;
            return this;
        }
        
        public Builder cullFace(boolean cullFace) {
            this.cullFace = cullFace;
            return this;
        }
        
        public Builder blendFunction(BlendFunction blendFunction) {
            this.blendFunction = Optional.ofNullable(blendFunction);
            return this;
        }
        
        public Builder noBlend() {
            this.blendFunction = Optional.empty();
            return this;
        }
        
        public RenderPipeline build() {
            if (vertexShader == null) {
                throw new IllegalStateException("Vertex shader must be set");
            }
            if (fragmentShader == null) {
                throw new IllegalStateException("Fragment shader must be set");
            }
            if (format == null) {
                throw new IllegalStateException("Vertex format must be set");
            }
            
            return new RenderPipeline(
                location,
                vertexShader,
                fragmentShader,
                new ArrayList<>(samplers),
                new ArrayList<>(uniforms),
                format,
                mode,
                depthTest,
                depthMask,
                cullFace,
                blendFunction
            );
        }
    }
}
