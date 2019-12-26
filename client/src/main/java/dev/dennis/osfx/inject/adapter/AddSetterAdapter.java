package dev.dennis.osfx.inject.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class AddSetterAdapter extends ClassVisitor {
    private static final int ACCESS = Opcodes.ACC_PUBLIC;

    private final String setterName;

    private final String setterDesc;

    private final boolean isStatic;

    private final String fieldOwner;

    private final String fieldName;

    private final String fieldDesc;

    private final Number fieldMultiplier;

    private String owner;

    public AddSetterAdapter(ClassVisitor classVisitor, String setterName, String setterDesc, boolean isStatic,
                            String fieldOwner, String fieldName, String fieldDesc, Number fieldMultiplier) {
        super(Opcodes.ASM7, classVisitor);
        this.setterName = setterName;
        this.setterDesc = setterDesc;
        this.isStatic = isStatic;
        this.fieldOwner = fieldOwner;
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

        Type fieldType = Type.getType(fieldDesc);

        if (!isStatic) {
            gen.loadThis();
        }

        gen.loadArg(0);

        if (fieldMultiplier != null) {
            if (fieldType.equals(Type.INT_TYPE)) {
                gen.push(fieldMultiplier.intValue());
                gen.visitInsn(Opcodes.IMUL);
            } else if (fieldType.equals(Type.LONG_TYPE)) {
                gen.push((Long) fieldMultiplier);
                gen.visitInsn(Opcodes.LMUL);
            }
        }

        if (isStatic) {
            gen.putStatic(Type.getObjectType(fieldOwner), fieldName, fieldType);
        } else {
            gen.putField(Type.getObjectType(owner), fieldName, fieldType);
        }

        gen.returnValue();
        gen.visitMaxs(0, 0);
        gen.visitEnd();

        super.visitEnd();
    }
}
