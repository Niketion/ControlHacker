package it.nik.controlhacker.commands;

import it.nik.controlhacker.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandReport implements CommandExecutor {
    private ArrayList<String> cooldown = new ArrayList<>();
    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0) {
            if (Bukkit.getPlayerExact(strings[0]) != null) {
                Player target = Bukkit.getPlayerExact(strings[0]);
                if (!(commandSender.equals(target))) {
                    if (strings.length > 1) {
                        if (!(cooldown.contains(commandSender.getName()))) {
                            String message = "";
                            for (int i = 1; i != strings.length; i++)
                                message += strings[i] + " ";
                            formatChatPrefix(commandSender, getConfigString("Report.Thanks-For-Report").replaceAll("%player%", target.getName()));
                            cooldown.add(commandSender.getName());
                            for (Player staffer : Bukkit.getServer().getOnlinePlayers()) {
                                if (staffer.hasPermission("controlhacker.mod")) {
                                    formatChat(staffer, (getConfigString("Report.Report-Message"))
                                            .replaceAll("%player%", commandSender.getName())
                                            .replaceAll("%reason%", message)
                                            .replaceAll("%reported%", target.getName()));
                                }
                            }
                            main.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> cooldown.remove(commandSender.getName()), 20 * main.getConfig().getInt("Report.Cooldown"));
                            return true;
                        } else {
                            commandSender.sendMessage(main.getConfig().getString("Report.Cooldown-Message").replaceAll
                                    ("%number%", String.valueOf(main.getConfig().getInt("Report.Cooldown"))).replaceAll("&", "§"));
                            return true;
                        }
                    } else {
                        formatChatPrefix(commandSender, getConfigString("Report.Report-Usage"));
                        return true;
                    }
                } else {
                    formatChatPrefix(commandSender, getConfigString("Report.Report-Yourself"));
                    return true;
                }
            } else {
                formatChatPrefix(commandSender, (getConfigString("Chat.Player-Not-Found")).replaceAll("%player%", strings[0]));
                return true;
            }
        } else {
            formatChatPrefix(commandSender, getConfigString("Report.Report-Usage"));
            return true;
        }
    }

    private void formatChatPrefix(CommandSender sender, String message) {
        sender.sendMessage(main.formatChat(Main.getInstance().getConfig().getString("Report.Prefix") + " " + message));
    }

    private void formatChat(Player player, String message) {
        player.sendMessage(main.formatChat(message));
    }

    private String getConfigString(String string) {
        return main.getConfig().getString(string);
    }
}
