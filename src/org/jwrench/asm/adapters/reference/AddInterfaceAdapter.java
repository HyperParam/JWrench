package org.jwrench.asm.adapters.reference;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;


public class AddInterfaceAdapter extends ClassVisitor implements Opcodes {

    private String[] interfacesToAdd;
    private ClassVisitor next;


    public AddInterfaceAdapter(final ClassVisitor cv, final String... interfacesToAdd) {
        super(ASM4, new ClassNode());
        next = cv;

        this.interfacesToAdd = interfacesToAdd;
    }


    @Override
    public void visitEnd() {
        ClassNode cn = (ClassNode) cv;

        for(String i : interfacesToAdd) {
            cn.interfaces.add(i);
            System.out.println("    [+I] " + cn.name + " implements " + i);
        }

        cn.accept(next);

    }
}
