package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.AbstractFont;
import dev.dennis.osfx.api.Widget;
import dev.dennis.osfx.inject.mixin.*;
import dev.dennis.osfx.Callbacks;
import dev.dennis.osfx.api.BufferProvider;
import dev.dennis.osfx.api.Client;

@Mixin("Client")
public abstract class ClientMixin implements Client {
    @Shadow
    private static Client client;

    @Getter
    @Setter
    private Callbacks callbacks;

    @Getter
    @Setter
    private AbstractFont currentFont;

    @Copy("fillRectangle")
    private static void rs$fillRectangle(int x, int y, int width, int height, int rgb) {
        throw new UnsupportedOperationException();
    }

    @Replace("fillRectangle")
    public static void hd$fillRectangle(int x, int y, int width, int height, int rgb) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.fillRectangle(x, y, width, height, rgb)) {
            return;
        }
        rs$fillRectangle(x, y, width, height, rgb);
    }

    @Copy("fillRectangleAlpha")
    private static void rs$fillRectangleAlpha(int x, int y, int width, int height, int rgb, int alpha) {
        throw new UnsupportedOperationException();
    }

    @Replace("fillRectangleAlpha")
    public static void hd$fillRectangleAlpha(int x, int y, int width, int height, int rgb, int alpha) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.fillRectangle(x, y, width, height, rgb, alpha)) {
            return;
        }
        rs$fillRectangleAlpha(x, y, width, height, rgb, alpha);
    }

    @Copy("drawHorizontalLine")
    private static void rs$drawHorizontalLine(int x, int y, int width, int rgb) {
        throw new UnsupportedOperationException();
    }

    @Replace("drawHorizontalLine")
    public static void hd$drawHorizontalLine(int x, int y, int width, int rgb) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawHorizontalLine(x, y, width, rgb)) {
            return;
        }
        rs$drawHorizontalLine(x, y, width, rgb);
    }

    @Copy("drawHorizontalLineAlpha")
    private static void rs$drawHorizontalLineAlpha(int x, int y, int width, int rgb, int alpha) {
        throw new UnsupportedOperationException();
    }

    @Replace("drawHorizontalLineAlpha")
    public static void hd$drawHorizontalLineAlpha(int x, int y, int width, int rgb, int alpha) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawHorizontalLine(x, y, width, rgb, alpha)) {
            return;
        }
        rs$drawHorizontalLineAlpha(x, y, width, rgb, alpha);
    }

    @Copy("drawVerticalLine")
    private static void rs$drawVerticalLine(int x, int y, int height, int rgb) {
        throw new UnsupportedOperationException();
    }

    @Replace("drawVerticalLine")
    public static void hd$drawVerticalLine(int x, int y, int height, int rgb) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawVerticalLine(x, y, height, rgb)) {
            return;
        }
        rs$drawVerticalLine(x, y, height, rgb);
    }

    @Copy("drawVerticalLineAlpha")
    private static void rs$drawVerticalLineAlpha(int x, int y, int height, int rgb, int alpha) {
        throw new UnsupportedOperationException();
    }

    @Replace("drawVerticalLineAlpha")
    public static void hd$drawVerticalLineAlpha(int x, int y, int height, int rgb, int alpha) {
        Callbacks callbacks = client.getCallbacks();
        if (callbacks != null && callbacks.drawVerticalLine(x, y, height, rgb, alpha)) {
            return;
        }
        rs$drawVerticalLineAlpha(x, y, height, rgb, alpha);
    }

    @Static
    @Getter("bufferProvider")
    @Override
    public abstract BufferProvider getBufferProvider();

    @Static
    @Getter("gameDrawingMode")
    @Override
    public abstract int getGameDrawingMode();

    @Static
    @Setter("gameDrawingMode")
    @Override
    public abstract void setGameDrawingMode(int mode);

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

    @Static
    @Getter("graphicsPixels")
    @Override
    public abstract int[] getGraphicsPixels();

    @Static
    @Getter("viewportX")
    @Override
    public abstract int getViewportX();

    @Static
    @Getter("viewportY")
    @Override
    public abstract int getViewportY();

    @Static
    @Getter("viewportWidget")
    @Override
    public abstract Widget getViewportWidget();

    @Static
    @Getter("colorPalette")
    @Override
    public abstract int[] getColorPalette();
}
