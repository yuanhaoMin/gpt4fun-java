package com.rua.util;

public class SharedDataUtils {

    private SharedDataUtils() {
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

}