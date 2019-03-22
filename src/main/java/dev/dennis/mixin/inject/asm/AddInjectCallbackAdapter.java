package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

public class AddInjectCallbackAdapter extends ClassVisitor {
    private final java.lang.reflect.Method method;

    private final String targetMethodName;

    private final String targetMethodDesc;

    private final boolean end;

    private String owner;

    public AddInjectCallbackAdapter(java.lang.reflect.Method method, String targetMethodName, String targetMethodDesc,
                                    boolean end, ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
        this.method = method;
        this.targetMethodName = targetMethodName;
        this.targetMethodDesc = targetMethodDesc;
        this.end = end;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals(targetMethodName) && descriptor.equals(targetMethodDesc)) {
            return new AddCallbackMethodAdapter(mv, access, name, descriptor);
        } else {
            return mv;
        }
    }

    private class AddCallbackMethodAdapter extends AdviceAdapter {
        protected AddCallbackMethodAdapter(MethodVisitor mv, int access, String name, String descriptor) {
            super(Opcodes.ASM7, mv, access, name, descriptor);
        }

        private void invokeCallback() {
            loadThis();
            invokeVirtual(Type.getObjectType(owner), Method.getMethod(method));
        }

        @Override
        protected void onMethodEnter() {
            if (!end) {
                invokeCallback();
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            if (end) {
                invokeCallback();
            }
        }
    }
}
