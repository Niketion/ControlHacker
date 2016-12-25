package it.nik.controlhacker.commands;

import it.nik.controlhacker.Fuctions;
import it.nik.controlhacker.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandFinish implements CommandExecutor {
    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        FileConfiguration fileConfiguration = main.getConfig();
        if (strings.length > 0) {
            if (commandSender.hasPermission("controlhacker.mod")) {
                if (Bukkit.getPlayerExact(strings[0]) != null) {
                    if (Fuctions.getInstance().isOnControl(Bukkit.getPlayerExact(strings[0]))) {
                        Fuctions.getInstance().finishControl(Bukkit.getPlayerExact(strings[0]), commandSender);
                        return true;
                    } else {
                        commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Chat.Player-Not-Controlled")
                                .replaceAll("%player%", strings[0])));
                        return true;
                    }
                } else {
                    commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Chat.Player-Not-Found")
                            .replaceAll("%player%", strings[0])));
                    return true;
                }
            } else {
                commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Chat.Permission-Denied")));
                return true;
            }
        } else {
            commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Chat.Usage-Control")));
            return true;
        }
    }
}
