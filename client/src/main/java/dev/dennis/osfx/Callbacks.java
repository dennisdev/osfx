package dev.dennis.osfx;

import dev.dennis.osfx.api.*;

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

    boolean drawScene(Scene scene, int cameraX, int cameraY, int cameraZ, int pitch, int yaw, int maxLevel);

    boolean drawModel(Model model, int rotation, int x, int y, int z);

    boolean drawModelTriangle(Model model, int index);

    boolean drawTile(Scene scene, SceneTilePaint tile, int level, int x, int y);

    boolean drawTile(Scene scene, SceneTileModel tile, int x, int y);
}
