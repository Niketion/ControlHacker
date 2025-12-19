package com.controlhacker.bukkit.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class AdventureUtil {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public static Component component(String message) {
        if (message == null) {
            return Component.empty();
        }
        return LEGACY.deserialize(message);
    }
}
