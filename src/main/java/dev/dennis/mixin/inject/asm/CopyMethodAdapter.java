package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.*;

public class CopyMethodAdapter extends ClassVisitor {
    private final String name;

    private final String desc;

    private final ClassVisitor destClassVisitor;

    private final String destName;

    public CopyMethodAdapter(ClassVisitor classVisitor, String name, String desc,
                             ClassVisitor destClassVisitor, String destName) {
        super(Opcodes.ASM7, classVisitor);
        this.name = name;
        this.desc = desc;
        this.destClassVisitor = destClassVisitor;
        this.destName = destName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals(this.name) && descriptor.equals(this.desc)) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            MethodVisitor copy = destClassVisitor.visitMethod(access, destName, descriptor, signature, exceptions);
            return new CopyMethodVisitor(mv, copy);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    private class CopyMethodVisitor extends MethodVisitor {
        private MethodVisitor copy;

        public CopyMethodVisitor(MethodVisitor mv, MethodVisitor copy) {
            super(Opcodes.ASM7, mv);
            this.copy = copy;
        }

        @Override
        public void visitParameter(String name, int access) {
            copy.visitParameter(name, access);
            super.visitParameter(name, access);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return new CopyAnnotationVisitor(super.visitAnnotationDefault(), copy.visitAnnotationDefault());
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return new CopyAnnotationVisitor(super.visitAnnotation(descriptor, visible),
                    copy.visitAnnotation(descriptor, visible));
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return new CopyAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible),
                    copy.visitTypeAnnotation(typeRef, typePath, descriptor, visible) );
        }

        @Override
        public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
            copy.visitAnnotableParameterCount(parameterCount, visible);
            super.visitAnnotableParameterCount(parameterCount, visible);
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            return new CopyAnnotationVisitor(super.visitParameterAnnotation(parameter, descriptor, visible),
                    copy.visitParameterAnnotation(parameter, descriptor, visible));
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            copy.visitAttribute(attribute);
            super.visitAttribute(attribute);
        }

        @Override
        public void visitCode() {
            copy.visitCode();
            super.visitCode();
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
            copy.visitFrame(type, numLocal, local, numStack, stack);
            super.visitFrame(type, numLocal, local, numStack, stack);
        }

        @Override
        public void visitInsn(int opcode) {
            copy.visitInsn(opcode);
            super.visitInsn(opcode);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            copy.visitIntInsn(opcode, operand);
            super.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            copy.visitVarInsn(opcode, var);
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            copy.visitTypeInsn(opcode, type);
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            copy.visitFieldInsn(opcode, owner, name, descriptor);
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
            copy.visitMethodInsn(opcode, owner, name, descriptor);
            super.visitMethodInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            copy.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            copy.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            copy.visitJumpInsn(opcode, label);
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitLabel(Label label) {
            copy.visitLabel(label);
            super.visitLabel(label);
        }

        @Override
        public void visitLdcInsn(Object value) {
            copy.visitLdcInsn(value);
            super.visitLdcInsn(value);
        }

        @Override
        public void visitIincInsn(int var, int increment) {
            copy.visitIincInsn(var, increment);
            super.visitIincInsn(var, increment);
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            copy.visitTableSwitchInsn(min, max, dflt, labels);
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            copy.visitLookupSwitchInsn(dflt, keys, labels);
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            copy.visitMultiANewArrayInsn(descriptor, numDimensions);
            super.visitMultiANewArrayInsn(descriptor, numDimensions);
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return new CopyAnnotationVisitor(super.visitInsnAnnotation(typeRef, typePath, descriptor, visible),
                    copy.visitInsnAnnotation(typeRef, typePath, descriptor, visible));
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            copy.visitTryCatchBlock(start, end, handler, type);
            super.visitTryCatchBlock(start, end, handler, type);
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return new CopyAnnotationVisitor(super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible),
                    copy.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible));
        }

        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
            copy.visitLocalVariable(name, descriptor, signature, start, end, index);
            super.visitLocalVariable(name, descriptor, signature, start, end, index);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
            return new CopyAnnotationVisitor(
                    super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible),
                    copy.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible));
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            copy.visitLineNumber(line, start);
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            copy.visitMaxs(maxStack, maxLocals);
            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitEnd() {
            copy.visitEnd();
            super.visitEnd();
        }
    }

    private class CopyAnnotationVisitor extends AnnotationVisitor {
        private final AnnotationVisitor copy;

        public CopyAnnotationVisitor(AnnotationVisitor av, AnnotationVisitor copy) {
            super(Opcodes.ASM7, av);
            this.copy = copy;
        }

        @Override
        public void visit(String name, Object value) {
            copy.visit(name, value);
            super.visit(name, value);
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            copy.visitEnum(name, descriptor, value);
            super.visitEnum(name, descriptor, value);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            return new CopyAnnotationVisitor(super.visitAnnotation(name, descriptor),
                    copy.visitAnnotation(name, descriptor));
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new CopyAnnotationVisitor(super.visitArray(name), copy.visitArray(name));
        }

        @Override
        public void visitEnd() {
            copy.visitEnd();
            super.visitEnd();
        }
    }

}
