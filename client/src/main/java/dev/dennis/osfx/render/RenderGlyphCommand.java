package dev.dennis.osfx.render;

import dev.dennis.osfx.Renderer;
import dev.dennis.osfx.api.Font;
import dev.dennis.osfx.util.MathUtil;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.bgfx.BGFX.*;

public class RenderGlyphCommand implements RenderCommand {
    private static final int GLYPH_COUNT = 256;

    private static final int GLYPH_ROWS = (int) Math.sqrt(GLYPH_COUNT);

    private final Font font;

    private final int glyphId;

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

    private IntBuffer pixelsBuf;

    public RenderGlyphCommand(Font font, int glyphId, int x, int y, int width, int height, int rgb, int alpha,
                              int scissorX, int scissorY, int scissorWidth, int scissorHeight) {
        this.font = font;
        this.glyphId = glyphId;
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

    private void createTexture() {
        byte[][] glyphs = font.getGlyphs();
        int[] glyphWidths = font.getGlyphWidths();
        int[] glyphHeights = font.getGlyphHeights();

        int textureSize = 0;
        for (int i = 0; i < GLYPH_COUNT; i++) {
            if (glyphWidths[i] > textureSize) {
                textureSize = glyphWidths[i];
            }
            if (glyphHeights[i] > textureSize) {
                textureSize = glyphHeights[i];
            }
        }

        textureSize *= GLYPH_ROWS;
        textureSize = MathUtil.getNextPowerOfTwo(textureSize);

        int widthPerGlyph = textureSize / GLYPH_ROWS;
        int[] pixels = new int[textureSize * textureSize];

        for (int i = 0; i < GLYPH_COUNT; i++) {
            int x = i % GLYPH_ROWS * widthPerGlyph;
            int y = i / GLYPH_ROWS * widthPerGlyph;
            int pixelIndex = (y * textureSize + x);
            int glyphWidth = glyphWidths[i];
            int glyphHeight = glyphHeights[i];
            byte[] glyph = glyphs[i];
            int glyphIndex = 0;
            for (int glyphY = 0; glyphY < glyphHeight; glyphY++) {
                for (int glyphX = 0; glyphX < glyphWidth; glyphX++) {
                    if (glyph[glyphIndex++] != 0) {
                        pixels[pixelIndex++] = 0xFFFFFFFF;
                    } else {
                        pixelIndex++;
                    }
                }
                pixelIndex += (textureSize - glyphWidth);
            }
        }

        pixelsBuf = MemoryUtil.memAllocInt(textureSize * textureSize);
        pixelsBuf.put(pixels);
        pixelsBuf.flip();

        short textureId = bgfx_create_texture_2d(textureSize, textureSize, false, 1,
                BGFX_TEXTURE_FORMAT_BGRA8, BGFX_TEXTURE_NONE, bgfx_make_ref(pixelsBuf));

        font.setTextureId(textureId);
        font.setTextureSize(textureSize);
    }

    @Override
    public void render(Renderer renderer, long encoder) {
        if (font.getTextureId() == -1) {
            createTexture();
        }

        bgfx_encoder_set_scissor(encoder, scissorX, scissorY, scissorWidth, scissorHeight);
        bgfx_encoder_set_texture(encoder, 0, (short) 0, font.getTextureId(), BGFX_SAMPLER_NONE);
        long state = BGFX_STATE_WRITE_RGB | BGFX_STATE_WRITE_A | BGFX_STATE_BLEND_ALPHA;
        bgfx_encoder_set_state(encoder, state, 0);

        float minU = glyphId % GLYPH_ROWS / 16.0f;
        float minV = glyphId / GLYPH_ROWS / 16.0f;
        float maxU = minU;
        float maxV = minV;
        maxU += (float) width / (float) font.getTextureSize();
        maxV += (float) height / (float) font.getTextureSize();

        renderer.renderQuad(encoder, 1, renderer.getQuadProgram(), x, y, width, height, rgb, alpha,
                minU, minV, maxU, maxV);
    }

    @Override
    public void cleanup(Renderer renderer) {
        if (pixelsBuf != null) {
            renderer.getBuffersToRemove().add(pixelsBuf);
        }
    }
}
