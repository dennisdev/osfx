package dev.dennis.osfx.api;

public interface BufferProvider {
    int[] getPixels();

    void setPixels(int[] pixels);
}
