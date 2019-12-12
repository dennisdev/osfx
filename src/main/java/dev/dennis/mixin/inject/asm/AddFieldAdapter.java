package dev.dennis.mixin.inject.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class AddFieldAdapter extends ClassVisitor {
    private final int fieldAccess;

    private final String fieldName;

    private final String fieldDesc;

    private boolean exists;

    public AddFieldAdapter(ClassVisitor classVisitor, int fieldAccess, String fieldName, String fieldDesc) {
        super(Opcodes.ASM7, classVisitor);
        this.fieldAccess = fieldAccess;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (name.equals(fieldName)) {
            exists = true;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        if (exists) {
            throw new IllegalStateException("Field " + fieldName + " already exists");
        }
        visitField(fieldAccess, fieldName, fieldDesc, null, null);
        super.visitEnd();
    }
}
