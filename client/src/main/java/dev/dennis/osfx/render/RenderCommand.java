package dev.dennis.osfx.render;

import dev.dennis.osfx.Renderer;

public interface RenderCommand {
    void render(Renderer renderer, long encoder);

    void cleanup(Renderer renderer);
}
