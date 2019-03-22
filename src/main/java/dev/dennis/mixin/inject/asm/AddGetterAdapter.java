package dev.dennis.mixin.inject.asm;

import dev.dennis.mixin.hook.FieldHook;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class AddGetterAdapter extends ClassVisitor {
    private static final int ACCESS = Opcodes.ACC_PUBLIC;

    private final String getterName;

    private final String getterDesc;

    private final String fieldName;

    private final String fieldDesc;

    private final Number fieldMultiplier;

    private String owner;

    public AddGetterAdapter(String getterName, String getterDesc, FieldHook fieldHook, ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
        this.getterName = getterName;
        this.getterDesc = getterDesc;
        this.fieldName = fieldHook.getName();
        this.fieldDesc = fieldHook.getDesc();
        this.fieldMultiplier = fieldHook.getMultiplier();
    }

    public AddGetterAdapter(String getterName, String getterDesc, String fieldName, String fieldDesc,
                            Number fieldMultiplier, ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
        this.getterName = getterName;
        this.getterDesc = getterDesc;
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
        MethodVisitor mv = visitMethod(ACCESS, getterName, getterDesc, null, null);

        GeneratorAdapter genAdapter = new GeneratorAdapter(mv, ACCESS, getterName, getterDesc);

        genAdapter.loadThis();
        genAdapter.getField(Type.getObjectType(owner), fieldName, Type.getObjectType(fieldDesc));
        genAdapter.returnValue();
        genAdapter.visitMaxs(1, 0);
        genAdapter.visitEnd();

        super.visitEnd();
    }
}
