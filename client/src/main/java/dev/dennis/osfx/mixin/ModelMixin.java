package dev.dennis.osfx.mixin;

import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.inject.mixin.*;
import dev.dennis.osfx.api.Model;

@Mixin("Model")
public abstract class ModelMixin implements Model {
    @Shadow
    private static Client client;

    @Copy("draw")
    public abstract void rs$draw(int rotation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z,
                                 long hash);

    @Replace("draw")
    public void hd$draw(int rotation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z,
                        long hash) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawModel(this, rotation, x, y, z)) {
            return;
        }
        rs$draw(rotation, pitchSin, pitchCos, yawSin, yawCos, x, y, z, hash);
    }

    @Copy("drawTriangle")
    public abstract void rs$drawTriangle(int index);

    @Replace("drawTriangle")
    public void hd$drawTriangle(int index) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawModelTriangle(this, index)) {
            return;
        }
        rs$drawTriangle(index);
    }

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

    @Getter("triangleCount")
    @Override
    public abstract int getTriangleCount();

    @Getter("indicesA")
    @Override
    public abstract int[] getIndicesA();

    @Getter("indicesB")
    @Override
    public abstract int[] getIndicesB();

    @Getter("indicesC")
    @Override
    public abstract int[] getIndicesC();

    @Getter("colorsA")
    @Override
    public abstract int[] getColorsA();

    @Getter("colorsB")
    @Override
    public abstract int[] getColorsB();

    @Getter("colorsC")
    @Override
    public abstract int[] getColorsC();

    @Getter("textureIndicesP")
    @Override
    public abstract int[] getTextureIndicesP();

    @Getter("textureIndicesM")
    @Override
    public abstract int[] getTextureIndicesM();

    @Getter("textureIndicesN")
    @Override
    public abstract int[] getTextureIndicesN();

    @Getter("triangleTextures")
    @Override
    public abstract short[] getTriangleTextures();

    @Getter("textureMapping")
    @Override
    public abstract byte[] getTextureMapping();

    @Getter("triangleAlphas")
    @Override
    public abstract byte[] getTriangleAlphas();

    @Getter("trianglePriorities")
    @Override
    public abstract byte[] getTrianglePriorities();
}
