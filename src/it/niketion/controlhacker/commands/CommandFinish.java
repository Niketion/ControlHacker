package it.niketion.controlhacker.commands;

import io.puharesource.mc.titlemanager.api.TitleObject;
import it.niketion.controlhacker.Fuctions;
import it.niketion.controlhacker.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class CommandFinish implements CommandExecutor {

    //
    //
    //

    private static Main main = Main.getInstance();
    private Fuctions fuctions = Fuctions.getInstance();

    //
    //
    //

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (getNoErrors()) {
            if (args.length > 0) {
                Player hacker = Bukkit.getPlayerExact(args[0]);
                if (hacker != null) {
                    if (fuctions.hasPlayerControl(hacker.getName())) {
                        fuctions.removePlayerControl(hacker.getName());
                        finishControl(hacker, sender, main);
                        return true;
                    } else {
                        sender.sendMessage(prefixNormal + playerNotControlled);
                        return true;
                    }
                } else {
                    sender.sendMessage(prefixError + playerNotFound);
                    return true;
                }
            } else {
                sender.sendMessage(prefixError + playerNotFound);
                return true;
            }
        } else {
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

    private static String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }

    private static String prefixNormal = main.getConfig().getString(format("prefix.prefix-normal")).replaceAll("&", "§");
    private static String prefixError = main.getConfig().getString(format("prefix.prefix-error")).replaceAll("&", "§");

    public static void finishControl(Player hacker, CommandSender sender, Main main) {
        try {
            FileConfiguration config = CommandControlHacker.getLocationConfig();
            sender.sendMessage(prefixNormal + main.getConfig().getString("commands.player.finish-control.staffer")
                    .replaceAll("&", "§").replaceAll("%player%", hacker.getName()));
            hacker.sendMessage(prefixNormal + main.getConfig().getString(format("commands.player.finish-control.teleport-hacker"))
                    .replaceAll("&", "§").replaceAll("%staffer%", sender.getName()));
            hacker.teleport(
                    new Location(Bukkit.getWorld(config.getString("endcontrol.World")),
                            config.getDouble("endcontrol.X"),
                            config.getDouble("endcontrol.Y"),
                            config.getDouble("endcontrol.Z"),
                            (float) config.getDouble("endcontrol.Yaw"),
                            (float) config.getDouble("endcontrol.Pitch")));

            hacker.getWorld().playSound(hacker.getLocation(), Sound.valueOf(soundFinishControl), 100, 10);

            if (main.loadDepends()) {
                new TitleObject("", "").send(hacker);
            }
        } catch (Exception e) {
            sender.sendMessage(prefixError + "Error: View console.");
            main.getLogger().log(Level.WARNING, "Error: Control can't started. You've edited the controllocation?");
            main.getLogger().log(Level.WARNING, "Problem with sounds? Check sounds here: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html");
        }
    }

    private String playerNotFound = main.getConfig().getString(format("commands.player.not-found")).replaceAll("&", "§");
    private String playerNotControlled = main.getConfig().getString(format("commands.player.not-controlled")).replaceAll("&", "§");
    private static String soundFinishControl = main.getConfig().getString(format("commands.player.sound.finish-control.hacker"));
}
