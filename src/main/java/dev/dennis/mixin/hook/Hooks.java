package dev.dennis.mixin.hook;

import java.util.Map;

public class Hooks {
    private final Map<String, ClassHook> classHooks;

    private final StaticHooks staticHooks;

    public Hooks(Map<String, ClassHook> classHooks, StaticHooks staticHooks) {
        this.classHooks = classHooks;
        this.staticHooks = staticHooks;
    }

    public ClassHook getClassHook(String name) {
        return classHooks.get(name);
    }

    public FieldHook getField(String className, String fieldName) {
        ClassHook classHook = classHooks.get(className);
        if (classHook == null) {
            return null;
        }
        return classHook.getField(fieldName);
    }

    public MethodHook getMethod(String className, String methodName) {
        ClassHook classHook = classHooks.get(className);
        if (classHook == null) {
            return null;
        }
        return classHook.getMethod(methodName);
    }

    public StaticFieldHook getStaticField(String name) {
        return staticHooks.getField(name);
    }

    public StaticMethodHook getStaticMethod(String name) {
        return staticHooks.getMethod(name);
    }

    public Map<String, ClassHook> getClassHooks() {
        return classHooks;
    }

    public StaticHooks getStaticHooks() {
        return staticHooks;
    }
}
