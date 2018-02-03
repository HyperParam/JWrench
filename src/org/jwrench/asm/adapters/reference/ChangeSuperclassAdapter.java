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

import java.util.Iterator;


public class ChangeSuperclassAdapter extends ClassVisitor implements Opcodes {

    private String superClass;
    private ClassVisitor next;


    public ChangeSuperclassAdapter(final ClassVisitor cv, final String superClass) {
        super(ASM4, new ClassNode());
        next = cv;

        this.superClass = superClass;
    }


    @Override
    public void visitEnd() {
        ClassNode cn = (ClassNode) cv;

        for(MethodNode mn : cn.methods){
            InsnList il = mn.instructions;
            Iterator<AbstractInsnNode> it = il.iterator();

            while(it.hasNext()){
                AbstractInsnNode ain = it.next();

                if(ain instanceof MethodInsnNode){
                    MethodInsnNode m = (MethodInsnNode)ain;
                    if((m.getOpcode() == INVOKESPECIAL)){
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if(min.owner.equals(cn.superName)){
                            min.owner  = superClass;
                        }
                        break;
                    }
                }
            }
        }

        cn.superName = superClass;


        System.out.println("    [~S] " + cn.name + " extends " + cn.superName);

        cn.accept(next);

    }
}
