package dev.dennis.osfx.mixin;

import dev.dennis.mixin.Copy;
import dev.dennis.mixin.Mixin;
import dev.dennis.mixin.Replace;
import dev.dennis.mixin.Shadow;
import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.Sprite;

@Mixin("Sprite")
public abstract class SpriteMixin implements Sprite {
    @Shadow
    private static Client client;

    @Copy("draw")
    public abstract void rs$draw(int x, int y);

    @Replace("draw")
    public void hd$draw(int x, int y) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawSprite(this, x, y)) {
            return;
        }
        rs$draw(x, y);
    }
}
