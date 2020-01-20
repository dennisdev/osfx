package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.Texture;
import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;

@Mixin("Texture")
public abstract class TextureMixin implements Texture {
    @Getter("pixels")
    @Override
    public abstract int[] getPixels();
}
