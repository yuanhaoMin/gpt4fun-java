package com.rua.util;

import java.text.DecimalFormat;

public class SharedFormatUtils {

    private SharedFormatUtils() {
    }

    public static String convertMillisToStringWithMaxTwoFractionDigits(long number) {
        double decimal = number / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(decimal);
    }

}