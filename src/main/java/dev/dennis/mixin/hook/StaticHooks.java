package dev.dennis.mixin.hook;

import java.util.Map;

public class StaticHooks {
    private final Map<String, StaticFieldHook> fields;

    private final Map<String, StaticMethodHook> methods;

    public StaticHooks(Map<String, StaticFieldHook> fields, Map<String, StaticMethodHook> methods) {
        this.fields = fields;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return "StaticHooks{" +
                "fields=" + fields +
                ", methods=" + methods +
                '}';
    }

    public StaticFieldHook getField(String name) {
        return fields.get(name);
    }

    public StaticMethodHook getMethod(String name) {
        return methods.get(name);
    }

    public Map<String, StaticFieldHook> getFields() {
        return fields;
    }

    public Map<String, StaticMethodHook> getMethods() {
        return methods;
    }
}
