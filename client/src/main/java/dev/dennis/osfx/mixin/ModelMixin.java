package dev.dennis.osfx.mixin;

import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;
import dev.dennis.osfx.api.Model;

@Mixin("Model")
public abstract class ModelMixin implements Model {
    @Getter("vertexCount")
    @Override
    public abstract int getVertexCount();

    @Getter("verticesX")
    @Override
    public abstract int[] getVerticesX();

    @Getter("verticesY")
    @Override
    public abstract int[] getVerticesY();

    @Getter("verticesZ")
    @Override
    public abstract int[] getVerticesZ();
}
