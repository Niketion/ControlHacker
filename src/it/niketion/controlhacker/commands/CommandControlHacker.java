package it.niketion.controlhacker.commands;

import it.niketion.controlhacker.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class CommandControlHacker implements CommandExecutor {

    //
    //
    //

    private static Main main = Main.getInstance();

    //
    //
    //

    private static File locationFile = new File(main.getDataFolder() + "/controllocation.yml");
    private static FileConfiguration locationConfig = YamlConfiguration.loadConfiguration(locationFile);

    public static File getLocationFile() {
        return locationFile;
    }
    public static FileConfiguration getLocationConfig() {
        return locationConfig;
    }

    //
    //
    //

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (getNoErrors()) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("inv.nik.controlhacker.operator")) {
                        main.reloadConfig();
                        main.getLogger().log(Level.FINE, "Config reloaded.");

                        sender.sendMessage(prefixNormal + "Config reloaded.");
                        return true;
                    } else {
                        sender.sendMessage(prefixError + permissionDenied);
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("setlocation")) {
                    if (sender instanceof Player) {
                        if (args[1].equalsIgnoreCase("hacker")) {
                            if (setLocationControl((Player) sender, "hacker")) {
                                setLocationControl((Player) sender, "hacker");
                                sender.sendMessage(prefixNormal + "Location of HACKER set.");
                                return true;
                            } else {
                                main.getLogger().log(Level.WARNING, "Error: File location not found...");
                                return true;
                            }
                        } else if (args[1].equalsIgnoreCase("staffer")) {
                            if (setLocationControl((Player) sender, "staffer")) {
                                setLocationControl((Player) sender, "staffer");
                                sender.sendMessage(prefixNormal + "Location of STAFFER set.");
                                return true;
                            } else {
                                main.getLogger().log(Level.WARNING, "Error: File location not found...");
                                return true;
                            }
                        } else if (args[1].equalsIgnoreCase("endcontrol")) {
                            if (setLocationControl((Player) sender, "endcontrol")) {
                                setLocationControl((Player) sender, "endcontrol");
                                sender.sendMessage(prefixNormal + "Location of ENDControl set.");
                                return true;
                            } else {
                                main.getLogger().log(Level.WARNING, "Error: File location not found...");
                                return true;
                            }
                        } else {
                            sender.sendMessage(prefixError + argsNotFound);
                            return true;
                        }
                    } else {
                        sender.sendMessage(prefixError + noConsole);
                        return true;
                    }
                } else {
                    sender.sendMessage(prefixError + argsNotFound);
                    return true;
                }
            } else {
                sender.sendMessage(prefixError + argsNotFound);
                return true;
            }
        } else {
            sender.sendMessage(prefixError + "Error: View console.");
            return true;
        }
    }

    //
    //
    //

    private boolean getNoErrors() {
        if (main.loadCommands()) {
            if (main.loadConfig()) {
                if (main.getEnable()) {
                    return true;
                } else {
                    main.getLogger().log(Level.WARNING, "There is a error in console.");
                    return false;
                }
            } else {
                main.getLogger().log(Level.WARNING, "There is a error in console.");
                return false;
            }
        } else {
            main.getLogger().log(Level.WARNING, "There is a error in console.");
            return false;
        }
    }

    private boolean setLocationControl(Player player, String string) {
        if (locationFile.exists()) {
            try {
                Location loc = player.getLocation();
                locationConfig.set(string + ".X", loc.getX());
                locationConfig.set(string + ".Y", loc.getY());
                locationConfig.set(string + ".Z", loc.getZ());
                locationConfig.set(string + ".Yaw", loc.getYaw());
                locationConfig.set(string + ".Pitch", loc.getPitch());
                locationConfig.set(string + ".World", loc.getWorld().getName());

                locationConfig.save(locationFile);
                return true;
            } catch (IOException e) {
                main.getLogger().log(Level.WARNING, "Error: File can't save. IOException");
                return false;
            }
        } else {
            main.getLogger().log(Level.WARNING, "Error: File location not found...");
            return false;
        }
    }

    private String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }

    private String prefixNormal = main.getConfig().getString(format("prefix.prefix-normal")).replaceAll("&", "§");
    private String prefixError = main.getConfig().getString(format("prefix.prefix-error")).replaceAll("&", "§");

    private String permissionDenied = main.getConfig().getString(format("permissions.denied")).replaceAll("&", "§");
    private String argsNotFound = main.getConfig().getString(format("commands.args.not-found")).replaceAll("&", "§");
    private String noConsole = main.getConfig().getString(format("commands.player.no-console")).replaceAll("&", "§");
}
