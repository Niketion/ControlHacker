package com.controlhacker.bukkit.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PlaceholderUtil {
    public static String apply(String message, String playerName, String staffName) {
        if (message == null) {
            return null;
        }
        return message.replace("%player%", playerName).replace("%staff%", staffName);
    }
}
