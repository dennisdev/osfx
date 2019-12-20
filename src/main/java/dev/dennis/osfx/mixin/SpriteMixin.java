package dev.dennis.osfx.mixin;

import dev.dennis.mixin.*;
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

    @Getter("pixels")
    @Override
    public abstract int[] getPixels();

    @Getter("width")
    @Override
    public abstract int getWidth();

    @Getter("height")
    @Override
    public abstract int getHeight();

    @Getter("offsetX")
    @Override
    public abstract int getOffsetX();

    @Getter("offsetY")
    @Override
    public abstract int getOffsetY();
}
