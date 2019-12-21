package dev.dennis.osfx.inject.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class AddInvokeAdapter extends ClassVisitor {
    private static final int ACCESS = Opcodes.ACC_PUBLIC;

    private final String name;

    private final String desc;

    private final String targetOwner;

    private final String targetName;

    private final String targetDesc;

    private final Integer targetDummyValue;

    public AddInvokeAdapter(ClassVisitor classVisitor, String name, String desc, String targetOwner, String targetName,
                            String targetDesc, Integer targetDummyValue) {
        super(Opcodes.ASM7, classVisitor);
        this.name = name;
        this.desc = desc;
        this.targetOwner = targetOwner;
        this.targetName = targetName;
        this.targetDesc = targetDesc;
        this.targetDummyValue = targetDummyValue;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = visitMethod(ACCESS, name, desc, null, null);

        GeneratorAdapter gen = new GeneratorAdapter(mv, ACCESS, name, desc);

        gen.loadThis();
        int argCount = Type.getArgumentTypes(desc).length;
        for (int i = 0; i < argCount; i++) {
            gen.loadArg(i);
        }
        if (targetDummyValue != null) {
            gen.push(targetDummyValue);
        }
        gen.invokeVirtual(Type.getObjectType(targetOwner), new Method(targetName, targetDesc));
        gen.returnValue();
        gen.visitMaxs(0, 0);
        gen.visitEnd();

        super.visitEnd();
    }
}
