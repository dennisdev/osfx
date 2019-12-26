package dev.dennis.osfx.render;

import dev.dennis.osfx.Renderer;

import java.nio.IntBuffer;

import static org.lwjgl.bgfx.BGFX.*;

public class RenderSpriteCommand implements RenderCommand {
    private final IntBuffer pixelsBuf;

    private final int spriteWidth;

    private final int spriteHeight;

    private final int x;

    private final int y;

    private final int width;

    private final int height;

    private final int alpha;

    private final int scissorX;

    private final int scissorY;

    private final int scissorWidth;

    private final int scissorHeight;

    public RenderSpriteCommand(IntBuffer pixelsBuf, int spriteWidth, int spriteHeight, int x, int y,
                               int width, int height, int alpha, int scissorX, int scissorY,
                               int scissorWidth, int scissorHeight) {
        this.pixelsBuf = pixelsBuf;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.alpha = alpha;
        this.scissorX = scissorX;
        this.scissorY = scissorY;
        this.scissorWidth = scissorWidth;
        this.scissorHeight = scissorHeight;
    }

    @Override
    public void render(Renderer renderer, long encoder) {
        short textureId = bgfx_create_texture_2d(spriteWidth, spriteHeight, false, 1,
                BGFX_TEXTURE_FORMAT_BGRA8, BGFX_TEXTURE_NONE, bgfx_make_ref(pixelsBuf));
        renderer.getTexturesToRemove().add(textureId);

        bgfx_encoder_set_scissor(encoder, scissorX, scissorY, scissorWidth, scissorHeight);
        bgfx_encoder_set_texture(encoder, 0, (short) 0, textureId, BGFX_SAMPLER_NONE);
        long state = BGFX_STATE_WRITE_RGB | BGFX_STATE_WRITE_A | BGFX_STATE_BLEND_ALPHA;
        bgfx_encoder_set_state(encoder, state, 0);
        renderer.renderQuad(encoder, 1, renderer.getQuadProgram(), x, y, width, height, 0xFFFFFF, alpha);
    }

    @Override
    public void cleanup(Renderer renderer) {
        renderer.getBuffersToRemove().add(pixelsBuf);
    }

    public IntBuffer getPixelsBuf() {
        return pixelsBuf;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getScissorX() {
        return scissorX;
    }

    public int getScissorY() {
        return scissorY;
    }

    public int getScissorWidth() {
        return scissorWidth;
    }

    public int getScissorHeight() {
        return scissorHeight;
    }
}
