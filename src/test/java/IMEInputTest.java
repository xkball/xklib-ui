import com.xkball.xklib.ui.backend.input.CharacterEvent;
import com.xkball.xklib.ui.widget.AbstractWidget;
import com.xkball.xklib.ui.widget.GuiSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IMEInputTest {
    
    private GuiSystem guiSystem;
    
    @BeforeEach
    public void setUp() {
        guiSystem = new GuiSystem();
    }
    
    @Test
    public void testChineseCharacterInput() {
        TestInputWidget widget = new TestInputWidget(0, 0, 100, 100);
        widget.setFocused(true);
        guiSystem.addScreenLayer(widget);
        
        String chineseText = "你好世界";
        for (char c : chineseText.toCharArray()) {
            CharacterEvent event = new CharacterEvent(c, 0);
            widget.charTyped(event);
        }
        
        assertEquals(4, widget.getInputCharacters().size());
        assertTrue(widget.getInputCharacters().contains((int) '你'));
        assertTrue(widget.getInputCharacters().contains((int) '好'));
        assertTrue(widget.getInputCharacters().contains((int) '世'));
        assertTrue(widget.getInputCharacters().contains((int) '界'));
    }
    
    @Test
    public void testMixedLanguageInput() {
        TestInputWidget widget = new TestInputWidget(0, 0, 100, 100);
        widget.setFocused(true);
        guiSystem.addScreenLayer(widget);
        
        String mixedText = "Hello世界123";
        for (char c : mixedText.toCharArray()) {
            CharacterEvent event = new CharacterEvent(c, 0);
            widget.charTyped(event);
        }
        
        assertEquals(10, widget.getInputCharacters().size());
    }
    
    @Test
    public void testUnicodeCharacterInput() {
        TestInputWidget widget = new TestInputWidget(0, 0, 100, 100);
        widget.setFocused(true);
        guiSystem.addScreenLayer(widget);
        
        int[] unicodeChars = {
            0x4E2D,
            0x6587,
            0x8F93,
            0x5165,
            0x1F600
        };
        
        for (int codepoint : unicodeChars) {
            CharacterEvent event = new CharacterEvent(codepoint, 0);
            widget.charTyped(event);
        }
        
        assertEquals(5, widget.getInputCharacters().size());
    }
    
    static class TestInputWidget extends AbstractWidget {
        private final List<Integer> inputCharacters = new ArrayList<>();
        
        public TestInputWidget(int x, int y, int width, int height) {
            super(x, y, width, height);
        }
        
        @Override
        protected boolean onCharTyped(com.xkball.xklib.api.gui.input.ICharEvent event) {
            inputCharacters.add(event.codepoint());
            return true;
        }
        
        public List<Integer> getInputCharacters() {
            return inputCharacters;
        }
    }
}
