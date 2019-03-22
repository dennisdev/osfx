package dev.dennis.mixin.inject;

import dev.dennis.mixin.*;
import dev.dennis.mixin.hook.*;
import dev.dennis.mixin.inject.asm.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Injector {
    private static final String GAME_ENGINE_HOOK_NAME = "GameEngine";

    private final Hooks hooks;

    private final Map<String, ClassWriter> writers;

    private final Map<String, ClassVisitor> adapters;

    public Injector(Hooks hooks) {
        this.hooks = hooks;
        this.writers = new HashMap<>();
        this.adapters = new HashMap<>();
    }

    public void loadMixins(String packageName) {

    }

    public void loadMixin(Class<?> mixinClass) throws IOException {
        if (!mixinClass.isAnnotationPresent(Mixin.class)) {
            throw new IllegalArgumentException(mixinClass.getName() + " is not a Mixin Class");
        }
        Mixin mixin = mixinClass.getAnnotation(Mixin.class);

        ClassHook classHook = hooks.getClassHook(mixin.value());
        if (classHook == null) {
            throw new IllegalStateException("No class hook found for " + mixin.value());
        }

        String obfClassName = classHook.getName();

        for (Class<?> interfaceClazz : mixinClass.getInterfaces()) {
            adapters.put(obfClassName, new AddInterfaceAdapter(interfaceClazz, delegate(obfClassName)));
        }

        for (Field field : mixinClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Shadow.class)) {
                continue;
            }
            String fieldName = field.getName();
            Type fieldType = Type.getType(field.getType());
            adapters.put(obfClassName, new AddFieldAdapter(field.getModifiers(), fieldName,
                    fieldType.getDescriptor(), delegate(obfClassName)));
            if (field.isAnnotationPresent(Getter.class)) {
                Getter getter = field.getAnnotation(Getter.class);
                String getterName = getter.value();
                if (getterName.isEmpty()) {
                    getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                }
                adapters.put(obfClassName, new AddGetterAdapter(getterName, Type.getMethodDescriptor(fieldType),
                        fieldName, fieldType.getInternalName(), null, delegate(obfClassName)));
            }
            if (field.isAnnotationPresent(Setter.class)) {
                Setter setter = field.getAnnotation(Setter.class);
                String setterName = setter.value();
                if (setterName.isEmpty()) {
                    setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                }
                adapters.put(obfClassName, new AddSetterAdapter(setterName, fieldName, fieldType.getInternalName(),
                        null, delegate(obfClassName)));
            }
        }

        List<Method> methodsToCopy = new ArrayList<>();

        for (Method method : mixinClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Getter.class)) {
                Getter getter = method.getAnnotation(Getter.class);
                boolean isStatic = method.isAnnotationPresent(Static.class);
                String methodDesc = Type.getMethodDescriptor(method);
                if (isStatic) {
                    StaticFieldHook fieldHook = hooks.getStaticField(getter.value());
                    if (fieldHook == null) {
                        throw new IllegalStateException("No static field hook found for " + getter.value());
                    }
                    adapters.put(obfClassName, new AddStaticGetterAdapter(method.getName(), methodDesc, fieldHook,
                            delegate(obfClassName)));
                } else {
                    FieldHook fieldHook = classHook.getField(getter.value());
                    if (fieldHook == null) {
                        throw new IllegalStateException("No field hook found for " + mixin.value() + "."
                                + getter.value());
                    }
                    adapters.put(obfClassName, new AddGetterAdapter(method.getName(), methodDesc, fieldHook,
                            delegate(obfClassName)));
                }
            } else if (method.isAnnotationPresent(Inject.class)) {
                Inject inject = method.getAnnotation(Inject.class);
                MethodHook methodHook = classHook.getMethod(inject.value());
                if (methodHook == null) {
                    throw new IllegalStateException("No method hook found for " + mixin.value() + "." + inject.value());
                }
                adapters.put(obfClassName, new AddInjectCallbackAdapter(method, methodHook.getName(),
                        methodHook.getDesc(), inject.end(), delegate(obfClassName)));
            }

            if (!Modifier.isAbstract(method.getModifiers())) {
                methodsToCopy.add(method);
            }
        }
        if (!methodsToCopy.isEmpty()) {
            adapters.put(obfClassName, new AddMethodsAdapter(hooks, mixinClass, methodsToCopy, delegate(obfClassName)));
        }

    }

    public void inject(Path jarPath, Path outputPath) throws IOException {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            inject(jarFile, new FileOutputStream(outputPath.toFile()));
        }
    }

    public void inject(JarFile jarFile, OutputStream outputStream) throws IOException {
        ClassHook gameEngineHook = hooks.getClassHook(GAME_ENGINE_HOOK_NAME);
        if (gameEngineHook == null) {
            throw new IllegalStateException(GAME_ENGINE_HOOK_NAME + " hook required");
        }
        Type gameEngineType = Type.getObjectType(gameEngineHook.getName());

        try (JarOutputStream jarOut = new JarOutputStream(outputStream, new Manifest())) {
            for (JarEntry entry : Collections.list(jarFile.entries())) {
                String entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    String className = entryName.substring(0, entryName.length() - 6);
                    JarEntry newEntry = new JarEntry(entryName);
                    jarOut.putNextEntry(newEntry);

                    adapters.put(className, new AppletToPanelAdapter(gameEngineType, delegate(className)));

                    ClassWriter writer = writers.get(className);
                    ClassVisitor adapter = adapters.get(className);
                    if (writer == null || adapter == null) {
                        InputStream in = jarFile.getInputStream(entry);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            jarOut.write(buffer, 0, len);
                        }
                    } else {
                        ClassReader classReader = new ClassReader(jarFile.getInputStream(entry));
                        classReader.accept(adapter, ClassReader.EXPAND_FRAMES);
                        jarOut.write(writer.toByteArray());
                    }

                    jarOut.closeEntry();
                }
            }
        }

    }

    private ClassVisitor delegate(String className) {
        return adapters.computeIfAbsent(className, this::getWriter);
    }

    private ClassWriter getWriter(String className) {
        return writers.computeIfAbsent(className, c -> new ClassWriter(0));
    }
}
