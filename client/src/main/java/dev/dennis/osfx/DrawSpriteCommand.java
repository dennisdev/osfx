package dev.dennis.osfx;

import java.nio.IntBuffer;

public class DrawSpriteCommand {
    private final IntBuffer pixelsBuf;

    private final int spriteWidth;

    private final int spriteHeight;

    private final int x;

    private final int y;

    private final int width;

    private final int height;

    private final int alpha;

    private final int scissorX;

    private final int scissorY;

    private final int scissorWidth;

    private final int scissorHeight;

    public DrawSpriteCommand(IntBuffer pixelsBuf, int spriteWidth, int spriteHeight, int x, int y,
                             int width, int height, int alpha, int scissorX, int scissorY,
                             int scissorWidth, int scissorHeight) {
        this.pixelsBuf = pixelsBuf;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.alpha = alpha;
        this.scissorX = scissorX;
        this.scissorY = scissorY;
        this.scissorWidth = scissorWidth;
        this.scissorHeight = scissorHeight;
    }

    public IntBuffer getPixelsBuf() {
        return pixelsBuf;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
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

    public int getAlpha() {
        return alpha;
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
