package dev.dennis.osfx.api;

public interface IndexedSprite {
    byte[] getPixels();

    int[] getPalette();

    int getWidth();

    int getHeight();

    int getOffsetX();

    int getOffsetY();

    int getMaxWidth();

    int getMaxHeight();
}
