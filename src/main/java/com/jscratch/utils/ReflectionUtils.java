package com.jscratch.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    public static String[] getAvailableMethods(Class<?> clazz) {
        List<String> methods = new ArrayList<>();
        for (Method m : clazz.getMethods()) {
            if (m.getDeclaringClass() != Object.class) {
                methods.add(m.getName());
            }
        }
        return methods.toArray(new String[0]);
    }
}
