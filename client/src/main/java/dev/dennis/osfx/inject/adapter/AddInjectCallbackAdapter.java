package dev.dennis.osfx.inject.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

public class AddInjectCallbackAdapter extends ClassVisitor {
    private final String targetName;

    private final String targetDesc;

    private final String callbackName;

    private final String callbackDesc;

    private final boolean end;

    private String owner;

    public AddInjectCallbackAdapter(ClassVisitor classVisitor, String targetName, String targetDesc,
                                    String callbackName, String callbackDesc, boolean end) {
        super(Opcodes.ASM7, classVisitor);
        this.targetName = targetName;
        this.targetDesc = targetDesc;
        this.callbackName = callbackName;
        this.callbackDesc = callbackDesc;
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
        if (name.equals(targetName) && (targetDesc == null || descriptor.equals(targetDesc))) {
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
            invokeVirtual(Type.getObjectType(owner), new Method(callbackName, callbackDesc));
        }

        @Override
        protected void onMethodEnter() {
            if (!end) {
                invokeCallback();
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            if (end && opcode != Opcodes.ATHROW) {
                invokeCallback();
            }
        }
    }
}
