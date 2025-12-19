package com.controlhacker.bukkit.util;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class AdventureAudience implements AutoCloseable {
    private final BukkitAudiences audiences;

    public static AdventureAudience create(JavaPlugin plugin) {
        return new AdventureAudience(BukkitAudiences.create(plugin));
    }

    public void send(CommandSender sender, String message) {
        audiences.sender(sender).sendMessage(AdventureUtil.component(message));
    }

    @Override
    public void close() {
        audiences.close();
    }
}
