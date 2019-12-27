package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.AbstractFont;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.inject.mixin.*;

import java.util.HashMap;
import java.util.Map;

@Mixin("AbstractFont")
public abstract class AbstractFontMixin implements AbstractFont {
    @Shadow
    private static Client client;

    @Getter
    private Map<byte[], Integer> glyphIdMap;

    @Getter
    @Setter
    private short textureId;

    @Getter
    @Setter
    private int textureSize;

    @Inject(value = "<init>", end = true)
    private void onConstructorEnd() {
        this.textureId = -1;
        this.glyphIdMap = new HashMap<>();
        byte[][] glyphs = getGlyphs();
        // The glyphs are null if this instance is used for font metrics
        if (glyphs[0] != null) {
            int id = 0;
            for (byte[] glyph : glyphs) {
                glyphIdMap.put(glyph, id++);
            }
        }
    }

    @Inject("drawText")
    private void onDrawText() {
        client.setCurrentFont(this);
    }

    @Inject("drawMouseoverText")
    private void onDrawMouseoverText() {
        client.setCurrentFont(this);
    }

    @Getter("glyphs")
    @Override
    public abstract byte[][] getGlyphs();

    @Getter("glyphWidths")
    @Override
    public abstract int[] getGlyphWidths();

    @Getter("glyphHeights")
    @Override
    public abstract int[] getGlyphHeights();
}
