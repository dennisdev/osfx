package dev.dennis.osfx.inject.hook;

public class MethodHook {
    private final String name;

    private final String desc;

    private final Integer dummyValue;

    public MethodHook(String name, String desc) {
        this(name, desc, null);
    }

    public MethodHook(String name, String desc, Integer dummyValue) {
        this.name = name;
        this.desc = desc;
        this.dummyValue = dummyValue;
    }

    @Override
    public String toString() {
        return "MethodHook{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", dummyValue=" + dummyValue +
                '}';
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
