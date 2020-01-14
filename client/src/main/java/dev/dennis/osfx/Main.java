package dev.dennis.osfx;

import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.util.OsrsConfig;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Path INJECTED_PATH = Paths.get("187_injected.jar");

    private static final boolean LOAD_NEW_CONFIG = false;

    private static final boolean LWJGL_DEBUG = true;

    private static final boolean BGFX_DEBUG = true;

    private static final String BGFX_DEBUG_DLL_NAME = "bgfx_debug.dll";

    public static void main(String[] args) throws Exception {
        if (LWJGL_DEBUG) {
            System.setProperty("org.lwjgl.util.Debug", "true");
        }
        if (BGFX_DEBUG && Files.exists(Paths.get(BGFX_DEBUG_DLL_NAME))) {
            System.setProperty("org.lwjgl.bgfx.libname", "bgfx_debug.dll");
        }

        OsrsConfig config;
        if (LOAD_NEW_CONFIG) {
            config = OsrsConfig.load("http://oldschool83.runescape.com/jav_config.ws");
        } else {
            config = OsrsConfig.load(Main.class.getResource("/jav_config.ws"));
        }

        ClassLoader classLoader = new URLClassLoader(new URL[] {INJECTED_PATH.toUri().toURL()});

        Class<?> clientClass = classLoader.loadClass("client");
        Client client = (Client) clientClass.newInstance();

        Renderer renderer = new Renderer(client, 800, 600);
        renderer.start(config);
    }
}
