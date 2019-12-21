package dev.dennis.osfx;

import java.nio.IntBuffer;

public class DrawSpriteCommand {
    private final IntBuffer pixelsBuf;

    private final int x;

    private final int y;

    private final int width;

    private final int height;

    private final int scissorX;

    private final int scissorY;

    private final int scissorWidth;

    private final int scissorHeight;

    public DrawSpriteCommand(IntBuffer pixelsBuf, int x, int y, int width, int height,
                             int scissorX, int scissorY, int scissorWidth, int scissorHeight) {
        this.pixelsBuf = pixelsBuf;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scissorX = scissorX;
        this.scissorY = scissorY;
        this.scissorWidth = scissorWidth;
        this.scissorHeight = scissorHeight;
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

    public int getScissorX() {
        return scissorX;
    }

    public int getScissorY() {
        return scissorY;
    }

    public int getScissorWidth() {
        return scissorWidth;
    }

    public int getScissorHeight() {
        return scissorHeight;
    }
}
