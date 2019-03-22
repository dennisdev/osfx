package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class AddSetterAdapter extends ClassVisitor {
    private static final int ACCESS = Opcodes.ACC_PUBLIC;

    private final String setterName;

    private final String fieldName;

    private final String fieldDesc;

    private final Number fieldMultiplier;

    private String owner;

    public AddSetterAdapter(String setterName, String fieldName, String fieldDesc,
                            Number fieldMultiplier, ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
        this.setterName = setterName;
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
        String setterDesc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getObjectType(fieldDesc));
        MethodVisitor mv = visitMethod(ACCESS, setterName, setterDesc, null, null);


        GeneratorAdapter genAdapter = new GeneratorAdapter(mv, ACCESS, setterName, setterDesc);

        genAdapter.loadThis();
        genAdapter.loadArg(0);
        genAdapter.putField(Type.getObjectType(owner), fieldName, Type.getObjectType(fieldDesc));
        genAdapter.returnValue();
        genAdapter.visitMaxs(2, 1);
        genAdapter.visitEnd();

        super.visitEnd();
    }
}
