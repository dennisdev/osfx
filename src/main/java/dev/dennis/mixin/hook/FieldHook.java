package dev.dennis.mixin.hook;

public class FieldHook {
    private final String name;

    private final String desc;

    private final Number multiplier;

    public FieldHook(String name, String desc) {
        this(name, desc, null);
    }

    public FieldHook(String name, String desc, Number multiplier) {
        this.name = name;
        this.desc = desc;
        this.multiplier = multiplier;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Number getMultiplier() {
        return multiplier;
    }
}
