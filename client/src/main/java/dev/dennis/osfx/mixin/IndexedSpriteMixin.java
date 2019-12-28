package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.IndexedSprite;
import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;

@Mixin("IndexedSprite")
public abstract class IndexedSpriteMixin implements IndexedSprite {
    @Getter("pixels")
    @Override
    public abstract byte[] getPixels();

    @Getter("width")
    @Override
    public abstract int getWidth();

    @Getter("height")
    @Override
    public abstract int getHeight();

    @Getter("offsetX")
    @Override
    public abstract int getOffsetX();

    @Getter("offsetY")
    @Override
    public abstract int getOffsetY();

    @Getter("maxWidth")
    @Override
    public abstract int getMaxWidth();

    @Getter("maxHeight")
    @Override
    public abstract int getMaxHeight();
}
