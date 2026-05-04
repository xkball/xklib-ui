package com.xkball.xklibmc_example.ui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NonNullByDefault
public class WorldTerrainWidgetInner extends Widget {

    private final Vector3f cameraTarget = new Vector3f();
    private BlockPos centerPos = BlockPos.ZERO;
    private float xRot = 89.0f;
    private float cameraLength = 0;
    private float yRot = 0.0f;
    private boolean rotating;
    private float fov = 60;
    private WorldTerrainPipRenderer.@Nullable WorldTerrainState lastState;
    private @Nullable Vector3f lastClickedWorldPos;
    
    private final BooleanLayoutVariable terrain;
    private final BooleanLayoutVariable grid;
    private final BooleanLayoutVariable player;
    private final BooleanLayoutVariable cameraTarget_;
    private final BooleanLayoutVariable depress_sphere;
    private final BooleanLayoutVariable debug;
    private final IntLayoutVariable yMode;
    private final IntLayoutVariable fixY;
    private final IntLayoutVariable lodDistance;
    private final IntLayoutVariable viewDistance;

    public WorldTerrainWidgetInner(BooleanLayoutVariable terrain, BooleanLayoutVariable grid, BooleanLayoutVariable player, BooleanLayoutVariable cameraTarget, BooleanLayoutVariable depress_sphere, BooleanLayoutVariable debug, IntLayoutVariable yMode, IntLayoutVariable fixY, IntLayoutVariable lodDistance, IntLayoutVariable viewDistance) {
        this.terrain = terrain;
        this.grid = grid;
        this.player = player;
        this.cameraTarget_ = cameraTarget;
        this.depress_sphere = depress_sphere;
        this.debug = debug;
        this.yMode = yMode;
        this.fixY = fixY;
        this.lodDistance = lodDistance;
        this.viewDistance = viewDistance;
        this.initCamera();
        this.setOverflow(false);
        this.fixY.addCallback(_ -> this.setCameraY());
        this.yMode.addCallback(_ -> this.setCameraY());
    }

    private void initCamera() {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if(level == null) return;
        var player = mc.player;
        if(player == null) return;
        var cam = mc.gameRenderer.getMainCamera();
        yRot = cam.yRot();
        centerPos = player.blockPosition();
        centerPos = centerPos.atY(level.getMinY());
        cameraTarget.set(centerPos.getX(), 0, centerPos.getZ());
        this.setCameraY();
        WorldTerrainPipRenderer.update();
    }
    
    public void reLocateCamera(){
        var player = Minecraft.getInstance().player;
        if(player == null) return;
        this.cameraTarget.x = player.blockPosition().getX();
        this.cameraTarget.z = player.blockPosition().getZ();
    }
    
    @Override
    public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        return this.onMouseDragged(event, dx, dy);
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        if(graphics instanceof B3dGuiGraphics b3dGuiGraphics){
            var inner = b3dGuiGraphics.getInner();
            var scaleX = XKLibBaseScreen.tryGetScaleX();
            var scaleY = XKLibBaseScreen.tryGetScaleY();
            var list = new ArrayList<String>();
            if(terrain.get()) list.add("terrain");
            if(grid.get()) list.add("grid");
            if(player.get()) list.add("player");
            if(cameraTarget_.get()) list.add("cameraTarget");
            lastState = new WorldTerrainPipRenderer.WorldTerrainState(
                    list,
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
                    depress_sphere.get(),
                    lodDistance.get(),
                    null,
                    new ScreenRectangle((int) (x/scaleX), (int) (y/scaleY), (int) (width/scaleX), (int) (height/scaleY))
            );
            inner.submitPictureInPictureRenderState(lastState);
            if(player.get()) this.renderPlayerHead(b3dGuiGraphics);
            if(lastClickedWorldPos != null && lastState.frustum().isVisible(new AABB(lastClickedWorldPos.x,lastClickedWorldPos.y,lastClickedWorldPos.z,lastClickedWorldPos.x + 1,lastClickedWorldPos.y + 1,lastClickedWorldPos.z + 1))) {
                var pos = lastState.projWorld2Screen(this, lastClickedWorldPos);
                graphics.drawString("world: " + vec3fToString(lastClickedWorldPos), pos.x, pos.y, -1);
            }
            if(debug.get()) {
                var y_ = y;
                graphics.drawString("fov: " + fov,x,y_,-1); y_ += 10;
                graphics.drawString("xRot: " + xRot,x,y_,-1); y_ += 10;
                graphics.drawString("yRot: " + yRot,x,y_,-1); y_ += 10;
                graphics.drawString("focus: " + this.isPrimaryFocused(),x,y_,-1); y_ += 10;
                graphics.drawString("queue: " + TerrainChunkManager.INSTANCE.taskQueue.taskCount(),x,y_,-1); y_ += 10;
                graphics.drawString("memAlloc: " + VanillaUtils.memSize(TerrainChunkManager.INSTANCE.getMemAlloc()),x,y_,-1); y_ += 10;
//                graphics.drawString("memUsed: " + VanillaUtils.memSize(TerrainChunkManager.INSTANCE.getMemUsed()),x,y_,-1);
                graphics.drawString("length: " + cameraLength,x,y_,-1); y_ += 10;
                graphics.drawString("camTar: " + vec3fToString(cameraTarget), x, y_,-1); y_ += 10;
                graphics.drawString("camPos: " + vec3fToString(dirVec().normalize(cameraLength + 100).add(cameraTarget)), x, y_,-1); y_ += 10;
            }
        }
    }
    
    public void renderPlayerHead(B3dGuiGraphics guiGraphics){
        var level = Minecraft.getInstance().level;
        var player = Minecraft.getInstance().player;
        if(level == null || player == null || lastState == null) return;
        var playInfos = player.connection.getListedOnlinePlayers();
        for(var p : playInfos){
            var uuid = p.getProfile().id();
            var entity = level.getEntity(uuid);
            if(entity == null || !lastState.frustum().isVisible(entity.getBoundingBox())) continue;
            var pos = lastState.projWorld2Screen(this, entity.position().toVector3f().add(0,2f,0));
            var px = pos.x - 8;
            var py = pos.y - 10;
            PlayerFaceExtractor.extractRenderState(guiGraphics.getInner(),p.getSkin(), (int) px, (int) py,16);
            py -= 10;
            guiGraphics.drawCenteredString(p.getProfile().name(), pos.x, py, -1);
        }
    }
    
    public @Nullable Vector3f projScreen2World(double screenX, double screenY) {
        return this.projScreen2World((float) screenX, (float) screenY);
    }
    
    public @Nullable Vector3f projScreen2World(float screenX, float screenY) {
        if(lastState == null) return null;
        var storage = TerrainChunkManager.INSTANCE.getCurrentLevelChunkStorage();
        if(storage == null) return null;
        return lastState.projScreen2World(this, storage, screenX, screenY);
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        return super.mouseMoved(mouseX, mouseY);
    }
    
    private String vec3fToString(Vector3f vec) {
        return String.format("( %.2f, %.2f, %.2f )", vec.x(), vec.y(), vec.z());
    }
    
    private Vector3f dirVec(){
        var x = (float) (Math.cos(Math.toRadians(xRot)) * Math.sin(Math.toRadians(yRot)));
        var y = (float) (Math.sin(Math.toRadians(xRot)));
        var z = (float) (Math.cos(Math.toRadians(xRot)) * Math.cos(Math.toRadians(yRot)));
        return new Vector3f(x,y,z).normalize();
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if(event.button() == 0){
            var worldPos = this.projScreen2World(event.x(), event.y());
            if(worldPos != null) {
                lastClickedWorldPos = worldPos;
            }
        }
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
            this.setCameraY();
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
        if(fov > 90 - 1e-6) {
            cameraLength -= (float) (scrollY * Math.log10(cameraLength + 10f));
            cameraLength = Math.max(cameraLength, 0);
        }
        if(cameraLength < 1e-6){
            fov = (float) Math.clamp(fov - scrollY, 5, 90);
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
        return true;
    }
    
    private void moveCamera(float dx, float dz){
        float speed = 30.0f * fov/120;
        var dir = new  Vector2f(dx,dz).mul(speed);
        dir.mul(new Matrix2f().rotate((float) Math.toRadians(-yRot)));
        cameraTarget.add(dir.x, 0, dir.y);
//        cameraTarget.add(dx*speed,0,dz*speed);
        this.setCameraY();
    }
    
    private void setCameraY(){
        var level = Minecraft.getInstance().level;
        if(level != null){
            if(yMode.get() == 0){
                cameraTarget.y = level.getSeaLevel();
                var storage = TerrainChunkManager.INSTANCE.getCurrentLevelChunkStorage();
                if (storage != null) {
                    var h = storage.getHeight((int) cameraTarget.x, (int) cameraTarget.z);
                    if(h != level.getMinY()){
                        cameraTarget.y = h;
                    }
                }
            }
            else{
                cameraTarget.y = fixY.get();
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
