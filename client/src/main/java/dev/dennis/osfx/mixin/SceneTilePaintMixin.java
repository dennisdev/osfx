package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.SceneTilePaint;
import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;

@Mixin("SceneTilePaint")
public abstract class SceneTilePaintMixin implements SceneTilePaint {
    @Getter("swColor")
    @Override
    public abstract int getSwColor();

    @Getter("seColor")
    @Override
    public abstract int getSeColor();

    @Getter("neColor")
    @Override
    public abstract int getNeColor();

    @Getter("nwColor")
    @Override
    public abstract int getNwColor();

    @Getter("textureId")
    @Override
    public abstract int getTextureId();

    @Getter("rgb")
    @Override
    public abstract int getRgb();
}
