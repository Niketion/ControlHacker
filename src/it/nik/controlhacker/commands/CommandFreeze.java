package it.nik.controlhacker.commands;

import it.nik.controlhacker.Fuctions;
import it.nik.controlhacker.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandFreeze implements CommandExecutor {
    private Main main = Main.getInstance();
    private Fuctions fuctions = Fuctions.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        FileConfiguration fileConfiguration = Main.getInstance().getConfig();

        if (!fileConfiguration.getBoolean("Freeze.Active")) {
            commandSender.sendMessage(main.formatChatPrefix("&cFreeze disabled..."));
            return true;
        }

        if (!(strings.length > 0)) {
            commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Freeze.Usage")));
            return true;
        }

        if (Bukkit.getPlayerExact(strings[0]) == null) {
            commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Freeze.Player-Not-Found")));
            return true;
        }

        Player target = Bukkit.getPlayerExact(strings[0]);

        if (Fuctions.getInstance().isFreezed(target)) {
            commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Freeze.Player-Is-Freezed")));
            return true;
        }

        fuctions.setFreeze(target);
        fuctions.addToControl(target);

        target.sendMessage(main.formatChat(fileConfiguration.getString("Chat.Start-Control.Hacker").replaceAll("%player%", commandSender.getName())));
        commandSender.sendMessage(main.formatChatPrefix(fileConfiguration.getString("Freeze.Freezed-Start")).replaceAll("%player%", strings[0]));

        return true;
    }
}
