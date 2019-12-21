package dev.dennis.osfx.inject.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AddInterfaceAdapter extends ClassVisitor {
    private final Type interfaceType;

    public AddInterfaceAdapter(ClassVisitor classVisitor, String interfaceName) {
        this(classVisitor, Type.getObjectType(interfaceName));
    }

    public AddInterfaceAdapter(ClassVisitor classVisitor, Class<?> interfaceClass) {
        this(classVisitor, Type.getType(interfaceClass));
    }

    public AddInterfaceAdapter(ClassVisitor classVisitor, Type interfaceType) {
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
