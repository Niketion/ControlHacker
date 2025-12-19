package com.controlhacker.bukkit.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ColorUtil {
    public static String colorize(String message) {
        if (message == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> colorize(List<String> lines) {
        List<String> colored = new ArrayList<>();
        if (lines == null) {
            return colored;
        }
        for (String line : lines) {
            colored.add(colorize(line));
        }
        return colored;
    }
}
