/*
 This file is a part of the JWrench Project (https://github.com/HyperParam/JWrench).

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>

 ASM (OW2 Consortium)
 Copyright (c) 2000-2011 INRIA, France Telecom
    All rights reserved.

 */
package org.jwrench.asm.adapters.reference;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class AddGetterAdapter extends ClassVisitor implements Opcodes {

    private String fieldName = null;
    private String getterName = null;
    private String fieldDescriptor = null;
    private String signature = null;

    private boolean isFieldPresent = false;
    private boolean isMethodPresent = false;
    private boolean isStatic = false;

    private ClassVisitor next;

    private int retInsn;

    public AddGetterAdapter(final ClassVisitor cv, final String fieldName, final String fieldDescriptor, final String getterName, final int retInsn){
        super(ASM4, new ClassNode());
        next = cv;

        this.fieldName = fieldName;
        this.getterName = getterName;
        this.retInsn = retInsn;
        this.fieldDescriptor = fieldDescriptor;
    }

    @Override
    public void visitEnd(){
        ClassNode cn = (ClassNode) cv;

        for(FieldNode f : cn.fields){
            if(fieldName.equals(f.name) && fieldDescriptor.equals(f.desc)){
                isFieldPresent = true;
                signature = f.signature;
                if((f.access & ACC_STATIC) != 0){
                    isStatic = true;
                }
                break;
            }
        }

        for(MethodNode mv: cn.methods){
            if(getterName.equals(cn.name) && fieldDescriptor.equals(mv.desc)){
                isMethodPresent = true;
                break;
            }
        }

        if(isFieldPresent && !isMethodPresent){
            MethodNode mn = new MethodNode(ACC_PUBLIC, getterName, "()" + fieldDescriptor, signature, null);

            mn.instructions.add(new VarInsnNode(ALOAD, 0));
            mn.instructions.add(new FieldInsnNode(isStatic ? GETSTATIC : GETFIELD, cn.name, fieldName, fieldDescriptor));
            mn.instructions.add(new InsnNode(retInsn));

            mn.visitMaxs(3, 3);
            mn.visitEnd();
            cn.methods.add(mn);

            System.out.println("          [+M] " + fieldDescriptor + " " + getterName + "() identified as " +  cn.name + "." + fieldName);
        }


        try{
            cn.accept(next);
        }catch (Exception ez)  {
            ez.printStackTrace();
        }

    }

}
