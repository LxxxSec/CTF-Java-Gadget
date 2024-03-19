package com.xiinnn.template;

import java.io.Serializable;

public class ToStringClass implements Serializable {
    public String a = "asd";
    @Override
    public String toString() {
        System.out.println("ToStringClass#toString is called");
        return super.toString();
    }
}
