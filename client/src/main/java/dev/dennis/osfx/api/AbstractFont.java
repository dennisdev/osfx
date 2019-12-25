package dev.dennis.osfx.api;

public interface AbstractFont {
    byte[][] getGlyphs();

    int[] getGlyphWidths();

    int[] getGlyphHeights();
}
