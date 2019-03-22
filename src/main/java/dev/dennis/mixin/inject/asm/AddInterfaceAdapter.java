package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

public class AddInterfaceAdapter extends ClassVisitor {
    private final Type interfaceType;

    public AddInterfaceAdapter(String interfaceName, ClassVisitor classVisitor) {
        this(Type.getObjectType(interfaceName), classVisitor);
    }

    public AddInterfaceAdapter(Class<?> interfaceClass, ClassVisitor classVisitor) {
        this(Type.getType(interfaceClass), classVisitor);
    }

    public AddInterfaceAdapter(Type interfaceType, ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
        this.interfaceType = interfaceType;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String[] newInterfaces;
        if (interfaces == null) {
            newInterfaces = new String[]{interfaceType.getInternalName()};
        } else {
            newInterfaces = new String[interfaces.length + 1];
            newInterfaces[interfaces.length] = interfaceType.getInternalName();
            System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
        }
        super.visit(version, access, name, signature, superName, newInterfaces);
    }
}
