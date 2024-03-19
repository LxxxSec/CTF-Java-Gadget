package com.xiinnn.tostring2getter;

import com.alibaba.fastjson.JSONArray;
import com.xiinnn.template.GetterClass;

import java.lang.reflect.Field;
import java.util.ArrayList;

// JSONArrayToString2Getter
// 实测：jdk8u192 fastjson#1.2.80 1.2.43
public class JSONArrayToString2Getter {
    public static void main(String[] args) throws Exception{
        GetterClass getterClass = new GetterClass();
        ArrayList arrayList = new ArrayList();
        arrayList.add(getterClass);
        JSONArray toStringBean = new JSONArray(arrayList);
        // GetterClass#getName is called
        toStringBean.toString();
    }
    public static void setFieldValue(Object obj, String field, Object val) throws Exception{
        Field dField = obj.getClass().getDeclaredField(field);
        dField.setAccessible(true);
        dField.set(obj, val);
    }
}
