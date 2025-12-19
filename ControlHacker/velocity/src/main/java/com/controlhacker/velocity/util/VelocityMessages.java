package com.controlhacker.velocity.util;

import com.velocitypowered.api.command.CommandSource;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;

@UtilityClass
public class VelocityMessages {
    public static void send(CommandSource source, String prefix, String message) {
        if (message == null) {
            return;
        }
        source.sendMessage(Component.text(prefix + " " + message));
    }
}
