package dev.dennis.osfx.api;

public interface BufferProvider {
    void setRaster();

    int[] getPixels();

    void setPixels(int[] pixels);
}
