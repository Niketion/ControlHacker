package it.nik.controlhacker.commands;

import it.nik.controlhacker.Main;
import it.nik.controlhacker.files.FileLocation;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class CommandControlHacker implements CommandExecutor {
    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0) {
            if (commandSender.hasPermission("controlhacker.admin")) {
                if (strings[0].equalsIgnoreCase("setlocation")) {
                    if (strings.length > 1) {
                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;
                            if (strings[1].equalsIgnoreCase("hacker")) {
                                saveLocation(player, "Hacker");
                                return true;
                            } else if (strings[1].equalsIgnoreCase("staffer")) {
                                saveLocation(player, "Staffer");
                                return true;
                            } else if (strings[1].equalsIgnoreCase("endcontrol")) {
                                saveLocation(player, "EndControl");
                                return true;
                            } else {
                                usageCommand(commandSender);
                                return true;
                            }
                        } else {
                            main.log(Level.INFO, "The command is executable only by player");
                            return true;
                        }
                    } else {
                        usageCommand(commandSender);
                        return true;
                    }
                } else if (strings[0].equalsIgnoreCase("reload")) {
                    main.reloadConfig();
                    main.reloadConfig();
                    commandSender.sendMessage(main.formatChatPrefix("&7'config.yml' reloaded."));
                    return true;
                } else {
                    usageCommand(commandSender);
                    return true;
                }
            } else {
                commandSender.sendMessage(main.formatChatPrefix(main.getConfig().getString("Chat.Permission-Denied")));
                return true;
            }
        } else {
            usageCommand(commandSender);
            return true;
        }
    }

    private void saveLocation(Player player, String name) {
        FileConfiguration fileConfiguration = FileLocation.getInstance().getLocationConfig();
        Location location = player.getLocation();

        fileConfiguration.set(name + ".World", location.getWorld().getName());
        fileConfiguration.set(name + ".X", location.getX());
        fileConfiguration.set(name + ".Y", location.getY());
        fileConfiguration.set(name + ".Z", location.getZ());
        fileConfiguration.set(name + ".Yaw", location.getYaw());
        fileConfiguration.set(name + ".Pitch", location.getPitch());
        FileLocation.getInstance().saveConfig();

        player.sendMessage(main.formatChatPrefix("&7Location of &d" + name + "&7 set."));
    }

    private void usageCommand(CommandSender commandSender) {
        commandSender.sendMessage(main.formatChatPrefix("&7Author - Version: &a" + main.getDescription().getAuthors() + " &7-&a " + main.getDescription().getVersion()));
        commandSender.sendMessage(main.formatChatPrefix("&7INFO:"));
        commandSender.sendMessage(main.formatChat("&7> /ch setlocation (hacker/staffer/endcontrol) - Set location"));
        commandSender.sendMessage(main.formatChat("&7> /ch reload - Reload 'config.yml'"));
    }
}
