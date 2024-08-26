package com.xiinnn.tostring2exec;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class CodeSignerToString2execAgent {
    public static void premain(String agentArgs, Instrumentation inst){
        inst.addTransformer(new ClassFileTransformer(){
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if(className.equals("java/security/cert/CertPath")){
                    try {
                        System.out.println(true);
                        ClassPool pool = ClassPool.getDefault();
                        CtClass ctClass = pool.get("java.security.cert.CertPath");
                        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
                        ctClass.removeMethod(writeReplace);
                        ctClass.detach();
                        return ctClass.toBytecode();
                    }catch (Exception e){
                        System.out.println(e);;
                    }

                }
                return  classfileBuffer;
            }
        });
    }
}