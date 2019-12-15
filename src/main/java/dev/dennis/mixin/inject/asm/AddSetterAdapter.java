package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class AddSetterAdapter extends ClassVisitor {
    private static final int ACCESS = Opcodes.ACC_PUBLIC;

    private final String setterName;

    private final String setterDesc;

    private final String fieldName;

    private final String fieldDesc;

    private final Number fieldMultiplier;

    private String owner;

    public AddSetterAdapter(ClassVisitor classVisitor, String setterName, String setterDesc,
                            String fieldName, String fieldDesc, Number fieldMultiplier) {
        super(Opcodes.ASM7, classVisitor);
        this.setterName = setterName;
        this.setterDesc = setterDesc;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
        this.fieldMultiplier = fieldMultiplier;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = visitMethod(ACCESS, setterName, setterDesc, null, null);

        GeneratorAdapter gen = new GeneratorAdapter(mv, ACCESS, setterName, setterDesc);

        gen.loadThis();
        gen.loadArg(0);
        gen.putField(Type.getObjectType(owner), fieldName, Type.getType(fieldDesc));
        gen.returnValue();
        gen.visitMaxs(0, 0);
        gen.visitEnd();

        super.visitEnd();
    }
}
