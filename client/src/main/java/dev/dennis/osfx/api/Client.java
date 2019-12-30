package dev.dennis.osfx.api;

import dev.dennis.osfx.Callbacks;

public interface Client extends GameEngine {
    Callbacks getCallbacks();

    void setCallbacks(Callbacks callbacks);

    AbstractFont getCurrentFont();

    void setCurrentFont(AbstractFont font);

    BufferProvider getBufferProvider();

    int getGameDrawingMode();

    void setGameDrawingMode(int mode);

    int getCameraPitch();

    int getCameraYaw();

    int getCameraZoom();

    int getScissorX();

    int getScissorY();

    int getScissorEndX();

    int getScissorEndY();

    int getScissorWidth();

    int getScissorHeight();

    int[] getGraphicsPixels();

    int getViewportX();

    int getViewportY();
}
