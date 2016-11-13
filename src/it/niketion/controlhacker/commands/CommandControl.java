package it.niketion.controlhacker.commands;

import io.puharesource.mc.titlemanager.api.TitleObject;
import it.niketion.controlhacker.Fuctions;
import it.niketion.controlhacker.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.logging.Level;

public class CommandControl implements CommandExecutor {

    //
    //
    //

    private Main main = Main.getInstance();
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
                    if (sender instanceof Player) {
                        if (sender != hacker) {
                            if (sender.hasPermission("inv.nik.controlhacker.mod")) {
                                if (!hacker.hasPermission("inv.nik.controlhacker.mod")) {
                                    if (!fuctions.hasPlayerControl(hacker.getName())) {
                                        if (controlStart(hacker, (Player) sender)) {
                                            fuctions.addPlayerControl(hacker.getName());
                                            return true;
                                        } else {
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage(prefixError + alreadyControl);
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage(prefixError + hackerIsStaffer);
                                    return true;
                                }
                            } else {
                                sender.sendMessage(prefixError + permissionDenied);
                                return true;
                            }
                        } else {
                            sender.sendMessage(prefixError + senderEqualsTarget);
                            return true;
                        }
                    } else {
                        sender.sendMessage(prefixError + noConsole);
                        return true;
                    }
                } else {
                    sender.sendMessage(prefixNormal + playerNotFound);
                    return true;
                }
            } else {
                sender.sendMessage(prefixError + argsNotFound);
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

    private String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }

    private String prefixNormal = main.getConfig().getString(format("prefix.prefix-normal")).replaceAll("&", "§");
    private String prefixError = main.getConfig().getString(format("prefix.prefix-error")).replaceAll("&", "§");

    private boolean controlStart(Player hacker, Player staffer) {
        try {
            FileConfiguration config = CommandControlHacker.getLocationConfig();
            if (config.getString("staffer.World") != null) {
                if (config.getString("hacker.World") != null) {
                    if (config.getString("endcontrol.World") != null) {
                        staffer.sendMessage(prefixNormal + main.getConfig().getString(format("commands.player.start-control.staffer"))
                                .replaceAll("&", "§").replaceAll("%player%", hacker.getName()));
                        staffer.teleport(new Location(Bukkit.getWorld(config.getString("staffer.World")),
                                config.getDouble("staffer.X"),
                                config.getDouble("staffer.Y"),
                                config.getDouble("staffer.Z"),
                                (float) config.getDouble("staffer.Yaw"),
                                (float) config.getDouble("staffer.Pitch")));
                        hacker.teleport(new Location(Bukkit.getWorld(config.getString("hacker.World")),
                                config.getDouble("hacker.X"),
                                config.getDouble("hacker.Y"),
                                config.getDouble("hacker.Z"),
                                (float) config.getDouble("hacker.Yaw"),
                                (float) config.getDouble("hacker.Pitch")));
                        hacker.getWorld().playEffect(hacker.getLocation(), Effect.EXPLOSION_LARGE, 0);
                        hacker.getWorld().playSound(hacker.getLocation(), Sound.valueOf(soundStartControlHacker), 100, 1);
                        staffer.getWorld().playSound(staffer.getLocation(), Sound.valueOf(soundStartControlStaffer), 100, 1);

                        if (main.loadDepends()) {
                            new TitleObject(
                                    main.getConfig().getString("commands.player.start-control.hacker.title")
                                            .replaceAll("&", "§")
                                            .replaceAll("%staffer%", staffer.getName()),
                                    main.getConfig().getString("commands.player.start-control.hacker.subtitle")
                                            .replaceAll("&", "§")
                                            .replaceAll("%staffer%", staffer.getName()))
                                    .setFadeIn(main.getConfig().getInt("commands.player.start-control.hacker.fade-in"))
                                    .setFadeOut(main.getConfig().getInt("commands.player.start-control.hacker.fade-out"))
                                    .setStay(999999999)
                                    .send(hacker);
                            return true;
                        }
                        return true;
                    } else {
                        staffer.sendMessage(prefixError + "Location of ENDControl not set.");
                        return false;
                    }
                } else {
                    staffer.sendMessage(prefixError + "Location of HACKER not set.");
                    return false;
                }
            } else {
                staffer.sendMessage(prefixError + "Location of STAFFER not set.");
                return false;
            }
        } catch (Exception e) {
            staffer.sendMessage(prefixError + "Error: View console.");
            main.getLogger().log(Level.WARNING, "Error: Control can't started. You've edited the controllocation?");
            main.getLogger().log(Level.WARNING, "Problem with sounds? Check sounds here: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html");
            return false;
        }
    }

    private String argsNotFound = main.getConfig().getString(format("commands.args.not-found")).replaceAll("&", "§");
    private String playerNotFound = main.getConfig().getString(format("commands.player.not-found")).replaceAll("&", "§");
    private String noConsole = main.getConfig().getString(format("commands.player.no-console")).replaceAll("&", "§");
    private String alreadyControl = main.getConfig().getString(format("commands.player.already-control")).replaceAll("&", "§");
    private String senderEqualsTarget = main.getConfig().getString(format("commands.player.staffer-equals-hacker")).replaceAll("&", "§");
    private String hackerIsStaffer = main.getConfig().getString(format("commands.player.hacker-is-staffer")).replaceAll("&", "§");
    private String soundStartControlHacker = main.getConfig().getString("commands.player.sound.start-control.hacker");
    private String soundStartControlStaffer = main.getConfig().getString("commands.player.sound.start-control.staffer");
    private String permissionDenied = main.getConfig().getString(format("permissions.denied")).replaceAll("&", "§");
}
