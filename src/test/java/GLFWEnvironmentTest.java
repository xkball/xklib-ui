import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL40C;
import org.lwjgl.system.MemoryUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

class GLFWEnvironmentTest {

    @Test
    void testGLFWWindowCreation() {

        GLFWErrorCallback.createPrint(System.err).set();
        
        assertTrue(glfwInit(), "Failed to initialize GLFW");
        
        try {
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            
            long window = glfwCreateWindow(800, 600, "LWJGL Test Window", glfwGetPrimaryMonitor(), MemoryUtil.NULL);
            assertNotEquals(MemoryUtil.NULL, window, "Failed to create GLFW window");
            
            glfwMakeContextCurrent(window);
            GL.createCapabilities();
            printMaxOpenGLVersion();
            String version = glGetString(GL_VERSION);
            assertNotNull(version, "Failed to get OpenGL version");
            System.out.println("OpenGL Version: " + version);
            
            while (!glfwWindowShouldClose(window)) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                
                glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
                
                glfwSwapBuffers(window);
                glfwPollEvents();
                
                if (glfwGetTime() > 5.0) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
            
            glfwDestroyWindow(window);
            
        } finally {
            glfwTerminate();
        }
    }
    
    private void printMaxOpenGLVersion() {
        // 获取支持的最大OpenGL版本
        int majorVersion = glGetInteger(GL40C.GL_MAJOR_VERSION);
        int minorVersion = glGetInteger(GL30C.GL_MINOR_VERSION);
        
        // 获取OpenGL版本字符串
        String versionString = glGetString(GL_VERSION);
        String vendor = glGetString(GL_VENDOR);
        String renderer = glGetString(GL_RENDERER);
        String shadingLanguageVersion = glGetString(GL20C.GL_SHADING_LANGUAGE_VERSION);
        
        System.out.println("=== OpenGL 信息 ===");
        System.out.println("最高支持版本: " + majorVersion + "." + minorVersion);
        System.out.println("版本字符串: " + versionString);
        System.out.println("供应商: " + vendor);
        System.out.println("渲染器: " + renderer);
        System.out.println("着色器语言版本: " + shadingLanguageVersion);
        
        // 检查特定版本支持
        boolean supports33 = (majorVersion > 3) || (majorVersion == 3 && minorVersion >= 3);
        boolean supports40 = (majorVersion > 4) || (majorVersion == 4 && minorVersion >= 0);
        boolean supports46 = (majorVersion > 4) || (majorVersion == 4 && minorVersion >= 6);
        
        System.out.println("支持 OpenGL 3.3: " + supports33);
        System.out.println("支持 OpenGL 4.0: " + supports40);
        System.out.println("支持 OpenGL 4.6: " + supports46);
    }
}