package dev.dennis.osfx;

import dev.dennis.osfx.api.Sprite;

public interface Callbacks {
    void onFrameStart();

    void onFrameEnd();

    boolean drawSprite(Sprite sprite, int x, int y);

    boolean drawSprite(Sprite sprite, int x, int y, int width, int height);
}
