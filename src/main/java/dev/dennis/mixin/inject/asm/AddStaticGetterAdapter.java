package dev.dennis.mixin.inject.asm;

import dev.dennis.mixin.hook.StaticFieldHook;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class AddStaticGetterAdapter extends ClassVisitor {
    private static final int ACCESS = Opcodes.ACC_PUBLIC;

    private final String getterName;

    private final String getterDesc;

    private final String fieldOwner;

    private final String fieldName;

    private final String fieldDesc;

    private final Number fieldMultiplier;

    public AddStaticGetterAdapter(ClassVisitor classVisitor, String getterName, String getterDesc,
                                  StaticFieldHook fieldHook) {
        this(classVisitor, getterName, getterDesc, fieldHook.getOwner(), fieldHook.getName(), fieldHook.getDesc(),
                fieldHook.getMultiplier());
    }

    public AddStaticGetterAdapter(ClassVisitor classVisitor, String getterName, String getterDesc, String fieldOwner,
                                  String fieldName, String fieldDesc, Number fieldMultiplier) {
        super(Opcodes.ASM7, classVisitor);
        this.getterName = getterName;
        this.getterDesc = getterDesc;
        this.fieldOwner = fieldOwner;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
        this.fieldMultiplier = fieldMultiplier;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = visitMethod(ACCESS, getterName, getterDesc, null, null);

        GeneratorAdapter gen = new GeneratorAdapter(mv, ACCESS, getterName, getterDesc);

        gen.getStatic(Type.getObjectType(fieldOwner), fieldName, Type.getType(fieldDesc));
        gen.returnValue();
        gen.visitMaxs(0, 0);
        gen.visitEnd();

        super.visitEnd();
    }
}
