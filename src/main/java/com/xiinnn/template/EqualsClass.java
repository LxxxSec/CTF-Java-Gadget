package com.xiinnn.template;

import java.io.Serializable;

public class EqualsClass implements Serializable {
    @Override
    public boolean equals(Object obj) {
        System.out.println("EqualsClass#equals is called");
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
