package dev.dennis.osfx.api;

public interface Model {
    int getVertexCount();

    int[] getVerticesX();

    int[] getVerticesY();

    int[] getVerticesZ();

    int getTriangleCount();

    int[] getIndicesA();

    int[] getIndicesB();

    int[] getIndicesC();

    int[] getColorsA();

    int[] getColorsB();

    int[] getColorsC();

    short[] getTriangleTextures();

    byte[] getTextureMapping();

    byte[] getTriangleAlphas();

    byte[] getTrianglePriorities();
}
