package com.xkball.xklib.resource;

import com.xkball.xklib.api.resource.IResource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ClasspathResource implements IResource {

    private final ClassLoader classLoader;
    private final String resourcePath;

    public ClasspathResource(ClassLoader classLoader, String resourcePath) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
        this.resourcePath = Objects.requireNonNull(resourcePath, "resourcePath");
    }

    @Override
    public InputStream open() {
        InputStream stream = classLoader.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }
        return stream;
    }

    @Override
    public BufferedReader openAsReader() {
        return new BufferedReader(new InputStreamReader(open(), StandardCharsets.UTF_8));
    }
}
