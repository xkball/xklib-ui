package com.xkball.xklib.ui.backend.gl.shader;

import com.google.common.collect.Maps;
import com.xkball.xklib.api.resource.IResource;
import com.xkball.xklib.resource.ClasspathResourceManager;
import com.xkball.xklib.resource.ResourceLocation;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL20C.*;

public final class ShaderProgram {

    private final ShaderModule vertex;
    private final ShaderModule fragment;

    private int programId;
    private boolean destroyed;
    private final Map<String, Uniform> uniformMap = Maps.newHashMap();

    public ShaderProgram(ResourceLocation vertexShader, ResourceLocation fragmentShader) {
        this(vertexShader, fragmentShader, new ClasspathResourceManager());
    }

    public ShaderProgram(ResourceLocation vertexShader, ResourceLocation fragmentShader, ClasspathResourceManager resourceManager) {
        Objects.requireNonNull(vertexShader, "vertexShader");
        Objects.requireNonNull(fragmentShader, "fragmentShader");
        Objects.requireNonNull(resourceManager, "resourceManager");

        this.vertex = new ShaderModule(vertexShader, GL_VERTEX_SHADER);
        this.fragment = new ShaderModule(fragmentShader, GL_FRAGMENT_SHADER);

        String vertexSource = readText(resourceManager.getResource(vertexShader));
        String fragmentSource = readText(resourceManager.getResource(fragmentShader));

        int vertexId = compileShader(this.vertex, vertexSource);
        int fragmentId = compileShader(this.fragment, fragmentSource);

        this.programId = linkProgram(vertexId, fragmentId, vertexShader, fragmentShader);
        this.buildUniformMap();
    }
    
    public Uniform getUniform(String name){
        return uniformMap.get(name);
    }
    
    public Uniform getOrCreateUniform(String name, int type, int count) {
        return uniformMap.computeIfAbsent(name, k -> {
            Uniform uniform = new Uniform(name, type, count, this);
            int location = glGetUniformLocation(programId, name);
            uniform.setLocation(location);
            return uniform;
        });
    }
    
    public Uniform getOrCreateUniform(String name, int type) {
        return getOrCreateUniform(name, type, getDefaultCount(type));
    }
    
    private void buildUniformMap() {
        int activeUniforms = glGetProgrami(programId, GL_ACTIVE_UNIFORMS);
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer sizeBuffer = stack.mallocInt(1);
            IntBuffer typeBuffer = stack.mallocInt(1);
            
            for (int i = 0; i < activeUniforms; i++) {
                String name = glGetActiveUniform(programId, i, sizeBuffer, typeBuffer);
                int location = glGetUniformLocation(programId, name);
                if (location < 0) {
                    continue;
                }
                
                int glType = typeBuffer.get(0);
                int arraySize = sizeBuffer.get(0);
                
                int uniformType = glTypeToUniformType(glType);
                if (uniformType < 0) {
                    continue;
                }
                
                int count = getDefaultCount(uniformType) * arraySize;
                
                Uniform uniform = new Uniform(name, uniformType, count, this);
                uniform.setLocation(location);
                uniformMap.put(name, uniform);
            }
        }
    }
    
    private static int glTypeToUniformType(int glType) {
        switch (glType) {
            case GL_INT:
                return Uniform.UT_INT1;
            case GL_INT_VEC2:
                return Uniform.UT_INT2;
            case GL_INT_VEC3:
                return Uniform.UT_INT3;
            case GL_INT_VEC4:
                return Uniform.UT_INT4;
            case GL_FLOAT:
                return Uniform.UT_FLOAT1;
            case GL_FLOAT_VEC2:
                return Uniform.UT_FLOAT2;
            case GL_FLOAT_VEC3:
                return Uniform.UT_FLOAT3;
            case GL_FLOAT_VEC4:
                return Uniform.UT_FLOAT4;
            case GL_FLOAT_MAT2:
                return Uniform.UT_MAT2;
            case GL_FLOAT_MAT3:
                return Uniform.UT_MAT3;
            case GL_FLOAT_MAT4:
                return Uniform.UT_MAT4;
            case GL_SAMPLER_2D:
            case GL_SAMPLER_CUBE:
                return Uniform.UT_INT1;
            default:
                return -1;
        }
    }
    
    private static int getDefaultCount(int type) {
        switch (type) {
            case Uniform.UT_INT1:
            case Uniform.UT_FLOAT1:
                return 1;
            case Uniform.UT_INT2:
            case Uniform.UT_FLOAT2:
                return 2;
            case Uniform.UT_INT3:
            case Uniform.UT_FLOAT3:
                return 3;
            case Uniform.UT_INT4:
            case Uniform.UT_FLOAT4:
                return 4;
            case Uniform.UT_MAT2:
                return 4;
            case Uniform.UT_MAT3:
                return 9;
            case Uniform.UT_MAT4:
                return 16;
            default:
                throw new IllegalArgumentException("Unknown uniform type: " + type);
        }
    }
    
    public void uploadUniforms(){
        ensureAlive();
        for (Uniform uniform : uniformMap.values()) {
            uniform.upload();
        }
    }

    public int getProgramId() {
        ensureAlive();
        return programId;
    }

    public void bind() {
        ensureAlive();
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;

        for (Uniform uniform : uniformMap.values()) {
            uniform.close();
        }
        uniformMap.clear();

        if (programId != 0) {
            glDeleteProgram(programId);
            programId = 0;
        }
        
        if (vertex.shaderId != 0) {
            glDeleteShader(vertex.shaderId);
            vertex.shaderId = 0;
        }
        if (fragment.shaderId != 0) {
            glDeleteShader(fragment.shaderId);
            fragment.shaderId = 0;
        }
    }

    private void ensureAlive() {
        if (destroyed) {
            throw new IllegalStateException("ShaderProgram already destroyed");
        }
        if (programId == 0) {
            throw new IllegalStateException("ShaderProgram not initialized");
        }
    }

    private static int linkProgram(int vertexId, int fragmentId, ResourceLocation vertexLoc, ResourceLocation fragmentLoc) {
        int program = glCreateProgram();
        if (program == 0) {
            throw new IllegalStateException("Failed to create OpenGL program");
        }

        glAttachShader(program, vertexId);
        glAttachShader(program, fragmentId);
        glLinkProgram(program);

        int linked = glGetProgrami(program, GL_LINK_STATUS);
        if (linked == GL_FALSE) {
            String log = glGetProgramInfoLog(program);
            glDetachShader(program, vertexId);
            glDetachShader(program, fragmentId);
            glDeleteProgram(program);
            throw new IllegalStateException(
                "Failed to link shader program (vertex=" + vertexLoc + ", fragment=" + fragmentLoc + ")\n" + log
            );
        }

        // detach for cleanliness
        glDetachShader(program, vertexId);
        glDetachShader(program, fragmentId);
        return program;
    }

    private static int compileShader(ShaderModule module, String source) {
        int shader = glCreateShader(module.type);
        if (shader == 0) {
            throw new IllegalStateException("Failed to create shader: " + module.id);
        }
        module.shaderId = shader;

        glShaderSource(shader, source);
        glCompileShader(shader);

        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (compiled == GL_FALSE) {
            String log = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            module.shaderId = 0;
            throw new IllegalStateException("Failed to compile shader: " + module.id + "\n" + log);
        }

        return shader;
    }

    private static String readText(IResource resource) {
        try (var reader = resource.openAsReader()) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[8192];
            int n;
            while ((n = reader.read(buf)) >= 0) {
                sb.append(buf, 0, n);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read resource", e);
        }
    }

    private static final class ShaderModule {

        private final ResourceLocation id;
        private final int type;
        private int shaderId;

        private ShaderModule(ResourceLocation id, int type) {
            this.id = Objects.requireNonNull(id, "id");
            this.type = type;
        }
    }
}
