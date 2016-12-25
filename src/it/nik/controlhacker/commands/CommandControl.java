package it.nik.controlhacker.commands;

import io.puharesource.mc.titlemanager.api.TitleObject;
import it.nik.controlhacker.Fuctions;
import it.nik.controlhacker.Main;
import it.nik.controlhacker.files.FileLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class CommandControl implements CommandExecutor {
    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        FileConfiguration fileConfiguration = main.getConfig();
        if (strings.length > 0) {
            if (commandSender.hasPermission("controlhacker.mod")) {
                if (Bukkit.getPlayerExact(strings[0]) != null) {
                    if (commandSender instanceof Player) {
                        Player target = Bukkit.getPlayerExact(strings[0]);
                        if (!(commandSender.equals(target))) {
                            if (!(Fuctions.getInstance().isOnControl(target))) {
                                Player playerCommand = (Player) commandSender;

                                if (teleport("Staffer", playerCommand)) ;
                                if (teleport("Hacker", target)) ;

                                Fuctions.getInstance().addToControl(target);

                                playerCommand.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Chat.Start-Control.Staffer")
                                        .replaceAll("%player%", strings[0])));
                                target.sendMessage(main.formatChat(fileConfiguration.getString("Chat.Start-Control.Hacker")
                                        .replaceAll("%player%", playerCommand.getName())));

                                if (main.depends()) {
                                    new TitleObject(main.formatChat(fileConfiguration.getString("Title.Start-Control.Title")), TitleObject.TitleType.TITLE)
                                            .setSubtitle(main.formatChat(fileConfiguration.getString("Title.Start-Control.SubTitle")
                                                    .replaceAll("%player%", playerCommand.getName())))
                                            .setFadeIn(0)
                                            .setStay(Integer.MAX_VALUE)
                                            .setFadeOut(0)
                                            .send(target);
                                }
                                return true;
                            } else {
                                commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Chat.Already-Control")));
                                return true;
                            }
                        } else {
                            commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Chat.Control-Yourself")));
                            return true;
                        }
                    } else {
                        main.log(Level.INFO, "The command is executable only by player");
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

    private boolean teleport(String name, Player player) {
        FileConfiguration fileConfiguration = FileLocation.getInstance().getLocationConfig();
        if (fileConfiguration.getString(name + ".World") != null) {
            player.teleport(new Location(Bukkit.getWorld(fileConfiguration.getString(name + ".World")),
                    fileConfiguration.getInt(name + ".X"),
                    fileConfiguration.getInt(name + ".Y"),
                    fileConfiguration.getInt(name + ".Z"),
                    fileConfiguration.getInt(name + ".Yaw"),
                    fileConfiguration.getInt(name + ".Pitch")));
            try {
                player.playSound(player.getLocation(), Sound.valueOf(main.getConfig().getString("Sound.Start-Control")
                        .toUpperCase().replaceAll("-", "_")), 1, 0);
            } catch (NullPointerException e) {
                main.formatMessageError("Sound not found... Change it.");
            }
            return true;
        } else {
            Bukkit.broadcastMessage(main.formatChatPrefix("&7Location of &d" + name + " &7not set."));
            return false;
        }
    }
}
