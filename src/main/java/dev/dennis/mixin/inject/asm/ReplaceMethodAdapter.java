package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class ReplaceMethodAdapter extends ClassVisitor {
    private final String name;

    private final String desc;

    private final String targetOwner;

    private final String targetName;

    private final String targetDesc;

    public ReplaceMethodAdapter(ClassVisitor classVisitor, String name, String desc,
                                String targetOwner, String targetName, String targetDesc) {
        super(Opcodes.ASM7, classVisitor);
        this.name = name;
        this.desc = desc;
        this.targetOwner = targetOwner;
        this.targetName = targetName;
        this.targetDesc = targetDesc;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals(this.name) && descriptor.equals(this.desc)) {
            return new ReplaceMethodVisitor(mv, access, name, desc);
        }
        return mv;
    }

    private class ReplaceMethodVisitor extends MethodVisitor {
        private final MethodVisitor mv;

        private final int access;

        private final String name;

        private final String desc;

        public ReplaceMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM7);
            this.mv = mv;
            this.access = access;
            this.name = name;
            this.desc = desc;
        }

        @Override
        public void visitCode() {
            GeneratorAdapter gen = new GeneratorAdapter(mv, access, name, desc);

            gen.visitCode();
            gen.loadThis();
            gen.invokeVirtual(Type.getObjectType(targetOwner), new Method(targetName, targetDesc));
            gen.returnValue();

            gen.visitMaxs(0, 0);
            gen.visitEnd();

            super.visitEnd();
        }
    }
}
