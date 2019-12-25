package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.AbstractFont;
import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;

@Mixin("AbstractFont")
public abstract class AbstractFontMixin implements AbstractFont {
    @Getter("glyphs")
    @Override
    public abstract byte[][] getGlyphs();
}
