package dev.dennis.osfx;

import java.nio.IntBuffer;

public class DrawSpriteCommand {
    private final IntBuffer pixelsBuf;

    private final int x;

    private final int y;

    private final int width;

    private final int height;

    public DrawSpriteCommand(IntBuffer pixelsBuf, int x, int y, int width, int height) {
        this.pixelsBuf = pixelsBuf;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public IntBuffer getPixelsBuf() {
        return pixelsBuf;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
