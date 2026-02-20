import com.xkball.xklib.ui.backend.window.Window;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

class WindowTest {

    @Test
    void testDefaultWindowConfig() {
        Window window = new Window();
        window.init();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            glfwGetWindowSize(window.getHandle(), w, h);
            assertEquals(1280, w.get(0));
            assertEquals(720, h.get(0));
            assertEquals(MemoryUtil.NULL, glfwGetWindowMonitor(window.getHandle()));
        } finally {
            window.destroy();
        }
    }
}
