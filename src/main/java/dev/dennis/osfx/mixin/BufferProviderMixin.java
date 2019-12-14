package dev.dennis.osfx.mixin;

import dev.dennis.mixin.Getter;
import dev.dennis.mixin.Mixin;
import dev.dennis.mixin.Setter;
import dev.dennis.osfx.api.BufferProvider;

@Mixin("BufferProvider")
public abstract class BufferProviderMixin implements BufferProvider {
    @Getter("pixels")
    @Override
    public abstract int[] getPixels();

    @Setter("pixels")
    @Override
    public abstract void setPixels(int[] pixels);
}
