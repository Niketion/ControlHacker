package niketion.github.controlhacker.bukkit.commands;

import niketion.github.controlhacker.bukkit.Main;
import niketion.github.controlhacker.bukkit.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandFinish implements CommandExecutor {

    /**
     * Get instance of main
     */
    private Main main = Main.getInstance();

    /**
     * The various command functions
     */
    private CommandFuctions commandFuctions = new CommandFuctions();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandFuctions.isPlayer(commandSender)) || !(commandFuctions.hasPermission(commandSender.getName(), Permissions.FINISH))) {
            return false;
        }

        if (!(strings.length > 0)) {
            commandSender.sendMessage(main.format(commandFuctions.getString("usage-finish")));
            return false;
        }

        // Check if target is online
        if (!commandFuctions.foundPlayer(commandSender, strings[0])) {
            return false;
        }

        // Check if zone are set
        if (!commandFuctions.isSet("end")) {
            return false;
        }

        // Check if target is in "inCheck" hashmap to key cheater
        if (!main.getInCheck().containsKey(strings[0])) {
            commandSender.sendMessage(main.format(commandFuctions.getString("is-not-in-check").replaceAll("%player%", strings[0])));
            return false;
        }

        commandFuctions.finishControl(Bukkit.getPlayerExact(strings[0]), commandSender);
        return true;
    }
}
