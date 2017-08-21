package niketion.github.controlhacker.bukkit.commands;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import niketion.github.controlhacker.bukkit.Main;
import niketion.github.controlhacker.bukkit.Permissions;
import niketion.github.controlhacker.bukkit.filemanager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class CommandControlHacker implements CommandExecutor {

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
        if (!(commandFuctions.isPlayer(commandSender))) {
            return false;
        }

        if (!commandFuctions.hasPermission(commandSender.getName(), Permissions.ADMIN)) {
            getUsage(commandSender.getName());
            return false;
        }

        if (!(strings.length > 0)) {
            getUsage(commandSender.getName());
            return false;
        }

        switch(strings[0]) {
            case "top":
                try {
                    Map<String, Comparable> variableMap = new HashMap<>();
                    for (String map : new FileManager("top", "stats").getConfig().getConfigurationSection("top").getKeys(false)) {
                        if (new File(main.getDataFolder() + "/stats/" + map + ".yml").exists())
                            variableMap.put(map, new FileManager("top", "stats").getConfig().getInt("top." + map));
                    }
                    final SortedMap<String, Comparable> sortedMap = ImmutableSortedMap
                            .copyOf(variableMap, Ordering.natural().reverse().onResultOf(Functions.forMap(variableMap)).compound(Ordering.natural().reverse()));

                    Date now = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    commandSender.sendMessage(main.format(commandFuctions.getString("top-message.head").replaceAll("%date%", format.format(now))));
                    int number = 1;
                    for (String key : sortedMap.keySet()) {
                        if (number <= main.getConfig().getInt("top-number")) {
                            int value = (int) sortedMap.get(key);
                            commandSender.sendMessage(main.format(commandFuctions.getString("top-message.default").replaceAll("%number%", String.valueOf(number)).replaceAll("%player%", key).replaceAll("%value%", String.valueOf(value))));
                        }
                        number++;
                    }
                } catch (NullPointerException excpetion) {
                    commandSender.sendMessage(main.format(commandFuctions.getString("top-null")));
                }
                return true;
            case "setzone":
                if (strings.length > 1) {
                    switch(strings[1]) {
                        case "cheater":
                            setZone(commandSender.getName(), "cheater");
                            break;
                        case "checker":
                            setZone(commandSender.getName(), "checker");
                            break;
                        case "end":
                            setZone(commandSender.getName(), "end");
                            break;
                    }
                } else {
                    getUsage(commandSender.getName());
                    return false;
                }
                break;
            case "reload":
                // The config is reloaded twice, as 1 does not work
                for (int i=0; i<1;i++)
                    main.reloadConfig();
                commandSender.sendMessage(main.format(ChatColor.DARK_PURPLE+"Config successfully reload."));
                break;
            case "stats":
                if (strings.length > 1) {
                    if (new File(main.getDataFolder().toString()+"/stats/"+strings[1]+".yml").exists()) {
                        FileConfiguration fileConfiguration = new FileManager(strings[1], "stats").getConfig();
                        for (String message : main.getConfig().getStringList("stats-message")) {
                            commandSender.sendMessage(main.format(message.replaceAll("%value-1%", String.valueOf(fileConfiguration.getInt("all-controls")))
                                    .replaceAll("%value-2%", String.valueOf(fileConfiguration.getInt("clean")))
                                    .replaceAll("%value-3%", String.valueOf(fileConfiguration.getInt("hack")))
                                    .replaceAll("%value-4%", String.valueOf(fileConfiguration.getInt("admission-refusal")))
                                    .replaceAll("%player%", strings[1])));
                        }
                        return true;
                    } else {
                        commandSender.sendMessage(main.format(commandFuctions.getString("stats-not-found").replaceAll("%player%", strings[1])));
                        return false;
                    }
                } else {
                    getUsage(commandSender.getName());
                    return false;
                }
            case "reset":
                if (strings.length > 1) {
                    if (new File(main.getDataFolder().toString()+"/stats/"+strings[1]+".yml").exists()) {
                        new FileManager(strings[1], "stats").getConfigFile().delete();
                        commandSender.sendMessage(main.format(commandFuctions.getString("stats-deleted").replaceAll("%player%",strings[1])));
                        return true;
                    } else {
                        commandSender.sendMessage(main.format(commandFuctions.getString("stats-not-found").replaceAll("%player%", strings[1])));
                        return false;
                    }
                } else {
                    getUsage(commandSender.getName());
                    return false;
                }
        }

        return true;
    }

    /**
     * Set zone of control (cheater/checker/end)
     * @param namePlayer - Name of player to get Location
     * @param nameZone - Zone to set
     */
    private void setZone(String namePlayer, String nameZone) {
        Player player = Bukkit.getPlayerExact(namePlayer);
        // Get config "location.yml"
        FileManager fileConfiguration = new FileManager("location", "locations");

        fileConfiguration.set(nameZone.toUpperCase() + ".world", player.getLocation().getWorld().getName());
        fileConfiguration.set(nameZone.toUpperCase() + ".x", player.getLocation().getX());
        fileConfiguration.set(nameZone.toUpperCase() + ".y", player.getLocation().getY());
        fileConfiguration.set(nameZone.toUpperCase() + ".z", player.getLocation().getZ());
        fileConfiguration.set(nameZone.toUpperCase() + ".yaw", player.getLocation().getYaw());
        fileConfiguration.set(nameZone.toUpperCase() + ".pitch", player.getLocation().getPitch());

        player.sendMessage(main.format(commandFuctions.getString("zone-set").replaceAll("%value%", nameZone.toUpperCase())));
    }

    /**
     * Get usage from config.yml
     * @param namePlayer - Name of player to send messages
     */
    private void getUsage(String namePlayer) {
        for (String usageCommand : main.getConfig().getStringList("usage-controlhacker"))
            Bukkit.getPlayerExact(namePlayer).sendMessage(main.format(usageCommand));
        Bukkit.getPlayerExact(namePlayer).sendMessage(ChatColor.DARK_PURPLE+"Plugin developed by @Niketion");
    }
}
