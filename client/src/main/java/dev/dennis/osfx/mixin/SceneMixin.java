package dev.dennis.osfx.mixin;

import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.*;
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

    @Copy("drawTileUnderlay")
    public abstract void rs$drawTileUnderlay(SceneTilePaint tile, int level, int pitchSin, int pitchCos,
                                             int yawSin, int yawCos, int x, int y);

    @Replace("drawTileUnderlay")
    public void hd$drawTileUnderlay(SceneTilePaint tile, int level, int pitchSin, int pitchCos,
                                    int yawSin, int yawCos, int x, int y) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawTile(this, tile, level, x, y)) {
            return;
        }
        rs$drawTileUnderlay(tile, level, pitchSin, pitchCos, yawSin, yawCos, x, y);
    }

    @Copy("drawTileOverlay")
    public abstract void rs$drawTileOverlay(SceneTileModel tile, int pitchSin, int pitchCos, int yawSin, int yawCos,
                                            int x, int y);

    @Replace("drawTileOverlay")
    public void hd$drawTileOverlay(SceneTileModel tile, int pitchSin, int pitchCos, int yawSin, int yawCos,
                                   int x, int y) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawTile(this, tile, x, y)) {
            return;
        }
        rs$drawTileOverlay(tile, pitchSin, pitchCos, yawSin, yawCos, x, y);
    }

    @Getter("tiles")
    @Override
    public abstract Tile[][][] getTiles();

    @Getter("tileHeights")
    @Override
    public abstract int[][][] getTileHeights();
}
