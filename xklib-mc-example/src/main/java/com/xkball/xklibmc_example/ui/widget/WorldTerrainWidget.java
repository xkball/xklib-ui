package com.xkball.xklibmc_example.ui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public class WorldTerrainWidget extends Widget {

    private final Vector3f cameraTarget = new Vector3f();
    private BlockPos centerPos = BlockPos.ZERO;
    private float xRot = 89.0f;
    private float cameraLength = 0;
    private float yRot = 0.0f;
    private boolean rotating;
    private float fov = 60;

    public WorldTerrainWidget() {
        this.initCamera();
    }

    private void initCamera() {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if(level == null) return;
        var cam = mc.gameRenderer.getMainCamera();
        yRot = cam.yRot();
        centerPos = cam.blockPosition();
        centerPos = centerPos.atY(level.getMinY());
        cameraTarget.set(centerPos.getX(), 0, centerPos.getZ());
        this.setCameraY();
        WorldTerrainPipRenderer.update();
    }

    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        if(graphics instanceof B3dGuiGraphics b3dGuiGraphics){
            var inner = b3dGuiGraphics.getInner();
            var scaleX = XKLibBaseScreen.tryGetScaleX();
            var scaleY = XKLibBaseScreen.tryGetScaleY();
            var state = new WorldTerrainPipRenderer.WorldTerrainState(
                    List.of("terrain","grid"),
                    new Vector3f(cameraTarget),
                    centerPos,
                    fov,
                    cameraLength,
                    xRot,
                    yRot,
                    (int) (x/scaleX),
                    (int) ((x + width)/scaleX),
                    (int) (y/scaleY),
                    (int) ((y + height)/scaleY),
                    1.0f,
                    null,
                    new ScreenRectangle((int) (x/scaleX), (int) (y/scaleY), (int) (width/scaleX), (int) (height/scaleY))
            );
            inner.submitPictureInPictureRenderState(state);
        }
    }
    
    @Override
    public void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
        super.renderDebug(graphics, mouseX, mouseY);
        graphics.drawString("fov: " + fov,x,y,-1);
        graphics.drawString("xRot: " + xRot,x,y + 10,-1);
        graphics.drawString("yRot: " + yRot,x,y + 20,-1);
        graphics.drawString("focus: " + this.isPrimaryFocused(),x,y + 30,-1);
        graphics.drawString("queue: " + TerrainChunkManager.INSTANCE.updateQueue.size(),x,y + 40,-1);
        graphics.drawString("memAlloc: " + VanillaUtils.memSize(TerrainChunkManager.INSTANCE.gpuBuffer.gpuBuffer.size()),x,y + 50,-1);
        graphics.drawString("memUsed: " + VanillaUtils.memSize(TerrainChunkManager.INSTANCE.gpuBuffer.usedSize()),x,y + 60,-1);
        graphics.drawString("length: " + cameraLength,x,y + 70,-1);
        graphics.drawString("camDir: " + dirVec(), x, y + 80,-1);
    }
    
    private Vector3f dirVec(){
        var x = (float) (Math.cos(Math.toRadians(xRot)) * Math.sin(Math.toRadians(yRot)));
        var y = (float) (Math.sin(Math.toRadians(xRot)));
        var z = (float) (Math.cos(Math.toRadians(xRot)) * Math.cos(Math.toRadians(yRot)));
        return new Vector3f(x,y,z).normalize();
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 2) {
            rotating = true;
        }
        return true;
    }

    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        if (event.button() == 2) {
            rotating = false;
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (event.button() == 2) {
            if (!rotating) {
                return false;
            }
            float sens = 0.25f * Math.max(0.4f,fov/100);
            yRot = yRot - (float) dx * sens;
            xRot = xRot + (float) dy * sens;
            xRot = Math.clamp(xRot, -89.9f, 89.9f);
            yRot = (yRot + 360) % 360;
            return true;
        }
        if (event.button() == 1) {
            var speed = 1 + (cameraLength/100);
            this.moveCamera((float) (-dx / 100) * speed, (float) (-dy / 100) * speed);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if(fov > 100 - 1e-6) {
            cameraLength -= (float) scrollY;
            cameraLength = Math.max(cameraLength, 0);
        }
        if(cameraLength < 1e-6){
            fov = (float) Math.clamp(fov - scrollY, 5, 100);
        }
        
        return true;
    }
    
    @Override
    protected boolean onKeyPressed(IKeyEvent event) {
        int key = event.key();
        float dx = 0;
        float dz = 0;
        if (key == InputConstants.KEY_W) {
            dz = -1;
        } else if (key == InputConstants.KEY_S) {
            dz = 1;
        } else if (key == InputConstants.KEY_A) {
            dx = -1;
        } else if (key == InputConstants.KEY_D) {
            dx = 1;
        } else {
            return false;
        }
        this.moveCamera(dx,dz);
        this.setCameraY();
        return true;
    }
    
    private void moveCamera(float dx, float dz){
        float speed = 30.0f * fov/120;
        var dir = new  Vector2f(dx,dz).mul(speed);
        dir.mul(new Matrix2f().rotate((float) Math.toRadians(-yRot)));
        cameraTarget.add(dir.x, 0, dir.y);
//        cameraTarget.add(dx*speed,0,dz*speed);
    }
    
    private void setCameraY(){
        var level = Minecraft.getInstance().level;
        if(level != null){
            var h = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, centerPos.getX(), centerPos.getZ());
            if(h != -64){
                cameraTarget.y = h;
            }
            else{
                cameraTarget.y = level.getSeaLevel();
            }
        }
    }
    
    @Override
    public void onFocusChanged(boolean focused) {
        if(!focused) this.rotating = false;
    }
    
    @Override
    public boolean isFocusable() {
        return true;
    }
}
