package dev.dennis.osfx.mixin;

import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.Font;
import dev.dennis.osfx.inject.mixin.Copy;
import dev.dennis.osfx.inject.mixin.Mixin;
import dev.dennis.osfx.inject.mixin.Replace;
import dev.dennis.osfx.inject.mixin.Shadow;

@Mixin("Font")
public abstract class FontMixin implements Font {
    @Shadow
    private static Client client;

    @Copy("drawGlyph")
    public abstract void rs$drawGlyph(byte[] glyph, int x, int y, int width, int height, int rgb);

    @Replace("drawGlyph")
    public void hd$drawGlyph(byte[] glyph, int x, int y, int width, int height, int rgb) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawGlyph(this, glyph, x, y, width, height, rgb)) {
            return;
        }
        rs$drawGlyph(glyph, x, y, width, height, rgb);
    }

    @Copy("drawGlyphAlpha")
    public abstract void rs$drawGlyphAlpha(byte[] glyph, int x, int y, int width, int height, int rgb, int alpha);

    @Replace("drawGlyphAlpha")
    public void hd$drawGlyphAlpha(byte[] glyph, int x, int y, int width, int height, int rgb, int alpha) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawGlyph(this, glyph, x, y, width, height, rgb, alpha)) {
            return;
        }
        rs$drawGlyphAlpha(glyph, x, y, width, height, rgb, alpha);
    }

    @Copy("drawGlyphShadow")
    public static void rs$drawGlyphShadow(byte[] glyph, int x, int y, int width, int height, int rgb) {
        throw new UnsupportedOperationException();
    }

    @Replace("drawGlyphShadow")
    public static void hd$drawGlyphShadow(byte[] glyph, int x, int y, int width, int height, int rgb) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawGlyph(client.getCurrentFont(), glyph, x, y, width, height, rgb)) {
            return;
        }
        rs$drawGlyphShadow(glyph, x, y, width, height, rgb);
    }

    @Copy("drawGlyphAlphaShadow")
    public static void rs$drawGlyphAlphaShadow(byte[] glyph, int x, int y, int width, int height, int rgb, int alpha) {
        throw new UnsupportedOperationException();
    }

    @Replace("drawGlyphAlphaShadow")
    public static void hd$drawGlyphShadow(byte[] glyph, int x, int y, int width, int height, int rgb, int alpha) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawGlyph(client.getCurrentFont(), glyph, x, y, width, height, rgb, alpha)) {
            return;
        }
        rs$drawGlyphAlphaShadow(glyph, x, y, width, height, rgb, alpha);
    }
}
