package dev.dennis.osfx;

import dev.dennis.osfx.api.AbstractFont;
import dev.dennis.osfx.api.IndexedSprite;
import dev.dennis.osfx.api.Sprite;

public interface Callbacks {
    void onFrameStart();

    void onFrameEnd();

    boolean fillRectangle(int x, int y, int width, int height, int rgb);

    boolean fillRectangle(int x, int y, int width, int height, int rgb, int alpha);

    boolean drawHorizontalLine(int x, int y, int width, int rgb);

    boolean drawHorizontalLine(int x, int y, int width, int rgb, int alpha);

    boolean drawVerticalLine(int x, int y, int height, int rgb);

    boolean drawVerticalLine(int x, int y, int height, int rgb, int alpha);

    boolean drawSprite(IndexedSprite sprite, int x, int y);

    boolean drawSprite(IndexedSprite sprite, int x, int y, int width, int height);

    boolean drawSprite(Sprite sprite, int x, int y);

    boolean drawSprite(Sprite sprite, int x, int y, int alpha);

    boolean drawSprite(Sprite sprite, int x, int y, int width, int height);

    boolean drawSprite(Sprite sprite, int x, int y, int width, int height, int alpha);

    boolean drawGlyph(AbstractFont font, byte[] glyph, int x, int y, int width, int height, int rgb);

    boolean drawGlyph(AbstractFont font, byte[] glyph, int x, int y, int width, int height, int rgb, int alpha);
}
