package dev.dennis.osfx.util;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.net.URL;

public class OsrsAppletStub implements AppletStub {
    private final OsrsConfig config;

    public OsrsAppletStub(OsrsConfig config) {
        this.config = config;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public URL getDocumentBase() {
        return getCodeBase();
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URL(config.getCodebase());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public String getParameter(String name) {
        return config.getAppletProperty(name);
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public void appletResize(int width, int height) {

    }
}
