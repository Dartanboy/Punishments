package me.dartanboy.punishments.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([smhd])");

    public static long parseDuration(String input) {
        Matcher matcher = TIME_PATTERN.matcher(input.toLowerCase());
        long millis = 0;

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            switch (matcher.group(2)) {
                case "s" -> millis += value * 1000L;
                case "m" -> millis += value * 60_000L;
                case "h" -> millis += value * 60 * 60_000L;
                case "d" -> millis += value * 24 * 60 * 60_000L;
            }
        }

        return millis;
    }
}
