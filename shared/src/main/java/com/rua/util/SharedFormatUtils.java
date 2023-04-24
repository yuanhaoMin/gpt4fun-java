package com.rua.util;

import java.text.DecimalFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SharedFormatUtils {

    private SharedFormatUtils() {
    }

    public static String convertMillisToStringWithMaxTwoFractionDigits(long number) {
        double decimal = number / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(decimal);
    }

    public static LocalDateTime getCurrentTimeInParis() {
        return LocalDateTime.now(Clock.system(ZoneId.of("Europe/Paris")));
    }

}