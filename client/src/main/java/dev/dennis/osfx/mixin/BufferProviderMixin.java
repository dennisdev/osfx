package dev.dennis.osfx.mixin;

import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Invoke;
import dev.dennis.osfx.inject.mixin.Mixin;
import dev.dennis.osfx.inject.mixin.Setter;
import dev.dennis.osfx.api.BufferProvider;

@Mixin("BufferProvider")
public abstract class BufferProviderMixin implements BufferProvider {
    @Invoke("setRaster")
    @Override
    public abstract void setRaster();

    @Getter("pixels")
    @Override
    public abstract int[] getPixels();

    @Setter("pixels")
    @Override
    public abstract void setPixels(int[] pixels);
}
