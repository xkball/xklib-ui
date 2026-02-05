import com.xkball.xklib.api.resource.IResource;
import com.xkball.xklib.resource.ClasspathResourceManager;
import com.xkball.xklib.resource.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceManagerTest {

    @Test
    void getResourceReturnsFile() throws IOException {
        ClasspathResourceManager manager = new ClasspathResourceManager(
            Thread.currentThread().getContextClassLoader()
        );
        ResourceLocation location = new ResourceLocation("xklib", "1.txt");
        IResource resource = manager.getResource(location);

        try (InputStream stream = resource.open()) {
            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals("111222333", content.trim());
        }
    }

    @Test
    void getResourceRejectsFolder() {
        ClasspathResourceManager manager = new ClasspathResourceManager(
            Thread.currentThread().getContextClassLoader()
        );
        ResourceLocation location = new ResourceLocation("xklib", "");

        assertThrows(IllegalStateException.class, () -> manager.getResource(location));
    }

    @Test
    void getResourceStackLoadsFolder() {
        ClasspathResourceManager manager = new ClasspathResourceManager(
            Thread.currentThread().getContextClassLoader()
        );
        ResourceLocation location = new ResourceLocation("xklib", "");
        List<IResource> stack = manager.getResourceStack(location);

        assertFalse(stack.isEmpty());
        assertTrue(stack.size() >= 2);
    }

    @Test
    void getResourceStackMissingReturnsEmpty() {
        ClasspathResourceManager manager = new ClasspathResourceManager(
            Thread.currentThread().getContextClassLoader()
        );
        ResourceLocation location = new ResourceLocation("xklib", "missing.txt");
        List<IResource> stack = manager.getResourceStack(location);

        assertTrue(stack.isEmpty());
    }

    @Test
    void listResourceStacksFindsAssets() {
        ClasspathResourceManager manager = new ClasspathResourceManager(
            Thread.currentThread().getContextClassLoader()
        );
        ResourceLocation location = new ResourceLocation("xklib", "");
        Map<ResourceLocation, List<IResource>> resources = manager.listResourceStacks(location);

        assertFalse(resources.isEmpty());
        assertTrue(resources.containsKey(new ResourceLocation("xklib", "1.txt")));
    }
}
