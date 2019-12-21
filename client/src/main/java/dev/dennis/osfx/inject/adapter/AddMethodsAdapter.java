package dev.dennis.osfx.inject.adapter;

import dev.dennis.osfx.inject.mixin.Copy;
import dev.dennis.osfx.inject.mixin.Mixin;
import dev.dennis.osfx.inject.mixin.Shadow;
import dev.dennis.osfx.inject.hook.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class AddMethodsAdapter extends ClassVisitor {
    private final Hooks hooks;

    private final Class<?> mixinClass;

    private final List<Method> methods;

    public AddMethodsAdapter(ClassVisitor classVisitor, Hooks hooks, Class<?> mixinClass, List<Method> methods) {
        super(Opcodes.ASM7, classVisitor);
        this.hooks = hooks;
        this.mixinClass = mixinClass;
        this.methods = methods;
    }

    @Override
    public void visitEnd() {
        try {
            ClassReader classReader = new ClassReader(mixinClass.getName());
            classReader.accept(new CopyMethodsVisitor(), ClassReader.EXPAND_FRAMES);
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.visitEnd();
    }

    private class CopyMethodsVisitor extends ClassVisitor {
        public CopyMethodsVisitor() {
            super(Opcodes.ASM7);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                         String[] exceptions) {
            for (Method method : methods) {
                if (method.getName().equals(name) && Type.getMethodDescriptor(method).equals(descriptor)) {
                    MethodVisitor mv = AddMethodsAdapter.super.visitMethod(access, name, descriptor, signature,
                            exceptions);
                    return new UpdateReferencesVisitor(mv, access, name, descriptor);
                }
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private class UpdateReferencesVisitor extends GeneratorAdapter {
        public UpdateReferencesVisitor(MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM7, mv, access, name, desc);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            if (owner.equals(Type.getType(mixinClass).getInternalName())) {
                Mixin mixin = mixinClass.getAnnotation(Mixin.class);
                ClassHook classHook = hooks.getClassHook(mixin.value());
                Field field;
                try {
                    field = mixinClass.getDeclaredField(name);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                if (field.isAnnotationPresent(Shadow.class)) {
                    Shadow shadow = field.getAnnotation(Shadow.class);
                    String fieldName = shadow.value();
                    if (fieldName.isEmpty()) {
                        fieldName = field.getName();
                    }
                    if (Modifier.isStatic(field.getModifiers())) {
                        StaticFieldHook fieldHook = hooks.getStaticField(fieldName);
                        if (fieldHook == null) {
                            throw new IllegalStateException("No static field hook found for shadow field "
                                    + fieldName);
                        }
                        owner = fieldHook.getOwner();
                        name = fieldHook.getName();
                        descriptor = Type.getType(fieldHook.getDesc()).getDescriptor();
                    } else {
                        FieldHook fieldHook = classHook.getField(fieldName);
                        if (fieldHook == null) {
                            throw new IllegalStateException("No field hook found for shadow field " + fieldName);
                        }
                        owner = classHook.getName();
                        name = fieldHook.getName();
                        descriptor = Type.getType(fieldHook.getDesc()).getDescriptor();
                    }
                } else {
                    owner = classHook.getName();
                }
            }
            super.visitFieldInsn(opcode, owner, name, descriptor);
//            if (!descriptor.equals(oldDescriptor)) {
//                checkCast(Type.getObjectType(oldDescriptor));
//            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (owner.equals(Type.getType(mixinClass).getInternalName())) {
                Mixin mixin = mixinClass.getAnnotation(Mixin.class);
                ClassHook classHook = hooks.getClassHook(mixin.value());
                owner = classHook.getName();
                Method method = null;
                for (Method m : mixinClass.getDeclaredMethods()) {
                    if (m.getName().equals(name) && Type.getMethodDescriptor(m).equals(descriptor)) {
                        method = m;
                        break;
                    }
                }
                if (method != null) {
                    String hookName = null;
                    if (method.isAnnotationPresent(Copy.class)) {
                        hookName = method.getAnnotation(Copy.class).value();
                    }
                    if (hookName != null) {
                        MethodHook methodHook = classHook.getMethod(hookName);
                        if (methodHook == null) {
                            throw new IllegalStateException("No method hook found for " + mixin.value() + "." + hookName);
                        }
                        descriptor = methodHook.getDesc();
                        if (methodHook.getDummyValue() != null) {
                            push(methodHook.getDummyValue());
                        }
                    }
                }
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
