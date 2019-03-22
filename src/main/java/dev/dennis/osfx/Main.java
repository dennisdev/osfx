package dev.dennis.osfx;

import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.util.OsrsConfig;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Path INJECTED_PATH = Paths.get("178_injected.jar");

    public static void main(String[] args) throws Exception {
        OsrsConfig config = OsrsConfig.load("http://oldschool83.runescape.com/jav_config.ws");

        ClassLoader classLoader = new URLClassLoader(new URL[] {INJECTED_PATH.toUri().toURL()});

        Class<?> clientClass = classLoader.loadClass("client");
        Client client = (Client) clientClass.newInstance();

        Renderer renderer = new Renderer(client, 800, 600);
        renderer.start(config);
    }
}
