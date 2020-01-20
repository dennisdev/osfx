package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.Texture;
import dev.dennis.osfx.api.TextureProvider;
import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;

@Mixin("TextureProvider")
public abstract class TextureProviderMixin implements TextureProvider {
    @Getter("textures")
    @Override
    public abstract Texture[] getTextures();
}
