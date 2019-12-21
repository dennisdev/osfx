package dev.dennis.osfx.inject.hook;

import java.util.Map;

public class Hooks {
    private final Map<String, ClassHook> classes;

    private final StaticHooks statics;

    public Hooks(Map<String, ClassHook> classes, StaticHooks statics) {
        this.classes = classes;
        this.statics = statics;
    }

    @Override
    public String toString() {
        return "Hooks{" +
                "classes=" + classes +
                ", statics=" + statics +
                '}';
    }

    public ClassHook getClassHook(String name) {
        return classes.get(name);
    }

    public FieldHook getField(String className, String fieldName) {
        ClassHook classHook = classes.get(className);
        if (classHook == null) {
            return null;
        }
        return classHook.getField(fieldName);
    }

    public MethodHook getMethod(String className, String methodName) {
        ClassHook classHook = classes.get(className);
        if (classHook == null) {
            return null;
        }
        return classHook.getMethod(methodName);
    }

    public StaticFieldHook getStaticField(String name) {
        return statics.getField(name);
    }

    public StaticMethodHook getStaticMethod(String name) {
        return statics.getMethod(name);
    }

    public Map<String, ClassHook> getClasses() {
        return classes;
    }

    public StaticHooks getStatics() {
        return statics;
    }
}
