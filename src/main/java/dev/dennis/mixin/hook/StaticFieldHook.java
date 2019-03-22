package dev.dennis.mixin.hook;

public class StaticFieldHook {
    private final String owner;

    private final String name;

    private final String desc;

    private final Number multiplier;

    public StaticFieldHook(String owner, String name, String desc) {
        this(owner, name, desc, null);
    }

    public StaticFieldHook(String owner, String name, String desc, Number multiplier) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.multiplier = multiplier;
    }

    public String getOwner() {
        return owner;
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
