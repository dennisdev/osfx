package dev.dennis.osfx.mixin;

import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.Sprite;
import dev.dennis.osfx.inject.mixin.*;

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

    @Copy("drawAlpha")
    public abstract void rs$drawAlpha(int x, int y, int alpha);

    @Replace("drawAlpha")
    public void hd$draw(int x, int y, int alpha) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawSprite(this, x, y, alpha)) {
            return;
        }
        rs$drawAlpha(x, y, alpha);
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

    @Copy("drawScaledAlpha")
    public abstract void rs$drawScaledAlpha(int x, int y, int width, int height, int alpha);

    @Replace("drawScaledAlpha")
    public void hd$drawScaledAlpha(int x, int y, int width, int height, int alpha) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawSprite(this, x, y, width, height, alpha)) {
            return;
        }
        rs$drawScaledAlpha(x, y, width, height, alpha);
    }

    @Copy("drawFast")
    public abstract void rs$drawFast(int x, int y);

    @Replace("drawFast")
    public void hd$drawFast(int x, int y) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawSprite(this, x, y)) {
            return;
        }
        rs$drawFast(x, y);
    }

    @Copy("drawFastScaled")
    public abstract void rs$drawFastScaled(int x, int y, int width, int height);

    @Replace("drawFastScaled")
    public void hd$drawFastScaled(int x, int y, int width, int height) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawSprite(this, x, y, width, height)) {
            return;
        }
        rs$drawFastScaled(x, y, width, height);
    }

    @Copy("drawColor")
    public abstract void rs$drawColor(int x, int y, int alpha, int rgb);

    @Replace("drawColor")
    public void hd$drawColor(int x, int y, int alpha, int rgb) {
        Callbacks callbacks = client.getCallbacks();
        // TODO should alpha blend with the rgb arg
        if (callbacks != null && callbacks.drawSprite(this, x, y, alpha)) {
            return;
        }
        rs$drawColor(x, y, alpha, rgb);
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

    @Getter("maxWidth")
    @Override
    public abstract int getMaxWidth();

    @Getter("maxHeight")
    @Override
    public abstract int getMaxHeight();
}
