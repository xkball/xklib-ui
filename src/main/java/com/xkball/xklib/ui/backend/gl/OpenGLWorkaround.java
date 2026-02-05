package com.xkball.xklib.ui.backend.gl;

import com.xkball.xklib.XKLibWorkaround;
import com.xkball.xklib.ui.XKLibUI;
import com.xkball.xklib.ui.backend.Window;
import com.xkball.xklib.ui.backend.gl.font.Font;
import com.xkball.xklib.ui.backend.gl.font.FontRenderer;
import com.xkball.xklib.ui.backend.gl.texture.TextureManager;

public class OpenGLWorkaround {
    
    public static TextureManager textureManager;
    public static Window window;
    public static Font font;
    public static FontRenderer fontRenderer;
    
    public static void init(){
        textureManager = new TextureManager(XKLibWorkaround.resourceManager);
        window = new Window(1280, 720, XKLibUI.NAME);
        window.init();
        font = new Font(32);
        fontRenderer = new FontRenderer();
    }
}
