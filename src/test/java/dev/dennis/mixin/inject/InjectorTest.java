package dev.dennis.mixin.inject;

import dev.dennis.mixin.hook.*;
import dev.dennis.osfx.mixin.*;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class InjectorTest {
    public static void main(String[] args) throws Exception {
        Hooks hooks = loadHooks();

        Injector injector = new Injector(hooks);
        injector.loadMixin(GameEngineMixin.class);
        injector.loadMixin(ClientMixin.class);
        injector.loadMixin(BufferProviderMixin.class);
        injector.loadMixin(MainBufferProviderMixin.class);
        injector.loadMixin(TimerMixin.class);
        injector.loadMixin(NanoTimerMixin.class);

        long start = System.currentTimeMillis();
        injector.inject(Paths.get("178.jar"), Paths.get("178_injected.jar"));
        long end = System.currentTimeMillis();
        System.out.println("elapsed: " + (end - start));
    }

    private static Hooks loadHooks() {
        Map<String, ClassHook> classHooks = new HashMap<>();

        classHooks.put("GameEngine", createGameEngineHook());
        classHooks.put("Client", createClientHook());
        classHooks.put("BufferProvider", createBufferProviderHook());
        classHooks.put("MainBufferProvider", createMainBufferProviderHook());
        classHooks.put("Timer", createTimerHook());
        classHooks.put("NanoTimer", createNanoTimerHook());

        Map<String, StaticFieldHook> staticFieldHooks = new HashMap<>();
        staticFieldHooks.put("client", new StaticFieldHook("ke", "av", "client"));
        staticFieldHooks.put("bufferProvider", new StaticFieldHook("e", "aw", "lt"));

        Map<String, StaticMethodHook> staticMethodHooks = new HashMap<>();

        StaticHooks staticHooks = new StaticHooks(staticFieldHooks, staticMethodHooks);

        return new Hooks(classHooks, staticHooks);
    }

    private static ClassHook createGameEngineHook() {
        Map<String, FieldHook> fields = new HashMap<>();
        fields.put("canvas", new FieldHook("ac", "java/awt/Canvas"));

        Map<String, MethodHook> methods = new HashMap<>();
        methods.put("post", new MethodHook("c", "(Ljava/lang/Object;I)V", 362317017));

        return new ClassHook("bf", fields, methods);
    }

    private static ClassHook createClientHook() {
        Map<String, FieldHook> fields = new HashMap<>();

        Map<String, MethodHook> methods = new HashMap<>();

        return new ClassHook("client", fields, methods);
    }

    private static ClassHook createBufferProviderHook() {
        Map<String, FieldHook> fields = new HashMap<>();
        fields.put("pixels", new FieldHook("l", "[I"));

        Map<String, MethodHook> methods = new HashMap<>();

        return new ClassHook("lt", fields, methods);
    }

    private static ClassHook createMainBufferProviderHook() {
        Map<String, FieldHook> fields = new HashMap<>();
        fields.put("image", new FieldHook("h", "java/awt/Image"));

        Map<String, MethodHook> methods = new HashMap<>();

        return new ClassHook("af", fields, methods);
    }

    private static ClassHook createTimerHook() {
        Map<String, FieldHook> fields = new HashMap<>();

        Map<String, MethodHook> methods = new HashMap<>();

        return new ClassHook("fd", fields, methods);
    }

    private static ClassHook createNanoTimerHook() {
        Map<String, FieldHook> fields = new HashMap<>();

        Map<String, MethodHook> methods = new HashMap<>();
        methods.put("sleep", new MethodHook("h", "(IIB)I", 68));

        return new ClassHook("fk", fields, methods);
    }
}
