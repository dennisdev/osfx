package dev.dennis.osfx.render;

import dev.dennis.osfx.Renderer;
import dev.dennis.osfx.api.Client;
import dev.dennis.osfx.api.Widget;

import static org.lwjgl.bgfx.BGFX.*;
import static org.lwjgl.bgfx.BGFX.bgfx_encoder_set_state;

public class RenderSceneCommand implements RenderCommand {
    @Override
    public void render(Renderer renderer, long encoder) {
        Client client = renderer.getClient();
        Widget viewportWidget = client.getViewportWidget();
        int viewportWidth = viewportWidget.getWidth();
        int viewportHeight = viewportWidget.getHeight();

        short frameBufferTextureId = bgfx_get_texture(renderer.getFrameBufferId(), 0);

        long state = BGFX_STATE_WRITE_RGB | BGFX_STATE_WRITE_A | BGFX_STATE_BLEND_ALPHA;
        bgfx_encoder_set_texture(encoder, 0, (short) 0, frameBufferTextureId, BGFX_SAMPLER_NONE);
        bgfx_encoder_set_state(encoder, state, 0);
        if (renderer.getBgfxCaps().originBottomLeft()) {
            renderer.renderQuad(encoder, Renderer.UI_VIEW, renderer.getQuadProgram(),
                    client.getViewportX(), client.getViewportY(), viewportWidth, viewportHeight,
                    0xFFFFFF, 0xFF, 0.0f, 1.0f, 1.0f, 0.0f);
        } else {
            renderer.renderQuad(encoder, Renderer.UI_VIEW, renderer.getQuadProgram(),
                    client.getViewportX(), client.getViewportY(), viewportWidth, viewportHeight,
                    0xFFFFFF, 0xFF);
        }
    }

    @Override
    public void cleanup(Renderer renderer) {

    }
}
