package com.hackclub.hccore.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static final int TICKS_PER_SECOND = 20;
    public static final int SECONDS_PER_MINUTE = 60;

    public static String toPrettyTime(int ticks) {
        return TimeUtil.toPrettyTime(ticks, false);
    }

    public static String toPrettyTime(int ticks, boolean full) {
        int totalSeconds = ticks / TimeUtil.TICKS_PER_SECOND;
        int days = (int) TimeUnit.SECONDS.toDays(totalSeconds);
        int hours = (int) (
            TimeUnit.SECONDS.toHours(totalSeconds) - TimeUnit.DAYS.toHours(days)
        );
        int minutes = (int) (
            TimeUnit.SECONDS.toMinutes(totalSeconds) -
            TimeUnit.DAYS.toMinutes(days) -
            TimeUnit.HOURS.toMinutes(hours)
        );
        int seconds = totalSeconds % TimeUtil.SECONDS_PER_MINUTE;

        String format = "";
        if (full) {
            format = "%1$dd %2$dh %3$dm %4$ds";
        } else {
            // Hide all zero values
            if (days != 0) {
                format += "%1$dd ";
            }
            if (hours != 0) {
                format += "%2$dh ";
            }
            if (minutes != 0) {
                format += "%3$dm ";
            }
            // Only show seconds if it's less than a minute
            if (seconds != 0 && totalSeconds < TimeUtil.SECONDS_PER_MINUTE) {
                format += "%4$ds";
            }
            format = format.trim();
        }

        return String.format(format, days, hours, minutes, seconds);
    }
}
