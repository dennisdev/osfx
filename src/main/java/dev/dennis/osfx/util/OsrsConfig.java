package dev.dennis.osfx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OsrsConfig {
    private final Map<String, String> appletProperties;

    private final Map<String, String> classLoaderProperties;

    public static OsrsConfig load(String url) throws IOException {
        return load(new URL(url));
    }

    public static OsrsConfig load(URL url) throws IOException {
        return load(new InputStreamReader(url.openStream()));
    }

    public static OsrsConfig load(Reader reader) throws IOException {
        Map<String, String> appletProperties = new HashMap<>();
        Map<String, String> classLoaderProperties = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.lines().forEach(line -> {
                if (line.startsWith("param=")) {
                    String pair = line.substring(6);
                    String[] split = pair.split("=", 2);
                    if (split.length == 1) {
                        appletProperties.put(split[0], "");
                    } else {
                        appletProperties.put(split[0], split[1]);
                    }
                } else if (!line.startsWith("msg=")) {
                    String[] split = line.split("=", 2);
                    classLoaderProperties.put(split[0], split[1]);
                }
            });
        }

        return new OsrsConfig(appletProperties, classLoaderProperties);
    }

    public OsrsConfig(Map<String, String> appletProperties, Map<String, String> classLoaderProperties) {
        this.appletProperties = appletProperties;
        this.classLoaderProperties = classLoaderProperties;
    }

    public String getAppletProperty(String key) {
        return appletProperties.get(key);
    }

    public String getCodebase() {
        return classLoaderProperties.get("codebase");
    }

}
