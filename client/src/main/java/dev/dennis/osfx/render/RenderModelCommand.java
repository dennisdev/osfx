package dev.dennis.osfx.render;

import dev.dennis.osfx.api.Model;

import java.nio.ByteBuffer;

public class RenderModelCommand {
    private final Model model;

    private final int rotation;

    private final int x;

    private final int y;

    private final int z;

    private final int vertexStart;

    private final int vertexCount;

    public RenderModelCommand(Model model, int rotation, int x, int y, int z, int vertexStart, int vertexCount) {
        this.model = model;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vertexStart = vertexStart;
        this.vertexCount = vertexCount;
    }

    public Model getModel() {
        return model;
    }

    public int getRotation() {
        return rotation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getVertexStart() {
        return vertexStart;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
