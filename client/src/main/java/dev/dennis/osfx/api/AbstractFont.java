package dev.dennis.osfx.api;

import java.util.Map;

public interface AbstractFont {
    byte[][] getGlyphs();

    int[] getGlyphWidths();

    int[] getGlyphHeights();

    Map<byte[], Integer> getGlyphIdMap();

    short getTextureId();

    void setTextureId(short id);

    int getTextureSize();

    void setTextureSize(int size);
}
