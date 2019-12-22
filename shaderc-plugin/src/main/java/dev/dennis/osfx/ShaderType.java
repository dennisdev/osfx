package dev.dennis.osfx;

public enum ShaderType {
    VERTEX("vertex"),

    FRAGMENT("fragment"),

    COMPUTE("compute");

    private final String type;

    ShaderType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
