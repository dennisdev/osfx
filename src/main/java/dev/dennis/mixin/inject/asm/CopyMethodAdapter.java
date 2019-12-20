package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class CopyMethodAdapter extends ClassVisitor {
    private final String name;

    private final String desc;

    private final ClassVisitor destClassVisitor;

    private final String destName;

    private MethodNode methodNode;

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
        if (name.equals(this.name) && descriptor.equals(this.desc) && methodNode == null) {
            methodNode = new MethodNode(access, name, descriptor, signature, exceptions);
            return methodNode;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        if (methodNode != null) {
            methodNode.accept(this);
            methodNode.name = destName;
            methodNode.accept(destClassVisitor);
        }
        super.visitEnd();
    }
}
