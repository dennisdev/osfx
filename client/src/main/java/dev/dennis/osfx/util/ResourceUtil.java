package dev.dennis.osfx.util;

import org.lwjgl.system.MemoryUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class ResourceUtil {
    public static ByteBuffer loadResource(String resourcePath, String name) throws IOException {
        URL url = ResourceUtil.class.getClassLoader().getResource(resourcePath + "/" + name);

        if (url == null) {
            throw new IOException("Resource not found: " + resourcePath + "/" + name);
        }

        int resourceSize = url.openConnection().getContentLength();

        ByteBuffer resource = MemoryUtil.memAlloc(resourceSize);
        try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
            int b;
            while ((b = bis.read()) != -1) {
                resource.put((byte) b);
            }
        }
        resource.flip();

        return resource;
    }
}
