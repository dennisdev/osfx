package dev.dennis.osfx;

import dev.dennis.osfx.api.*;
import dev.dennis.osfx.render.*;
import dev.dennis.osfx.util.KeyMapping;
import dev.dennis.osfx.util.OsrsAppletStub;
import dev.dennis.osfx.util.OsrsConfig;
import dev.dennis.osfx.util.ResourceUtil;
import org.joml.Matrix4f;
import org.lwjgl.bgfx.*;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.libc.LibCStdio;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import static org.lwjgl.bgfx.BGFX.*;
import static org.lwjgl.bgfx.BGFX.bgfx_shutdown;
import static org.lwjgl.bgfx.BGFXPlatform.bgfx_set_platform_data;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.memASCII;

public class Renderer implements Callbacks {
    private static final boolean DEBUG = false;

    private static final String TITLE = "OSFX";

    private static final int RESET = BGFX_RESET_NONE;

    private static final int MIN_WIDTH = 765;

    private static final int MIN_HEIGHT = 503;

    private static final int MAX_WIDTH = 7680;

    private static final int MAX_HEIGHT = 2160;

    private static final BGFXReleaseFunctionCallback releaseMemoryCb = BGFXReleaseFunctionCallback.create(
            (ptr, userData) -> MemoryUtil.nmemFree(ptr)
    );

    private static final int DRAW_FULL_MODE = 2;

    private static final float PI = (float) Math.PI;

    private static final float TAU = PI * 2f;

    private static final float SHORT_TO_RADIANS = TAU / 65536.0f;

    private static final float SHORT_TO_DEGREES = 360f / 65536.0f;

    private static final float DEGREES_TO_RADIANS = TAU / 360.0f;

    private static final float RS_TO_RADIANS = TAU / 2048.0f;

    private static final float NEAR = 50f;

    private static final float FAR = 4000f;

    private static final float DEFAULT_ZOOM = 25.0f / 256.0f;

    public static final int WHITE_RGBA = 0xFFFFFFFF;

    private static final int SMALL_TEXTURE_SIZE = 128;

    private static final int TEXTURE_SIZE = 128;

    public static final int BACKGROUND_VIEW = 0;

    public static final int UI_VIEW = 1;

    public static final int SCENE_VIEW = 2;

    private final Client client;

    private final CyclicBarrier barrier;

    private int width;

    private int height;

    private long window;

    private int rendererType;

    private int format;

    private BGFXCaps bgfxCaps;

    private int lastMouseX;

    private int lastMouseY;

    private int frame;

    private final List<Buffer> buffersToRemove;

    private final List<Short> texturesToRemove;

    private final List<Short> vertexBuffersToRemove;

    private final List<RenderCommand> renderCommands;

    private final List<RenderModelCommand> renderModelCommands;

    private Dimension lastCanvasSize;

    private IntBuffer fullscreenTextureBuf;

    private IntBuffer whiteTextureBuf;

    private BGFXVertexLayout layout;

    private BGFXVertexLayout sceneLayout;

    private short quadProgram;

    private short sceneProgram;

    private short fullscreenTextureId;

    private short whiteTextureId;

    private short textureArrayId;

    private FloatBuffer matrixBuf;

    private final Matrix4f matrix;

    private short frameBufferId;

    private int lastViewportWidth;

    private int lastViewportHeight;

    private final ByteBuffer[] vertexBuffers;

    private short vertexBufferId;

    private int vertexCount;

    private static boolean isPointOnCanvas(Canvas canvas, int x, int y) {
        return x >= canvas.getX() && y >= canvas.getY()
                && x <= canvas.getX() + canvas.getWidth() && y <= canvas.getY() + canvas.getHeight();
    }

    private static Point translateToCanvas(Canvas canvas, int x, int y) {
        return new Point(x - canvas.getX(), y - canvas.getY());
    }

    private static BGFXCallbackInterface createBgfxCallbacks(MemoryStack stack) {
        return BGFXCallbackInterface.callocStack(stack)
                .vtbl(BGFXCallbackVtbl.callocStack(stack)
                        .fatal((_this, _filePath, _line, _code, _str) -> {
                            if (_code == BGFX_FATAL_DEBUG_CHECK) {
                                System.out.println("BREAK"); // set debugger breakpoint
                            } else {
                                throw new RuntimeException("Fatal error " + _code + ": " + memASCII(_str));
                            }
                        })
                        .trace_vargs((_this, _filePath, _line, _format, _argList) -> {
                            try (MemoryStack frame = MemoryStack.stackPush()) {
                                String filePath = (_filePath != NULL) ? memUTF8(_filePath) : "[n/a]";

                                ByteBuffer buffer = frame.malloc(256); // arbitrary size to store formatted message
                                int length = LibCStdio.nvsnprintf(memAddress(buffer), buffer.remaining(), _format, _argList);
                                if (length > 0) {
                                    String message = memASCII(buffer, length - 1); // bgfx log messages are terminated with the newline character
                                    if (message.contains("Texture")) {
                                        return;
                                    }
                                    System.out.println("bgfx: [" + filePath + " (" + _line + ")] - " + message);
                                } else {
                                    System.out.println("bgfx: [" + filePath + " (" + _line + ")] - error: unable to format output: " + memASCII(_format));
                                }
                            }
                        })
                        .profiler_begin((_this, _name, _abgr, _filePath, _line) -> {

                        })
                        .profiler_begin_literal((_this, _name, _abgr, _filePath, _line) -> {

                        })
                        .profiler_end(_this -> {

                        })
                        .cache_read_size((_this, _id) -> 0)
                        .cache_read((_this, _id, _data, _size) -> false)
                        .cache_write((_this, _id, _data, _size) -> {

                        })
                        .screen_shot((_this, _filePath, _width, _height, _pitch, _data, _size, _yflip) -> {
                            System.out.println("screenshot");
                        })
                        .capture_begin((_this, _width, _height, _pitch, _format, _yflip) -> {
                            System.out.println("capture_begin");
                        })
                        .capture_end(_this -> {
                            System.out.println("capture_end");
                        })
                        .capture_frame((_this, _data, _size) -> {
                            System.out.println("capture_frame");
                        })
                );
    }

    private static void addSceneVertex(ByteBuffer vertex, int x, int y, int z, int rgb, int alpha, float u, float v,
                                       int textureId) {
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;

        vertex.putFloat(x);
        vertex.putFloat(y);
        vertex.putFloat(z);
        vertex.putInt(alpha << 24 | b << 16 | g << 8 | r);
        vertex.putFloat(u);
        vertex.putFloat(v);
        vertex.putFloat(textureId);
    }

    public Renderer(Client client, int width, int height) {
        this.client = client;
        this.barrier = new CyclicBarrier(2);
        this.width = width;
        this.height = height;
        this.buffersToRemove = new ArrayList<>();
        this.texturesToRemove = new ArrayList<>();
        this.vertexBuffersToRemove = new ArrayList<>();
        this.renderCommands = new ArrayList<>();
        this.renderModelCommands = new ArrayList<>();
        this.fullscreenTextureId = -1;
        this.whiteTextureId = -1;
        this.textureArrayId = -1;
        this.matrix = new Matrix4f();
        this.frameBufferId = -1;
        this.vertexBufferId = -1;
        this.vertexBuffers = new ByteBuffer[2];
    }

    private void startClient(OsrsConfig config) {
        client.setCallbacks(this);
        client.setStub(new OsrsAppletStub(config));
        client.setSize(width, height);
        client.setGameDrawingMode(DRAW_FULL_MODE);
        client.init();
        client.start();
    }

    private void stopClient() {
        // This blocks for 5 seconds
        client.stop();
        client.destroy();
    }

    public void start(OsrsConfig config) {
        startClient(config);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            init(stack);

            // shouldClose && client not crashed
            while (!glfwWindowShouldClose(window)) {
                render();
            }

            // data is managed from main thread, and it's passed to renderer
            // just as MemoryRef. At this point the renderer might be using it. We must wait
            // for the previous frame to finish before we can free it.
            bgfx_frame(false);

            destroy();
        }
    }

    private void init(MemoryStack stack) {
        initWindow();
        initBgfx(stack);
        initRenderer(stack);
    }

    private void initWindow() {
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        // the client (renderer) API is managed by bgfx
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

        window = glfwCreateWindow(width, height, TITLE, NULL, NULL);

        if (window == NULL) {
            throw new RuntimeException("Error creating GLFW window");
        }

        glfwSetWindowSizeLimits(window, MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT);

        setGlfwCallbacks();
    }

    private void initBgfx(MemoryStack stack) {
        BGFXPlatformData platformData = BGFXPlatformData.callocStack(stack);

        switch (Platform.get()) {
            case LINUX:
                platformData.ndt(GLFWNativeX11.glfwGetX11Display());
                platformData.nwh(GLFWNativeX11.glfwGetX11Window(window));
                break;
            case MACOSX:
                platformData.ndt(NULL);
                platformData.nwh(GLFWNativeCocoa.glfwGetCocoaWindow(window));
                break;
            case WINDOWS:
                platformData.ndt(NULL);
                platformData.nwh(GLFWNativeWin32.glfwGetWin32Window(window));
                break;
        }

        platformData.context(NULL);
        platformData.backBuffer(NULL);
        platformData.backBufferDS(NULL);

        bgfx_set_platform_data(platformData);

        BGFXInit init = BGFXInit.mallocStack(stack);
        bgfx_init_ctor(init);
        init.type(BGFX_RENDERER_TYPE_COUNT)
                .callback(createBgfxCallbacks(stack))
                .resolution(it -> it
                        .reset(RESET)
                        .width(width)
                        .height(height));

        if (!bgfx_init(init)) {
            throw new RuntimeException("Failed to initialize BGFX");
        }

        bgfxCaps = bgfx_get_caps();

        rendererType = bgfxCaps.rendererType();
        String rendererName = bgfx_get_renderer_name(rendererType);
        System.out.println("Using renderer: " + rendererName);

        format = init.resolution().format();
    }

    private void initRenderer(MemoryStack stack) {
        if (DEBUG) {
            bgfx_set_debug(BGFX_DEBUG_TEXT | BGFX_DEBUG_STATS);
        }

        bgfx_set_view_clear(BACKGROUND_VIEW,
                BGFX_CLEAR_COLOR | BGFX_CLEAR_DEPTH,
                0x000000FF,
                1.0f,
                0);

        bgfx_set_view_clear(UI_VIEW,
                BGFX_CLEAR_COLOR | BGFX_CLEAR_DEPTH,
                0x000000FF,
                1.0f,
                0);

        bgfx_set_view_clear(SCENE_VIEW,
                BGFX_CLEAR_COLOR | BGFX_CLEAR_DEPTH,
                0x000000FF,
                1.0f,
                0);

        BGFXTextureInfo textureInfo = BGFXTextureInfo.mallocStack(stack);
        bgfx_calc_texture_size(textureInfo, MAX_WIDTH, MAX_HEIGHT, 1, false, false,
                1, BGFX_TEXTURE_FORMAT_BGRA8);
        fullscreenTextureBuf = MemoryUtil.memAllocInt(textureInfo.storageSize() / 4 + 1);
        whiteTextureBuf = MemoryUtil.memAllocInt(1);
        whiteTextureBuf.put(WHITE_RGBA);
        whiteTextureBuf.flip();
        whiteTextureId = bgfx_create_texture_2d(1, 1, false, 1,
                BGFX_TEXTURE_FORMAT_BGRA8, BGFX_TEXTURE_NONE, bgfx_make_ref(whiteTextureBuf));

        quadProgram = createProgram("vs_quad", "fs_quad");
        sceneProgram = createProgram("vs_scene", "fs_scene");

        layout = createVertexLayout(false, true, true, false);
        sceneLayout = createVertexLayout(false, true, true, true);

        matrixBuf = MemoryUtil.memAllocFloat(16);

        int initialTriangleCount = 2 << 17;
        for (int i = 0; i < vertexBuffers.length; i++) {
            vertexBuffers[i] = MemoryUtil.memAlloc(initialTriangleCount * 3 * sceneLayout.stride());
        }
    }

    private void initTextureArray() {
        TextureProvider textureProvider = client.getTextureProvider();
        if (textureProvider == null) {
            return;
        }
        Texture[] textures = textureProvider.getTextures();

        int textureCount = textures.length + 1;

        textureArrayId = bgfx_create_texture_2d(TEXTURE_SIZE, TEXTURE_SIZE, false, textureCount,
                BGFX_TEXTURE_FORMAT_BGRA8, BGFX_TEXTURE_NONE, null);

        initDefaultTexture();

        updateTextures();
    }

    private void initDefaultTexture() {
        int[] pixels = new int[TEXTURE_SIZE * TEXTURE_SIZE];
        Arrays.fill(pixels, WHITE_RGBA);
        updateTexture(0, pixels);
    }

    private void updateTextures() {
        TextureProvider textureProvider = client.getTextureProvider();
        if (textureProvider == null) {
            return;
        }
        Texture[] textures = textureProvider.getTextures();
        for (int id = 0; id < textures.length; id++) {
            Texture texture = textures[id];
            if (texture != null && !texture.isLoaded()) {
                int[] pixels = textureProvider.load(id);
                if (pixels == null) {
                    continue;
                }
                updateTexture(id + 1, pixels);
            }
        }
    }

    private void updateTexture(int layer, int[] pixels) {
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != 0) {
                pixels[i] |= 0xFF << 24;
            }
        }
        int size = pixels.length == 4096 ? SMALL_TEXTURE_SIZE : TEXTURE_SIZE;
        IntBuffer pixelBuffer = MemoryUtil.memAllocInt(pixels.length);
        pixelBuffer.put(pixels);
        pixelBuffer.flip();
        buffersToRemove.add(pixelBuffer);
        bgfx_update_texture_2d(textureArrayId, layer, 0, 0, 0, size, size,
                bgfx_copy(pixelBuffer), 0xFFFF);
    }

    private void destroy() {
        destroyBgfx();
        destroyWindow();

        stopClient();

        System.out.println("Destroyed");
    }

    private void destroyWindow() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();

        System.out.println("GLFW Terminated");
    }

    private void destroyBgfx() {
        MemoryUtil.memFree(fullscreenTextureBuf);
        MemoryUtil.memFree(whiteTextureBuf);
        MemoryUtil.memFree(matrixBuf);

        layout.free();
        sceneLayout.free();

        bgfx_destroy_texture(fullscreenTextureId);
        bgfx_destroy_texture(whiteTextureId);
        if (textureArrayId != -1) {
            bgfx_destroy_texture(textureArrayId);
        }

        bgfx_destroy_program(quadProgram);
        bgfx_destroy_program(sceneProgram);

        bgfx_shutdown();

        System.out.println("BGFX Shutdown");
    }

    private void render() {
        sync();

        // Start of frame
        glfwPollEvents();

        for (Buffer buf : buffersToRemove) {
            MemoryUtil.memFree(buf);
        }
        buffersToRemove.clear();

        for (short textureId : texturesToRemove) {
            bgfx_destroy_texture(textureId);
        }
        texturesToRemove.clear();

        for (short id : vertexBuffersToRemove) {
            bgfx_destroy_vertex_buffer(id);
        }
        vertexBuffersToRemove.clear();

        for (RenderCommand command : renderCommands) {
            command.cleanup(this);
        }
        renderCommands.clear();

        renderModelCommands.clear();

        vertexBuffers[frame % 2].clear();
        vertexCount = 0;

        sync();

        // Client is drawing

        sync();

        // End of frame

        Canvas canvas = client.getCanvas();

        bgfx_set_view_rect(BACKGROUND_VIEW, 0, 0, width, height);
        bgfx_set_view_rect(UI_VIEW, canvas.getX(), canvas.getY(), width, height);

        matrix.setOrthoLH(0.0f, width, height, 0.0f, 0.0f, 1.0f, !bgfxCaps.homogeneousDepth());
        matrix.get(matrixBuf);
        bgfx_set_view_transform(UI_VIEW, null, matrixBuf);

        long encoder = bgfx_encoder_begin(false);

        renderFullscreenTexture(encoder);

        Widget viewportWidget = client.getViewportWidget();
        if (viewportWidget != null) {
            int viewportWidth = viewportWidget.getWidth();
            int viewportHeight = viewportWidget.getHeight();
            if (frameBufferId == -1 || lastViewportWidth != viewportWidth || lastViewportHeight != viewportHeight) {
                if (frameBufferId != -1) {
                    bgfx_destroy_frame_buffer(frameBufferId);
                }
                short[] attachments = new short[2];

                attachments[0] = bgfx_create_texture_2d(viewportWidth, viewportHeight, false, 1,
                        BGFX_TEXTURE_FORMAT_RGBA32F, BGFX_TEXTURE_RT, null);
                attachments[1] = bgfx_create_texture_2d(viewportWidth, viewportHeight, false, 1,
                        BGFX_TEXTURE_FORMAT_D16, BGFX_TEXTURE_RT_WRITE_ONLY, null);

                frameBufferId = bgfx_create_frame_buffer_from_handles(attachments, true);

                lastViewportWidth = viewportWidth;
                lastViewportHeight = viewportHeight;
            }
            bgfx_set_view_frame_buffer(SCENE_VIEW, frameBufferId);
            bgfx_set_view_rect(SCENE_VIEW, 0, 0, viewportWidth, viewportHeight);

            int centerX = viewportWidth / 2;
            int centerY = viewportHeight / 2;
            int pitch = client.getCameraPitch();
            int yaw = client.getCameraYaw();
            int zoom = client.getCameraZoom();
            setFrustumMatrix(0, 0, centerX, centerY, viewportWidth, viewportHeight,
                    pitch, yaw, zoom);

            bgfx_set_view_transform(SCENE_VIEW, null, matrix.get(matrixBuf));

            bgfx_encoder_set_scissor(encoder, 0, 0, width, height);

            if (textureArrayId == -1) {
                initTextureArray();
            } else {
                updateTextures();
            }

            if (vertexBufferId != -1) {
                vertexBuffersToRemove.add(vertexBufferId);
            }

            ByteBuffer vertexBuffer = vertexBuffers[frame % 2];
            vertexBuffer.flip();
            vertexBufferId = bgfx_create_vertex_buffer(bgfx_make_ref(vertexBuffer), sceneLayout, 0);

            for (RenderModelCommand command : renderModelCommands) {
                if (command.getVertexCount() == 0) {
                    continue;
                }

                matrix.identity().translate(command.getX(), command.getY(), command.getZ())
                        .rotateY(command.getRotation() * RS_TO_RADIANS);
                bgfx_encoder_set_transform(encoder, matrix.get(matrixBuf));

                bgfx_encoder_set_vertex_buffer(encoder, 0, vertexBufferId, command.getVertexStart(),
                        command.getVertexCount(), BGFX_INVALID_HANDLE);

                bgfx_encoder_set_state(encoder, BGFX_STATE_DEFAULT | BGFX_STATE_BLEND_ALPHA, 0);
                bgfx_encoder_set_texture(encoder, 0, (short) 0, textureArrayId, BGFX_SAMPLER_U_CLAMP);

                bgfx_encoder_submit(encoder, SCENE_VIEW, sceneProgram, 0, false);
            }
        }

        for (RenderCommand command : renderCommands) {
            command.render(this, encoder);
        }

        bgfx_encoder_end(encoder);

        bgfx_touch(BACKGROUND_VIEW);
        bgfx_touch(UI_VIEW);
        bgfx_touch(SCENE_VIEW);

        frame = bgfx_frame(false);

        sync();
    }

    private Matrix4f setFrustumMatrix(int offsetX, int offsetY, int centerX, int centerY, int width, int height,
                                      int pitch, int yaw, int zoom) {
        int left = (offsetX - centerX << 9) / zoom;
        int right = (offsetX + width - centerX << 9) / zoom;
        int top = (offsetY - centerY << 9) / zoom;
        int bottom = (offsetY + height - centerY << 9) / zoom;

        matrix.setFrustum(left * DEFAULT_ZOOM, right * DEFAULT_ZOOM, -bottom * DEFAULT_ZOOM,
                -top * DEFAULT_ZOOM, NEAR, FAR)
                .rotateX(PI);
        if (pitch != 0) {
            matrix.rotateX(pitch * RS_TO_RADIANS);
        }
        if (yaw != 0) {
            matrix.rotateY(yaw * RS_TO_RADIANS);
        }
        return matrix;
    }

    private void renderFullscreenTexture(long encoder) {
        updateFullscreenTexture();

        Canvas canvas = client.getCanvas();

        bgfx_encoder_set_texture(encoder, 0, (short) 0, fullscreenTextureId, BGFX_SAMPLER_NONE);
        bgfx_encoder_set_state(encoder, BGFX_STATE_WRITE_RGB | BGFX_STATE_WRITE_A, 0);
        renderQuad(encoder, UI_VIEW, quadProgram, 0, 0, canvas.getWidth(), canvas.getHeight(),
                0xFFFFFF, 255);
    }

    private void updateFullscreenTexture() {
        Canvas canvas = client.getCanvas();

        BufferProvider bufferProvider = client.getBufferProvider();
        int[] pixels = bufferProvider.getPixels();
        fullscreenTextureBuf.clear();
        fullscreenTextureBuf.put(pixels);
        fullscreenTextureBuf.flip();

        if (fullscreenTextureId == -1 || !canvas.getSize().equals(lastCanvasSize)) {
            if (fullscreenTextureId != -1) {
                bgfx_destroy_texture(fullscreenTextureId);
            }
            fullscreenTextureId = bgfx_create_texture_2d(canvas.getWidth(), canvas.getHeight(), false, 1,
                    BGFX_TEXTURE_FORMAT_BGRA8, BGFX_TEXTURE_NONE, null);
            lastCanvasSize = canvas.getSize();
        }
        bgfx_update_texture_2d(fullscreenTextureId, 0, 0, 0, 0,
                canvas.getWidth(), canvas.getHeight(), bgfx_make_ref(fullscreenTextureBuf), 0xFFFF);
    }

    @Override
    public void onFrameStart() {
        sync();
        sync();
    }

    @Override
    public void onFrameEnd() {
        sync();
        sync();
    }

    public boolean isBufferProviderPixels() {
        return client.getBufferProvider().getPixels() == client.getGraphicsPixels();
    }

    private int getScissorX() {
        return client.getScissorX() + client.getCanvas().getX();
    }

    private int getScissorY() {
        return client.getScissorY() + client.getCanvas().getY();
    }

    private int getScissorWidth() {
        return client.getScissorWidth();
    }

    private int getScissorHeight() {
        return client.getScissorHeight();
    }

    @Override
    public boolean fillRectangle(int x, int y, int width, int height, int rgb) {
        return fillRectangle(x, y, width, height, rgb, 255);
    }

    @Override
    public boolean fillRectangle(int x, int y, int width, int height, int rgb, int alpha) {
        if (!isBufferProviderPixels()) {
            return false;
        }
        if (width == 0 || height == 0 || alpha == 0) {
            return true;
        }
        alpha = Math.min(alpha, 255);
        renderCommands.add(new RenderRectangleCommand(x, y, width, height, rgb, alpha, getScissorX(), getScissorY(),
                getScissorWidth(), getScissorHeight()));
        return true;
    }

    @Override
    public boolean drawHorizontalLine(int x, int y, int width, int rgb) {
        return drawHorizontalLine(x, y, width, rgb, 255);
    }

    @Override
    public boolean drawHorizontalLine(int x, int y, int width, int rgb, int alpha) {
        return fillRectangle(x, y, width, 1, rgb, alpha);
    }

    @Override
    public boolean drawVerticalLine(int x, int y, int height, int rgb) {
        return drawVerticalLine(x, y, height, rgb, 255);
    }

    @Override
    public boolean drawVerticalLine(int x, int y, int height, int rgb, int alpha) {
        return fillRectangle(x, y, 1, height, rgb, alpha);
    }

    @Override
    public boolean drawSprite(IndexedSprite sprite, int x, int y) {
        return drawSprite(sprite, x, y, sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public boolean drawSprite(IndexedSprite sprite, int x, int y, int width, int height) {
        byte[] palettePixels = sprite.getPixels();
        int[] palette = sprite.getPalette();

        int[] pixels = new int[palettePixels.length];
        for (int i = 0; i < pixels.length; i++) {
            int paletteIndex = palettePixels[i];
            if (paletteIndex != 0) {
                pixels[i] = 0xFF << 24 | palette[paletteIndex];
            }
        }
        return drawSprite(sprite, pixels, x, y, width, height, 255);
    }

    @Override
    public boolean drawSprite(Sprite sprite, int x, int y) {
        return drawSprite(sprite, x, y, sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public boolean drawSprite(Sprite sprite, int x, int y, int alpha) {
        return drawSprite(sprite, x, y, sprite.getWidth(), sprite.getHeight(), alpha);
    }

    @Override
    public boolean drawSprite(Sprite sprite, int x, int y, int width, int height) {
        return drawSprite(sprite, x, y, width, height, 255);
    }

    @Override
    public boolean drawSprite(Sprite sprite, int x, int y, int width, int height, int alpha) {
        int[] pixels = sprite.getPixels();
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != 0) {
                pixels[i] |= 0xFF << 24;
            }
        }
        return drawSprite(sprite, pixels, x, y, width, height, alpha);
    }

    private boolean drawSprite(AbstractSprite sprite, int[] pixels, int x, int y, int width, int height, int alpha) {
        if (!isBufferProviderPixels()) {
            return false;
        }
        if (width == 0 || height == 0 || alpha == 0) {
            return true;
        }
        int spriteWidth = sprite.getWidth();
        int spriteHeight = sprite.getHeight();

        int offsetX = sprite.getOffsetX();
        int offsetY = sprite.getOffsetY();

        if (spriteWidth != width || spriteHeight != height) {
            int scaledX = 0;
            int scaledY = 0;
            final int maxWidth = sprite.getMaxWidth();
            final int maxHeight = sprite.getMaxHeight();
            final int scaledWidth = (maxWidth << 16) / width;
            final int scaledHeight = (maxHeight << 16) / height;
            if (offsetX > 0) {
                final int scaledOffsetX = ((offsetX << 16) + scaledWidth - 1) / scaledWidth;
                x += scaledOffsetX;
                scaledX += scaledOffsetX * scaledWidth - (offsetX << 16);
            }
            if (offsetY > 0) {
                final int scaledOffsetY = ((offsetY << 16) + scaledHeight - 1) / scaledHeight;
                y += scaledOffsetY;
                scaledY += scaledOffsetY * scaledHeight - (offsetY << 16);
            }
            if (spriteWidth < maxWidth) {
                width = ((spriteWidth << 16) - scaledX + scaledWidth - 1) / scaledWidth;
            }
            if (spriteHeight < maxHeight) {
                height = ((spriteHeight << 16) - scaledY + scaledHeight - 1) / scaledHeight;
            }
        } else {
            x += offsetX;
            y += offsetY;
        }

        alpha = Math.min(alpha, 255);

        IntBuffer pixelsBuf = MemoryUtil.memAllocInt(pixels.length);
        pixelsBuf.put(pixels);
        pixelsBuf.flip();

        renderCommands.add(new RenderSpriteCommand(pixelsBuf, spriteWidth, spriteHeight, x, y, width, height, alpha,
                getScissorX(), getScissorY(), getScissorWidth(), getScissorHeight()));
        return true;
    }

    @Override
    public boolean drawGlyph(AbstractFont font, byte[] glyph, int x, int y, int width, int height, int rgb) {
        return drawGlyph(font, glyph, x, y, width, height, rgb, 255);
    }

    @Override
    public boolean drawGlyph(AbstractFont font, byte[] glyph, int x, int y, int width, int height, int rgb, int alpha) {
        if (!isBufferProviderPixels()) {
            return false;
        }
        if (width == 0 || height == 0 || alpha == 0) {
            return true;
        }
        int glyphId = font.getGlyphIdMap().get(glyph);
        renderCommands.add(new RenderGlyphCommand(font, glyphId, x, y, width, height, rgb, alpha,
                getScissorX(), getScissorY(), getScissorWidth(), getScissorHeight()));
        return true;
    }

    @Override
    public boolean drawScene(Scene scene, int cameraX, int cameraY, int cameraZ, int pitch, int yaw, int maxLevel) {
        renderCommands.add(new RenderSceneCommand());
        return false;
    }

    @Override
    public boolean drawModel(Model model, int rotation, int x, int y, int z) {
        int[] verticesX = model.getVerticesX();
        int[] verticesY = model.getVerticesY();
        int[] verticesZ = model.getVerticesZ();

        int[] indicesA = model.getIndicesA();
        int[] indicesB = model.getIndicesB();
        int[] indicesC = model.getIndicesC();

        int[] colorsA = model.getColorsA();
        int[] colorsB = model.getColorsB();
        int[] colorsC = model.getColorsC();

        short[] triangleTextures = model.getTriangleTextures();

        byte[] triangleAlphas = model.getTriangleAlphas();

        int[] colorPalette = client.getColorPalette();

        ByteBuffer vertexBuf = vertexBuffers[frame % 2];

        if (vertexBuf.remaining() < model.getTriangleCount() * 3 * sceneLayout.stride()) {
            vertexBuf.flip();
            ByteBuffer newBuffer = MemoryUtil.memAlloc(vertexBuf.capacity() * 2);
            newBuffer.put(vertexBuf);
            buffersToRemove.add(vertexBuf);
            vertexBuf = vertexBuffers[frame % 2] = newBuffer;
            System.out.println("new buffer: " + vertexBuf.capacity());
        }

        int vertexStart = vertexCount;

        float[][][] texCoords = computeTexCoords(model);

        float[][] us = texCoords[0];
        float[][] vs = texCoords[1];

        for (int i = 0; i < model.getTriangleCount(); i++) {
            int colorA = colorsA[i];
            int colorB = colorsB[i];
            int colorC = colorsC[i];
            int textureId = 0;
            if (triangleTextures != null) {
                textureId = triangleTextures[i] + 1;
            }
            int alpha = 0xFF;
            if (triangleAlphas != null && textureId > 0) {
                alpha -= triangleAlphas[i];
            }
            if (colorC == -1) {
                colorC = colorB = colorA;
            } else if (colorC == -2) {
                continue;
            }

            float[] u = us[i];
            float[] v = vs[i];

            addSceneVertex(vertexBuf,
                    verticesX[indicesA[i]],
                    verticesY[indicesA[i]],
                    verticesZ[indicesA[i]],
                    colorPalette[colorA],
                    alpha,
                    u[0],
                    v[0],
                    textureId);
            addSceneVertex(vertexBuf,
                    verticesX[indicesB[i]],
                    verticesY[indicesB[i]],
                    verticesZ[indicesB[i]],
                    colorPalette[colorB],
                    alpha,
                    u[1],
                    v[1],
                    textureId);
            addSceneVertex(vertexBuf,
                    verticesX[indicesC[i]],
                    verticesY[indicesC[i]],
                    verticesZ[indicesC[i]],
                    colorPalette[colorC],
                    alpha,
                    u[2],
                    v[2],
                    textureId);

            vertexCount += 3;
        }
        renderModelCommands.add(new RenderModelCommand(model, rotation, x, y, z, vertexStart,
                vertexCount - vertexStart));
        return false;
    }

    private float[][][] computeTexCoords(Model model) {
        int triangleCount = model.getTriangleCount();

        int[] verticesX = model.getVerticesX();
        int[] verticesY = model.getVerticesY();
        int[] verticesZ = model.getVerticesZ();

        int[] indicesA = model.getIndicesA();
        int[] indicesB = model.getIndicesB();
        int[] indicesC = model.getIndicesC();

        int[] texIndicesP = model.getTextureIndicesP();
        int[] texIndicesM = model.getTextureIndicesM();
        int[] texIndicesN = model.getTextureIndicesN();

        short[] triangleTextures = model.getTriangleTextures();
        byte[] textureMapping = model.getTextureMapping();

        float[][] texCoordUs = new float[triangleCount][3];
        float[][] texCoordVs = new float[triangleCount][3];
        for (int i = 0; i < triangleCount; i++) {
            int textureId = -1;
            if (triangleTextures != null) {
                textureId = triangleTextures[i];
            }
            int mapping = -1;
            if (textureMapping != null) {
                mapping = textureMapping[i];
            }
            if (textureId == -1) {
                continue;
            }
            float[] u = texCoordUs[i];
            float[] v = texCoordVs[i];
            if (mapping == -1) {
                u[0] = 0.0f;
                v[0] = 1.0f;

                u[1] = 1.0f;
                v[1] = 1.0f;

                u[2] = 0.0f;
                v[2] = 0.0f;
            } else {
                mapping &= 0xFF;
                int a = indicesA[i];
                int b = indicesB[i];
                int c = indicesC[i];
                int p = texIndicesP[mapping];
                int m = texIndicesM[mapping];
                int n = texIndicesN[mapping];
                float originX = verticesX[p];
                float originY = verticesY[p];
                float originZ = verticesZ[p];
                float mxDistance = verticesX[m] - originX;
                float myDistance = verticesY[m] - originY;
                float mzDistance = verticesZ[m] - originZ;
                float nxDistance = verticesX[n] - originX;
                float nyDistance = verticesY[n] - originY;
                float nzDistance = verticesZ[n] - originZ;
                float axDistance = verticesX[a] - originX;
                float ayDistance = verticesY[a] - originY;
                float azDistance = verticesZ[a] - originZ;
                float bxDistance = verticesX[b] - originX;
                float byDistance = verticesY[b] - originY;
                float bzDistance = verticesZ[b] - originZ;
                float cxDistance = verticesX[c] - originX;
                float cyDistance = verticesY[c] - originY;
                float czDistance = verticesZ[c] - originZ;
                float f_797_ = myDistance * nzDistance - mzDistance * nyDistance;
                float f_798_ = mzDistance * nxDistance - mxDistance * nzDistance;
                float f_799_ = mxDistance * nyDistance - myDistance * nxDistance;
                float f_800_ = nyDistance * f_799_ - nzDistance * f_798_;
                float f_801_ = nzDistance * f_797_ - nxDistance * f_799_;
                float f_802_ = nxDistance * f_798_ - nyDistance * f_797_;
                float f_803_ = 1.0f / (f_800_ * mxDistance + f_801_ * myDistance + f_802_ * mzDistance);
                u[0] = (f_800_ * axDistance + f_801_ * ayDistance + f_802_ * azDistance) * f_803_;
                u[1] = (f_800_ * bxDistance + f_801_ * byDistance + f_802_ * bzDistance) * f_803_;
                u[2] = (f_800_ * cxDistance + f_801_ * cyDistance + f_802_ * czDistance) * f_803_;
                f_800_ = myDistance * f_799_ - mzDistance * f_798_;
                f_801_ = mzDistance * f_797_ - mxDistance * f_799_;
                f_802_ = mxDistance * f_798_ - myDistance * f_797_;
                f_803_ = 1.0f / (f_800_ * nxDistance + f_801_ * nyDistance + f_802_ * nzDistance);
                v[0] = (f_800_ * axDistance + f_801_ * ayDistance + f_802_ * azDistance) * f_803_;
                v[1] = (f_800_ * bxDistance + f_801_ * byDistance + f_802_ * bzDistance) * f_803_;
                v[2] = (f_800_ * cxDistance + f_801_ * cyDistance + f_802_ * czDistance) * f_803_;
            }
        }
        return new float[][][] {texCoordUs, texCoordVs};
    }

    @Override
    public boolean drawModelTriangle(Model model, int index) {
        return isBufferProviderPixels();
    }

    private void sync() {
        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private short createShader(String name) {
        String path = "shaders/";
        switch (rendererType) {
            case BGFX_RENDERER_TYPE_DIRECT3D9:
                path += "dx9";
                break;
            case BGFX_RENDERER_TYPE_DIRECT3D11:
                path += "dx11";
                break;
            case BGFX_RENDERER_TYPE_OPENGLES:
                path += "essl";
                break;
            case BGFX_RENDERER_TYPE_OPENGL:
                path += "glsl";
                break;
            case BGFX_RENDERER_TYPE_METAL:
                path += "metal";
                break;
            default:
                throw new IllegalStateException("Unknown renderer type: " + rendererType);
        }
        try {
            ByteBuffer shaderResource = ResourceUtil.loadResource(path, name + ".bin");
            return bgfx_create_shader(bgfx_make_ref_release(shaderResource, releaseMemoryCb, 0L));
        } catch (IOException e) {
            throw new RuntimeException("Failed loading shader resource: " + path + "/" + name + ".bin", e);
        }
    }

    private short createProgram(String vertexShaderName, String fragmentShaderName) {
        return createProgram(vertexShaderName, fragmentShaderName, true);
    }

    private short createProgram(String vertexShaderName, String fragmentShaderName, boolean destroy) {
        short vs = createShader(vertexShaderName);
        short fs = createShader(fragmentShaderName);
        return bgfx_create_program(vs, fs, destroy);
    }

    public void renderQuad(long encoder, int view, short program, float x, float y, float width, float height,
                           int rgb, int alpha) {
        renderQuad(encoder, view, program, x, y, width, height, rgb, alpha, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    public void renderQuad(long encoder, int view, short program, float x, float y, float width, float height,
                           int rgb, int alpha, float minU, float minV, float maxU, float maxV) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            BGFXTransientVertexBuffer tvb = BGFXTransientVertexBuffer.callocStack(stack);
            BGFXTransientIndexBuffer tib = BGFXTransientIndexBuffer.callocStack(stack);

            if (bgfx_alloc_transient_buffers(tvb, layout, 4, tib, 6)) {
                ByteBuffer vertex = tvb.data();

                float z = 0.0f;

                float minX = x;
                float minY = y;
                float maxX = x + width;
                float maxY = y + height;

                int r = rgb >> 16 & 0xFF;
                int g = rgb >> 8 & 0xFF;
                int b = rgb & 0xFF;

                int abgr = alpha << 24 | b << 16 | g << 8 | r;

                vertex.putFloat(minX);
                vertex.putFloat(minY);
                vertex.putFloat(z);
                vertex.putInt(abgr);
                vertex.putFloat(minU);
                vertex.putFloat(minV);

                vertex.putFloat(maxX);
                vertex.putFloat(minY);
                vertex.putFloat(z);
                vertex.putInt(abgr);
                vertex.putFloat(maxU);
                vertex.putFloat(minV);

                vertex.putFloat(maxX);
                vertex.putFloat(maxY);
                vertex.putFloat(z);
                vertex.putInt(abgr);
                vertex.putFloat(maxU);
                vertex.putFloat(maxV);

                vertex.putFloat(minX);
                vertex.putFloat(maxY);
                vertex.putFloat(z);
                vertex.putInt(abgr);
                vertex.putFloat(minU);
                vertex.putFloat(maxV);
                vertex.flip();

                ByteBuffer indices = tib.data();
                indices.putShort((short) 0);
                indices.putShort((short) 2);
                indices.putShort((short) 1);
                indices.putShort((short) 0);
                indices.putShort((short) 3);
                indices.putShort((short) 2);
                indices.flip();

                bgfx_encoder_set_transient_vertex_buffer(encoder, 0, tvb, 0, 4,
                        BGFX_INVALID_HANDLE);
                bgfx_encoder_set_transient_index_buffer(encoder, tib, 0, 6);

                bgfx_encoder_submit(encoder, view, program, 0, false);
            }
        }
    }

    private BGFXVertexLayout createVertexLayout(boolean withNormals, boolean withColor, boolean withTexCoords,
                                                boolean withTextureId) {
        BGFXVertexLayout layout = BGFXVertexLayout.calloc();

        bgfx_vertex_layout_begin(layout, rendererType);

        bgfx_vertex_layout_add(layout,
                BGFX_ATTRIB_POSITION,
                3,
                BGFX_ATTRIB_TYPE_FLOAT,
                false,
                false);

        if (withNormals) {
            bgfx_vertex_layout_add(layout,
                    BGFX_ATTRIB_NORMAL,
                    3,
                    BGFX_ATTRIB_TYPE_FLOAT,
                    false,
                    false);
        }

        if (withColor) {
            bgfx_vertex_layout_add(layout,
                    BGFX_ATTRIB_COLOR0,
                    4,
                    BGFX_ATTRIB_TYPE_UINT8,
                    true,
                    false);
        }

        if (withTexCoords) {
            bgfx_vertex_layout_add(layout,
                    BGFX_ATTRIB_TEXCOORD0,
                    2,
                    BGFX_ATTRIB_TYPE_FLOAT,
                    false,
                    false);
        }

        if (withTextureId) {
            bgfx_vertex_layout_add(layout,
                    BGFX_ATTRIB_TEXCOORD7,
                    1,
                    BGFX_ATTRIB_TYPE_FLOAT,
                    false,
                    false);
        }

        bgfx_vertex_layout_end(layout);

        return layout;
    }

    private void setGlfwCallbacks() {
        // Keyboard
        glfwSetCharCallback(window, this::onCharInput);
        glfwSetKeyCallback(window, this::onKeyInput);

        // Mouse
        glfwSetCursorPosCallback(window, this::onMouseMoved);
        glfwSetCursorEnterCallback(window, this::onMouseEntered);
        glfwSetMouseButtonCallback(window, this::onMouseClicked);
        glfwSetScrollCallback(window, this::onMouseScrolled);

        glfwSetWindowFocusCallback(window, this::onFocus);
        glfwSetWindowSizeCallback(window, this::resize);
        glfwSetFramebufferSizeCallback(window, this::resize);
        glfwSetWindowRefreshCallback(window, this::refresh);
    }

    private void onCharInput(long window, int codePoint) {
        char[] chars = Character.toChars(codePoint);
        Canvas canvas = client.getCanvas();
        if (canvas != null && chars.length == 1) {
            KeyEvent event = new KeyEvent(canvas, 0, 0, 0, 0, chars[0]);
            for (KeyListener listener : canvas.getKeyListeners()) {
                listener.keyTyped(event);
            }
        }
    }

    private void onKeyInput(long window, int key, int scanCode, int action, int mods) {
        Canvas canvas = client.getCanvas();
        if (canvas != null) {
            key = KeyMapping.mapGlfwKeyToJava(key);
            if (key == -1) {
                return;
            }
            int modifiers = KeyMapping.mapGlfwModifiersToJava(mods);
            KeyEvent event = new KeyEvent(canvas, 0, 0, modifiers, key, '\0');
            for (KeyListener listener : canvas.getKeyListeners()) {
                if (action == GLFW_RELEASE) {
                    listener.keyReleased(event);
                } else {
                    listener.keyPressed(event);
                }
            }
        }
    }

    private void onMouseMoved(long window, double x, double y) {
        Canvas canvas = client.getCanvas();
        if (canvas != null) {
            int realX = (int) x;
            int realY = (int) y;
            Point canvasPoint = translateToCanvas(canvas, realX, realY);
            MouseEvent event = new MouseEvent(canvas, 0, System.currentTimeMillis(), 0,
                    canvasPoint.x, canvasPoint.y, 0, false);
            for (MouseMotionListener listener : canvas.getMouseMotionListeners()) {
                listener.mouseMoved(event);
            }
            lastMouseX = realX;
            lastMouseY = realY;
        }
    }

    private void onMouseEntered(long window, boolean entered) {
        Canvas canvas = client.getCanvas();
        if (canvas != null) {
            double[] xArr = new double[1];
            double[] yArr = new double[1];
            glfwGetCursorPos(window, xArr, yArr);
            int x = (int) xArr[0];
            int y = (int) yArr[0];
            Point canvasPoint = translateToCanvas(canvas, x, y);
            boolean onCanvas = isPointOnCanvas(canvas, x, y);
            boolean wasOnCanvas = isPointOnCanvas(canvas, lastMouseX, lastMouseY);
            MouseEvent event = new MouseEvent(canvas, 0, System.currentTimeMillis(), 0,
                    canvasPoint.x, canvasPoint.y, 0, false);
            for (MouseListener listener : canvas.getMouseListeners()) {
                if (entered && onCanvas) {
                    listener.mouseEntered(event);
                } else if (!entered && wasOnCanvas) {
                    listener.mouseExited(event);
                }
            }
        }
    }

    private void onMouseClicked(long window, int button, int action, int mods) {
        //System.out.println(button + ", " + action + ", " + mods);
        Canvas canvas = client.getCanvas();
        if (canvas != null) {
            double[] x = new double[1];
            double[] y = new double[1];
            glfwGetCursorPos(window, x, y);
            if (!isPointOnCanvas(canvas, (int) x[0], (int) y[0]) && action == GLFW_PRESS) {
                return;
            }
            Point canvasPoint = translateToCanvas(canvas, (int) x[0], (int) y[0]);
            int awtButton = MouseEvent.NOBUTTON;
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                awtButton = MouseEvent.BUTTON1;
            } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                awtButton = MouseEvent.BUTTON3;
            } else if (button == GLFW_MOUSE_BUTTON_MIDDLE) {
                awtButton = MouseEvent.BUTTON2;
            }
            MouseEvent event = new MouseEvent(canvas, 0, System.currentTimeMillis(), 0,
                    canvasPoint.x, canvasPoint.y, 1, false, awtButton);
            for (MouseListener listener : canvas.getMouseListeners()) {
                if (action == GLFW_PRESS) {
                    listener.mousePressed(event);
                } else {
                    listener.mouseReleased(event);
                }
            }
        }
    }

    private void onMouseScrolled(long window, double xOffset, double yOffset) {
        Canvas canvas = client.getCanvas();
        if (canvas != null) {
            double[] x = new double[1];
            double[] y = new double[1];
            glfwGetCursorPos(window, x, y);
            if (!isPointOnCanvas(canvas, (int) x[0], (int) y[0])) {
                return;
            }
            MouseWheelEvent event = new MouseWheelEvent(canvas, 0, 0, 0, 0, 0, 0,
                    false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 0, (int) -yOffset);
            for (MouseWheelListener listener : canvas.getMouseWheelListeners()) {
                listener.mouseWheelMoved(event);
            }
        }
    }

    private void onFocus(long window, boolean focused) {
        Canvas canvas = client.getCanvas();
        if (canvas != null) {
            for (FocusListener listener : canvas.getFocusListeners()) {
                if (focused) {
                    listener.focusGained(null);
                } else {
                    listener.focusLost(null);
                }
            }
        }
    }

    private void resize(long window, int width, int height) {
        if (width < MIN_WIDTH || height < MIN_HEIGHT || (this.width == width && this.height == height)) {
            return;
        }
        System.out.println("Resize " + width + ", " + height);
        this.width = width;
        this.height = height;
        client.setSize(width, height);
        bgfx_reset(width, height, RESET, format);
    }

    private void refresh(long window) {
        System.out.println("Refresh");
    }

    public Client getClient() {
        return client;
    }

    public BGFXCaps getBgfxCaps() {
        return bgfxCaps;
    }

    public short getQuadProgram() {
        return quadProgram;
    }

    public List<Buffer> getBuffersToRemove() {
        return buffersToRemove;
    }

    public List<Short> getTexturesToRemove() {
        return texturesToRemove;
    }

    public short getWhiteTextureId() {
        return whiteTextureId;
    }

    public short getFrameBufferId() {
        return frameBufferId;
    }
}
