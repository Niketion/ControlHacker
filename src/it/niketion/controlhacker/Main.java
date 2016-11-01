package it.niketion.controlhacker;

import it.niketion.controlhacker.commands.CommandControl;
import it.niketion.controlhacker.commands.CommandControlHacker;
import it.niketion.controlhacker.commands.CommandFinish;
import it.niketion.controlhacker.listeners.ListenerControlHacker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    //
    //
    //

    private Fuctions fuctions = Fuctions.getInstance();
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    //
    //
    //

    @Override
    public void onEnable() {
        instance = this;

        getEnable();

        loadConfig();
        loadCommands();
        loadListeners();
        loadDepends();
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (fuctions.hasPlayerControl(player.getName())) {
                fuctions.removePlayerControl(player.getName());
                CommandFinish.finishControl(player, Bukkit.getConsoleSender(), this);
                getLogger().log(Level.WARNING, "All hacker are teleport to spawn for the reload...");
            }
        }
    }

    //
    //
    //

    public boolean getEnable() {
        if (getServer().getPluginManager().isPluginEnabled("ControlHacker")) {
            getLogger().log(Level.FINE, "There isn't error on enabling.");
            getLogger().log(Level.FINE, "Author: "
                    + getDescription().getAuthors());
            return true;
        } else {
            getLogger().log(Level.WARNING, "Error: Problem on enabling, you've edit the plugin.yml?");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
    }

    public boolean loadConfig() {
        if (getConfig().options() != null) {
            if (getConfig().getString("version-config").equals("0.2a")) {
                try {
                    getConfig().options().copyDefaults(true);
                    CommandControlHacker.getLocationConfig().save(CommandControlHacker.getLocationFile());

                    if (!new File(getDataFolder(), "config.yml").exists()) {
                        saveResource("config.yml", false);
                    }
                    if (!new File(getDataFolder(), "controllocation.yml").exists()) {
                        saveResource("controllocation.yml", false);
                    }
                    return true;
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, "Error: Problem on load. IOException.");
                    return false;
                }
            } else {
                getLogger().log(Level.WARNING, "Error: Problem on version of the config, delete here.");
                return false;
            }
        } else {
            getLogger().log(Level.WARNING, "Error: Problem on load config. Exception: Null");
            return false;
        }
    }

    public boolean loadCommands() {
        try {
            getCommand("controlhacker").setExecutor(new CommandControlHacker());
            getCommand("control").setExecutor(new CommandControl());
            getCommand("finish").setExecutor(new CommandFinish());
            return true;
        } catch (Exception e) {
            e.getCause();
            getLogger().log(Level.WARNING, "Error: Problem on load commands.");
            return false;
        }
    }

    public boolean loadListeners() {
        try {
            Bukkit.getPluginManager().registerEvents(new ListenerControlHacker(), this);
            return true;
        } catch (Exception e) {
            e.getCause();
            getLogger().log(Level.WARNING, "Error: Problem on load listeners.");
            return false;
        }
    }

    public boolean loadDepends() {
        if (getServer().getPluginManager().getPlugin("TitleManager") != null) {
            getLogger().log(Level.FINE, "Find TitleManager.");
            return true;
        } else {
            getLogger().log(Level.WARNING, "Can't find TitleManager, title disabled.");
            return false;
        }
    }
}
