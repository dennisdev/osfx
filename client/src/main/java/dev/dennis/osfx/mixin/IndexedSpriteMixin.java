package dev.dennis.osfx.mixin;

import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.IndexedSprite;
import dev.dennis.osfx.inject.mixin.*;

@Mixin("IndexedSprite")
public abstract class IndexedSpriteMixin implements IndexedSprite {
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

    @Copy("drawScaled")
    public abstract void rs$drawScaled(int x, int y, int width, int height);

    @Replace("drawScaled")
    public void hd$drawScaled(int x, int y, int width, int height) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawSprite(this, x, y, width, height)) {
            return;
        }
        rs$drawScaled(x, y, width, height);
    }

    @Getter("pixels")
    @Override
    public abstract byte[] getPixels();

    @Getter("palette")
    @Override
    public abstract int[] getPalette();

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

    @Getter("maxWidth")
    @Override
    public abstract int getMaxWidth();

    @Getter("maxHeight")
    @Override
    public abstract int getMaxHeight();
}
