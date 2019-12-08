package dev.dennis.mixin.inject;

import com.google.gson.Gson;
import dev.dennis.inject.HooksTest;
import dev.dennis.mixin.hook.Hooks;
import dev.dennis.osfx.mixin.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;

public class InjectorTest {
    public static void main(String[] args) throws Exception {
        int version = 186;

        Hooks hooks = loadHooks(version);

        Injector injector = new Injector(hooks);
        injector.loadMixin(GameEngineMixin.class);
        injector.loadMixin(ClientMixin.class);
        injector.loadMixin(BufferProviderMixin.class);
        injector.loadMixin(MainBufferProviderMixin.class);
        injector.loadMixin(TimerMixin.class);
        injector.loadMixin(NanoTimerMixin.class);

        long start = System.currentTimeMillis();
        injector.inject(Paths.get(version + ".jar"), Paths.get(version + "_injected.jar"));
        long end = System.currentTimeMillis();
        System.out.println("elapsed: " + (end - start));
    }

    private static Hooks loadHooks(int version) {
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(HooksTest.class.getResourceAsStream("/hooks/" + version + ".json"));
        return gson.fromJson(reader, Hooks.class);
    }
}
