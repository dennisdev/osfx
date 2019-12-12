package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.*;

import java.applet.Applet;
import java.awt.*;

public class AppletToPanelAdapter extends ClassVisitor {
    private static final Type APPLET_TYPE = Type.getType(Applet.class);

    private static final Type PANEL_TYPE = Type.getType(Panel.class);

    private final Type gameEngineType;

    public AppletToPanelAdapter(ClassVisitor classVisitor, Type gameEngineType) {
        super(Opcodes.ASM7, classVisitor);
        this.gameEngineType = gameEngineType;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (APPLET_TYPE.getInternalName().equals(superName)) {
            superName = PANEL_TYPE.getInternalName();
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (Type.getType(descriptor).equals(APPLET_TYPE)) {
            descriptor = gameEngineType.getDescriptor();
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        Type[] argTypes = Type.getArgumentTypes(descriptor);
        for (int i = 0; i < argTypes.length; i++) {
            if (argTypes[i].equals(APPLET_TYPE)) {
                argTypes[i] = gameEngineType;
            }
        }
        descriptor = Type.getMethodDescriptor(Type.getReturnType(descriptor), argTypes);
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new AppletToPanelMethodAdapter(mv);
    }

    private class AppletToPanelMethodAdapter extends MethodVisitor {
        AppletToPanelMethodAdapter(MethodVisitor methodVisitor) {
            super(Opcodes.ASM7, methodVisitor);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            if (Type.getType(descriptor).equals(APPLET_TYPE)) {
                super.visitFieldInsn(opcode, owner, name, gameEngineType.getDescriptor());
            } else {
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }
            if (Type.getType(descriptor).equals(APPLET_TYPE)
                    && (opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC)) {
                super.visitInsn(Opcodes.POP);
                super.visitInsn(Opcodes.ACONST_NULL);
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (owner.equals(APPLET_TYPE.getInternalName()) && name.equals("<init>")) {
                owner = PANEL_TYPE.getInternalName();
            } else if (owner.equals("netscape/javascript/JSObject") && name.equals("getWindow")) {
                super.visitInsn(Opcodes.POP);
                super.visitInsn(Opcodes.ACONST_NULL);
            } else {
                Type[] argTypes = Type.getArgumentTypes(descriptor);
                for (int i = 0; i < argTypes.length; i++) {
                    if (argTypes[i].equals(APPLET_TYPE)) {
                        argTypes[i] = gameEngineType;
                    }
                }
                descriptor = Type.getMethodDescriptor(Type.getReturnType(descriptor), argTypes);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
