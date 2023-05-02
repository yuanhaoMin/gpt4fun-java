package com.rua.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SharedDataUtils {

    private SharedDataUtils() {
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String toStringNullSafe(Object object) {
        return object != null ?
                object.toString() :
                "";
    }

}