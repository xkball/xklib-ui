import com.xkball.xklib.ui.backend.window.Window;
import com.xkball.xklib.ui.backend.gl.font.Font;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FontRendererTest {
    
    @Test
    void testFontWidth() {
        Window window = new Window(64, 64, "font-width-test");
        window.init();
        
        try {
            Font font = new Font(16);
            
            int width1 = font.width("A");
            int width2 = font.width("AA");
            int width3 = font.width("");
            
            assertTrue(width1 > 0);
            assertTrue(width2 > width1);
            assertEquals(0, width3);
            
            font.destroy();
        } finally {
            window.destroy();
        }
    }
}
