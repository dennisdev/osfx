package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.SceneTileModel;
import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;

@Mixin("SceneTileModel")
public abstract class SceneTileModelMixin implements SceneTileModel {
    @Getter("verticesX")
    @Override
    public abstract int[] getVerticesX();

    @Getter("verticesY")
    @Override
    public abstract int[] getVerticesY();

    @Getter("verticesZ")
    @Override
    public abstract int[] getVerticesZ();

    @Getter("colorsA")
    @Override
    public abstract int[] getColorsA();

    @Getter("colorsB")
    @Override
    public abstract int[] getColorsB();

    @Getter("colorsC")
    @Override
    public abstract int[] getColorsC();

    @Getter("indicesA")
    @Override
    public abstract int[] getIndicesA();

    @Getter("indicesB")
    @Override
    public abstract int[] getIndicesB();

    @Getter("indicesC")
    @Override
    public abstract int[] getIndicesC();

    @Getter("textureIds")
    @Override
    public abstract int[] getTextureIds();
}
