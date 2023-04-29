package com.rua.util;

import java.text.DecimalFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SharedFormatUtils {

    private SharedFormatUtils() {
    }

    public static String createLogMessage(final String methodName, final long startTimestamp, final String... args) {
        final var logBuilder = new StringBuilder();
        final var endTimestamp = System.currentTimeMillis();
        final var elapsedTime = SharedFormatUtils.convertMillisToStringWithMaxTwoFractionDigits(endTimestamp - startTimestamp);
        logBuilder.append(methodName) //
                .append(" ") //
                .append(elapsedTime) //
                .append("s.");
        for (final var arg : args) {
            logBuilder.append(" ") //
                    .append(arg);
        }
        return logBuilder.toString();
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