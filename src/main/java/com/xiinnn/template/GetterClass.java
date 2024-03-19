package com.xiinnn.template;

import java.io.Serializable;

public class GetterClass implements Serializable {
    public String name;

    public String getName() {
        System.out.println("GetterClass#getName is called");
        return name;
    }
}
