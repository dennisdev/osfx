package dev.dennis.osfx.mixin;

import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;
import dev.dennis.osfx.inject.mixin.Setter;
import dev.dennis.osfx.inject.mixin.Static;
import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.BufferProvider;
import dev.dennis.osfx.api.Client;

@Mixin("Client")
public abstract class ClientMixin implements Client {
    @Getter
    @Setter
    private Callbacks callbacks;

    @Static
    @Getter("bufferProvider")
    @Override
    public abstract BufferProvider getBufferProvider();

    @Static
    @Getter("cameraPitch")
    @Override
    public abstract int getCameraPitch();

    @Static
    @Getter("cameraYaw")
    @Override
    public abstract int getCameraYaw();

    @Static
    @Getter("cameraZoom")
    @Override
    public abstract int getCameraZoom();

    @Static
    @Getter("scissorX")
    @Override
    public abstract int getScissorX();

    @Static
    @Getter("scissorY")
    @Override
    public abstract int getScissorY();

    @Static
    @Getter("scissorEndX")
    @Override
    public abstract int getScissorEndX();

    @Static
    @Getter("scissorEndY")
    @Override
    public abstract int getScissorEndY();

    @Override
    public int getScissorWidth() {
        return getScissorEndX() - getScissorX();
    }

    @Override
    public int getScissorHeight() {
        return getScissorEndY() - getScissorY();
    }
}
