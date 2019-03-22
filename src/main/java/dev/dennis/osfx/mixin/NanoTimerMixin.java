package dev.dennis.osfx.mixin;

import dev.dennis.mixin.Inject;
import dev.dennis.mixin.Mixin;
import dev.dennis.mixin.Shadow;
import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.Timer;

@Mixin("NanoTimer")
public abstract class NanoTimerMixin implements Timer {
    @Shadow
    private static Client client;

    @Inject(value = "sleep", end = true)
    public void onFrameStart() {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null) {
            callbacks.onFrameStart();
        }
    }
}
