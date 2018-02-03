package org.jwrench.asm.adapters.reference;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ChangeMethodNameAdapter extends ClassVisitor implements Opcodes {

    private String desc;

    private boolean isFieldPresent = false;
    private boolean isMethodPresent = false;
    private boolean isStatic = false;

    private ClassVisitor next;

    private int retInsn;

    private String targetMethodName, wantedMethodName;

    public ChangeMethodNameAdapter(final ClassVisitor cv, String targetMethodName, String wantedMethodName){
        super(ASM4, new ClassNode());
        next = cv;
        this.targetMethodName = targetMethodName;
        this.wantedMethodName = wantedMethodName;
    }

    @Override
    public void visitEnd(){
        ClassNode cn = (ClassNode) cv;

        for(MethodNode mn : cn.methods){
            if(mn.name.equals(targetMethodName)){
                mn.name = wantedMethodName;
                System.out.println("    [!!M] Changed Method name: " + targetMethodName + " -> " + wantedMethodName);
            }
        }



        try{
            cn.accept(next);
        }catch (Exception ez)  {
            ez.printStackTrace();
        }

    }

}