package com.jesus.opencms.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jesus on 27/08/2015.
 */
public class Test {
    public static void main(String [ ] args){
        Map<String, String> map = new HashMap<String, String>();
        map.put("hola", "1");
        map.put("hola", "2");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.print("key: " + entry.getKey());
            System.out.print("value: " + entry.getValue());
        }
    }
}
