package dev.dennis.osfx.mixin;

import dev.dennis.mixin.Getter;
import dev.dennis.mixin.Invoke;
import dev.dennis.mixin.Mixin;
import dev.dennis.mixin.Setter;
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
