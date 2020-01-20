package dev.dennis.osfx.api;

public interface TextureProvider {
    int[] load(int id);

    Texture[] getTextures();
}
