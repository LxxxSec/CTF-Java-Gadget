package com.xiinnn.tostring2put;

import com.xiinnn.template.PutMapClass;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.lang.reflect.Field;
import java.util.HashMap;

// TiedMapEntry#toString -> put(key,  value)
// 实测：commons-collections#3.2.2
public class TiedMapEntryToString2Put {
    public static void main(String[] args) throws Exception{
        ConstantTransformer constantTransformer = new ConstantTransformer(1);
        PutMapClass putMapClass = new PutMapClass();

        LazyMap lazymap = (LazyMap) LazyMap.decorate(putMapClass, constantTransformer);
        LazyMap lazymap1 = (LazyMap) LazyMap.decorate(new HashMap(), constantTransformer);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazymap1, "useless");
        setFieldValue(tiedMapEntry, "map", lazymap);
        setFieldValue(tiedMapEntry, "key", "useless");

        // 成功调用 PutMapClass#put
        tiedMapEntry.toString();
    }
    public static void setFieldValue(Object obj, String field, Object val) throws Exception{
        Field dField = obj.getClass().getDeclaredField(field);
        dField.setAccessible(true);
        dField.set(obj, val);
    }
}
