package dev.dennis.osfx.mixin;

import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;
import dev.dennis.osfx.api.MainBufferProvider;

import java.awt.*;

@Mixin("MainBufferProvider")
public abstract class MainBufferProviderMixin implements MainBufferProvider {
    @Getter("image")
    @Override
    public abstract Image getImage();
}
