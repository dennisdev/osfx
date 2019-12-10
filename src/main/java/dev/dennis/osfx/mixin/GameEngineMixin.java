package dev.dennis.osfx.mixin;

import dev.dennis.mixin.*;
import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.GameEngine;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.*;
import java.net.URL;

@Mixin("GameEngine")
public abstract class GameEngineMixin implements GameEngine {
    @Shadow
    private static Client client;

    @Getter
    @Setter
    private AppletStub stub;

    @Getter("canvas")
    @Override
    public abstract Canvas getCanvas();

    public String getParameter(String key) {
        return stub.getParameter(key);
    }

    public URL getDocumentBase() {
        return stub.getDocumentBase();
    }

    public URL getCodeBase() {
        return stub.getCodeBase();
    }

    public AppletContext getAppletContext() {
        return stub.getAppletContext();
    }

    @Inject("post")
    private void onFrameEnd() {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null) {
            callbacks.onFrameEnd();
        }
    }

    @Copy("setupClipboard")
    public abstract void rs$setupClipboard();
}
