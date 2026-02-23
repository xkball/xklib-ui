package com.xkball.xklib.ui.backend.gl;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.XKLibUI;
import com.xkball.xklib.ui.backend.gl.state.GuiRenderer;
import com.xkball.xklib.ui.backend.window.Window;
import com.xkball.xklib.ui.backend.gl.font.Font;
import com.xkball.xklib.ui.backend.gl.font.FontRenderer;
import com.xkball.xklib.ui.backend.gl.texture.TextureManager;

public class OpenGLWorkaround {
    
    public static TextureManager textureManager;
    public static Window window;
    public static Font font;
    public static FontRenderer fontRenderer;
    public static GuiRenderer guiRenderer;
    
    public static void init(){
        textureManager = new TextureManager(XKLib.resourceManager);
        window = new Window(1280, 720, XKLibUI.NAME);
        window.init();
        font = new Font(32);
        fontRenderer = new FontRenderer();
        guiRenderer = new GuiRenderer();
        XKLib.gui.initGLFWCallbacks(window.getHandle());
        XKLib.gui.setGraphics(guiRenderer.getGuiGraphics());
    }
}
