package dev.dennis.osfx.mixin;

import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.GameEngine;
import dev.dennis.osfx.inject.mixin.*;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.net.URL;

@Mixin("GameEngine")
public abstract class GameEngineMixin implements GameEngine {
    @Shadow
    private static Client client;

    @Getter
    @Setter
    private AppletStub stub;

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

    @Replace("setupClipboard")
    public void hd$setupClipboard() {
        if (!GraphicsEnvironment.isHeadless()) {
            rs$setupClipboard();
        }
    }

    @Copy("setClipboardContents")
    public abstract void rs$setClipboardContents(String text);

    @Replace("setClipboardContents")
    public void hd$setClipboardContents(String text) {
        if (getClipboard() != null) {
            rs$setClipboardContents(text);
        }
    }

    @Getter("canvas")
    @Override
    public abstract Canvas getCanvas();

    @Getter("clipboard")
    @Override
    public abstract Clipboard getClipboard();

    @Getter("replaceCanvasNextFrame")
    @Override
    public abstract boolean isReplaceCanvasNextFrame();

    @Setter("replaceCanvasNextFrame")
    @Override
    public abstract void setReplaceCanvasNextFrame(boolean replace);
}
