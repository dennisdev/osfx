package dev.dennis.osfx.mixin;

import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.Scene;
import dev.dennis.osfx.inject.mixin.*;

@Mixin("Scene")
public abstract class SceneMixin implements Scene {
    @Shadow
    private static Client client;

    @Copy("draw")
    public abstract void rs$draw(int cameraX, int cameraY, int cameraZ, int pitch, int yaw, int maxLevel);

    @Replace("draw")
    public void hd$draw(int cameraX, int cameraY, int cameraZ, int pitch, int yaw, int maxLevel) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawScene(this, cameraX, cameraY, cameraZ, pitch, yaw, maxLevel)) {
            return;
        }
        rs$draw(cameraX, cameraY, cameraZ, pitch, yaw, maxLevel);
    }

    @Getter("tileHeights")
    @Override
    public abstract int[][][] getTileHeights();
}
