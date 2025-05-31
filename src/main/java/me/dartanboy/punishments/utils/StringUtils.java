package me.dartanboy.punishments.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String colorize(String message) {
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = matcher.group(0);
            message = message.replace(color, ChatColor.of(color).toString());
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
