package dev.dennis.osfx.mixin;

import dev.dennis.mixin.Getter;
import dev.dennis.mixin.Mixin;
import dev.dennis.mixin.Setter;
import dev.dennis.mixin.Static;
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
}
