package dev.dennis.osfx.render;

import dev.dennis.osfx.Renderer;

import static org.lwjgl.bgfx.BGFX.*;

public class RenderRectangleCommand implements RenderCommand {
    private final int x;

    private final int y;

    private final int width;

    private final int height;

    private final int rgb;

    private final int alpha;

    private final int scissorX;

    private final int scissorY;

    private final int scissorWidth;

    private final int scissorHeight;

    public RenderRectangleCommand(int x, int y, int width, int height, int rgb, int alpha, int scissorX, int scissorY,
                                  int scissorWidth, int scissorHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rgb = rgb;
        this.alpha = alpha;
        this.scissorX = scissorX;
        this.scissorY = scissorY;
        this.scissorWidth = scissorWidth;
        this.scissorHeight = scissorHeight;
    }

    @Override
    public void render(Renderer renderer, long encoder) {
        bgfx_encoder_set_scissor(encoder, scissorX, scissorY, scissorWidth, scissorHeight);
        bgfx_encoder_set_texture(encoder, 0, (short) 0, renderer.getWhiteTextureId(), BGFX_SAMPLER_NONE);
        long state = BGFX_STATE_WRITE_RGB | BGFX_STATE_WRITE_A | BGFX_STATE_BLEND_ALPHA;
        bgfx_encoder_set_state(encoder, state, 0);
        renderer.renderQuad(encoder, Renderer.UI_VIEW, renderer.getQuadProgram(), x, y, width, height, rgb, alpha);
    }

    @Override
    public void cleanup(Renderer renderer) {

    }
}
