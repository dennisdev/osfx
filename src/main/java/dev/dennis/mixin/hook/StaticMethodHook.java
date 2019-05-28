package dev.dennis.mixin.hook;

public class StaticMethodHook {
    private final String owner;

    private final String name;

    private final String desc;

    private final Integer dummyValue;

    public StaticMethodHook(String owner, String name, String desc) {
        this(owner, name, desc, null);
    }

    public StaticMethodHook(String owner, String name, String desc, Integer dummyValue) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.dummyValue = dummyValue;
    }

    @Override
    public String toString() {
        return "StaticMethodHook{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", dummyValue=" + dummyValue +
                '}';
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

    public Integer getDummyValue() {
        return dummyValue;
    }
}
