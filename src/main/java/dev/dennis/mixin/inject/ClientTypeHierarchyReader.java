package dev.dennis.mixin.inject;

import org.mutabilitydetector.asm.typehierarchy.TypeHierarchy;
import org.mutabilitydetector.asm.typehierarchy.TypeHierarchyReader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientTypeHierarchyReader extends TypeHierarchyReader {
    private Map<String, byte[]> classes;

    @Override
    public TypeHierarchy hierarchyOf(Type t) {
        if (t.getSort() != Type.OBJECT || classes == null) {
            return super.hierarchyOf(t);
        }
        byte[] classData = classes.get(t.getInternalName());
        if (classData == null) {
            return super.hierarchyOf(t);
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classData);
        classReader.accept(classNode, 0);

        boolean isInterface = (classNode.access & Opcodes.ACC_INTERFACE) != 0;

        Type superType;
        if (classNode.superName == null || isInterface) {
            superType = null;
        } else {
            superType = Type.getObjectType(classNode.superName);
        }
        List<Type> interfaceTypes = classNode.interfaces.stream()
                .map(Type::getObjectType)
                .collect(Collectors.toList());
        return new TypeHierarchy(Type.getObjectType(classNode.name), superType, interfaceTypes,
                isInterface);
    }

    public Map<String, byte[]> getClasses() {
        return classes;
    }

    public void setClasses(Map<String, byte[]> classes) {
        this.classes = classes;
    }
}
