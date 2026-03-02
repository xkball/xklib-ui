package com.xkball.xklib.resource;

import com.xkball.xklib.x3d.api.resource.IResource;
import com.xkball.xklib.x3d.api.resource.IResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class ClasspathResourceManager implements IResourceManager {

    private static final String ASSETS_ROOT = "assets";
    private final ClassLoader classLoader;

    public ClasspathResourceManager(){
        this(null);
    }
    
    public ClasspathResourceManager(@Nullable ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNullElseGet(
            classLoader,
            () -> Thread.currentThread().getContextClassLoader()
        );
    }

    @Override
    public IResource getResource(ResourceLocation location) {
        String resourcePath = toResourcePath(location);
        URL url = classLoader.getResource(resourcePath);
        if (url == null || isDirectoryUrl(url, resourcePath)) {
            throw new IllegalStateException("Resource is not a file: " + resourcePath);
        }
        return new ClasspathResource(classLoader, resourcePath);
    }

    @Override
    public List<IResource> getResourceStack(ResourceLocation location) {
        Map<ResourceLocation, List<IResource>> stacks = listResourceStacks(location);
        if (stacks.isEmpty()) {
            return List.of();
        }
        return stacks.values().stream().flatMap(List::stream).toList();
    }

    @Override
    public Map<ResourceLocation, List<IResource>> listResourceStacks(ResourceLocation location) {
        String namespace = location.namespace();
        String normalizedPath = normalizePath(location.path());
        String basePath = buildBasePath(namespace, normalizedPath);
        String namespacePrefix = ASSETS_ROOT + "/" + namespace + "/";

        Map<ResourceLocation, List<IResource>> results = new LinkedHashMap<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(basePath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    readFromFileUrl(results, url, namespace, normalizedPath);
                } else if ("jar".equals(protocol)) {
                    readFromJarUrl(results, url, namespace, normalizedPath, namespacePrefix, basePath);
                }
            }
        } catch (IOException | URISyntaxException ignored) {
            return results;
        }
        return results;
    }

    private void readFromFileUrl(
        Map<ResourceLocation, List<IResource>> results,
        URL url,
        String namespace,
        String normalizedPath
    ) throws URISyntaxException, IOException {
        Path path = Paths.get(url.toURI());
        if (Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.walk(path)) {
                stream.filter(Files::isRegularFile).forEach(file -> {
                    String relative = path.relativize(file).toString().replace('\\', '/');
                    String relativeToNamespace = normalizedPath.isEmpty()
                        ? relative
                        : normalizedPath + "/" + relative;
                    addResource(results, namespace, relativeToNamespace);
                });
            }
        } else if (Files.isRegularFile(path)) {
            if (!normalizedPath.isEmpty()) {
                addResource(results, namespace, normalizedPath);
            }
        }
    }

    private void readFromJarUrl(
        Map<ResourceLocation, List<IResource>> results,
        URL url,
        String namespace,
        String normalizedPath,
        String namespacePrefix,
        String basePath
    ) throws IOException {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        String entryName = connection.getEntryName();
        String jarBase = entryName == null ? basePath : entryName;
        String jarPrefix = jarBase.endsWith("/") ? jarBase : jarBase + "/";
        try (JarFile jarFile = connection.getJarFile()) {
            JarEntry baseEntry = jarFile.getJarEntry(jarBase);
            if (baseEntry != null && !baseEntry.isDirectory()) {
                if (!normalizedPath.isEmpty()) {
                    addResource(results, namespace, normalizedPath);
                }
                return;
            }
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (!name.startsWith(jarPrefix) || !name.startsWith(namespacePrefix)) {
                    continue;
                }
                String relativeToNamespace = name.substring(namespacePrefix.length());
                if (normalizedPath.isEmpty()) {
                    addResource(results, namespace, relativeToNamespace);
                    continue;
                }
                if (relativeToNamespace.equals(normalizedPath)
                    || relativeToNamespace.startsWith(normalizedPath + "/")) {
                    addResource(results, namespace, relativeToNamespace);
                }
            }
        }
    }

    private void addResource(
        Map<ResourceLocation, List<IResource>> results,
        String namespace,
        String relativePath
    ) {
        String normalized = normalizePath(relativePath);
        String resourcePath = ASSETS_ROOT + "/" + namespace + "/" + normalized;
        ResourceLocation location = new ResourceLocation(namespace, normalized);
        results.put(location, List.of(new ClasspathResource(classLoader, resourcePath)));
    }

    private String toResourcePath(ResourceLocation location) {
        String normalized = normalizePath(location.path());
        return buildBasePath(location.namespace(), normalized);
    }

    private String buildBasePath(String namespace, String normalizedPath) {
        if (normalizedPath.isEmpty()) {
            return ASSETS_ROOT + "/" + namespace;
        }
        return ASSETS_ROOT + "/" + namespace + "/" + normalizedPath;
    }

    private boolean isDirectoryUrl(URL url, String resourcePath) {
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            try {
                Path path = Paths.get(url.toURI());
                return Files.isDirectory(path);
            } catch (URISyntaxException ignored) {
                return false;
            }
        }
        if ("jar".equals(protocol)) {
            try {
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                JarEntry entry = connection.getJarFile().getJarEntry(resourcePath);
                return entry != null && entry.isDirectory();
            } catch (IOException ignored) {
                return false;
            }
        }
        return false;
    }

    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        String normalized = path.trim().replace('\\', '/');
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.contains("..")) {
            throw new IllegalArgumentException("Path traversal is not allowed: " + path);
        }
        return normalized;
    }
}
