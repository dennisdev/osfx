package dev.dennis.osfx.api;

import java.applet.AppletStub;
import java.awt.*;
import java.awt.event.FocusListener;
import java.awt.event.WindowListener;

public interface GameEngine extends Runnable, FocusListener, WindowListener {
    void setSize(int width, int height);

    AppletStub getStub();

    void setStub(AppletStub stub);

    String getParameter(String key);

    void init();

    void start();

    void stop();

    void destroy();

    Canvas getCanvas();
}
