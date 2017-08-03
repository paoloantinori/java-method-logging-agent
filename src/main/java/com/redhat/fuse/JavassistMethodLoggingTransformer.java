package com.redhat.fuse;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class JavassistMethodLoggingTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if("dummy/DummyMain".equals(className)) {
                System.out.println("==transforming class "+ className);

                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(className.replace("/", "."));
                for (CtMethod m : cc.getDeclaredMethods()) {
                    m.addLocalVariable("elapsedTime", CtClass.longType);
                    m.insertBefore("elapsedTime = System.currentTimeMillis();");
                    m.insertBefore("System.err.println(\"Enter method " + m.getLongName() + " - \"+ Thread.currentThread().getName());");
                    m.insertAfter("{elapsedTime = System.currentTimeMillis() - elapsedTime;"
                            + "System.out.println(\"Method [" + m.getLongName() + "] Executed in ms: \" + elapsedTime);}");
                    m.insertAfter("System.err.println(\"Exit method " + m.getLongName() + " - \"+ Thread.currentThread().getName());");
                }

                byte[] byteCode = cc.toBytecode();
                cc.detach();
                return byteCode;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
