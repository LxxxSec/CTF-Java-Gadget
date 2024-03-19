package com.xiinnn.template;

import java.util.HashMap;

public class PutMapClass extends HashMap {
    public Object put(Object key, Object value){
        System.out.println("PutMapClass#put is called");
        return true;
    }
}
