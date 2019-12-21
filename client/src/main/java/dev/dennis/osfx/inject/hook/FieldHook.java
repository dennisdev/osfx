package dev.dennis.osfx.inject.hook;

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

    @Override
    public String toString() {
        return "FieldHook{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", multiplier=" + multiplier +
                '}';
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
