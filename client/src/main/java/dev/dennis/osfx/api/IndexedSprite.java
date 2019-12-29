package dev.dennis.osfx.api;

public interface IndexedSprite extends AbstractSprite {
    byte[] getPixels();

    int[] getPalette();
}
