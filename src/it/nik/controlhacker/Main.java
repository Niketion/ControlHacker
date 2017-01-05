package it.nik.controlhacker;

import it.nik.controlhacker.commands.*;
import it.nik.controlhacker.files.FileLocation;
import it.nik.controlhacker.files.FileReport;
import it.nik.controlhacker.listeners.ListenerControlHacker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private PluginManager pluginManager = getServer().getPluginManager();

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        getNoErrors();
        loadConfig();
        register();

        if (!depends()) {
            formatMessageError("TitleManager not found... title disabled.");
        }
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Fuctions.getInstance().isOnControl(player)) {
                Fuctions.getInstance().finishControl(player, Bukkit.getConsoleSender());
            }
        }
    }

    public boolean depends() {
        if (pluginManager.getPlugin("TitleManager") != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean getNoErrors() {
        if (String.valueOf(getDescription().getAuthors()).equals("[Niketion]")
                || getDescription().getVersion().equalsIgnoreCase("4.1")
                || pluginManager.isPluginEnabled("ControlHacker")) {
            return true;
        } else {
            pluginManager.disablePlugin(this);
            formatMessageError("ControlHacker is not enabled. You have edited the 'plugin.yml'?");
            return false;
        }
    }

    private boolean register() {
        try {
            getCommand("report").setExecutor(new CommandReport());
            getCommand("controlhacker").setExecutor(new CommandControlHacker());
            getCommand("control").setExecutor(new CommandControl());
            getCommand("finish").setExecutor(new CommandFinish());
            getCommand("listreports").setExecutor(new CommandListReports());

            pluginManager.registerEvents(new ListenerControlHacker(), this);

            return true;
        } catch (Exception e) {
            formatMessageError("I can not register commands or listeners", e);
            pluginManager.disablePlugin(this);
            return false;
        }
    }

    private boolean loadConfig() {
        try {
            if (getConfig().getString("Version-Config").equalsIgnoreCase("4.1")) {
                getConfig().options().copyDefaults(true);

                FileLocation fileLocation = FileLocation.getInstance();
                FileReport fileReport = FileReport.getInstance();

                fileLocation.getLocationConfig().save(fileLocation.getLocationFile());
                fileReport.getConfig().save(fileReport.getFile());

                if (!new File(getDataFolder(), "config.yml").exists()) {
                    saveResource("config.yml", false);
                }
                if (!new File(getDataFolder(), "location.yml").exists()) {
                    saveResource("location.yml", false);
                }
                if (!new File(getDataFolder(), "reports.yml").exists()) {
                    saveResource("reports.yml", false);
                }

                if (fileReport.getConfig().getString("MaxID") == null) {
                    fileReport.getConfig().set("MaxID", "0");
                }

                return true;
            } else {
                formatMessageError("Version of the 'config.yml' not right, delete here");
                pluginManager.disablePlugin(this);
                return false;
            }
        } catch (Exception e) {
            formatMessageError("I can't save the files", e);
            pluginManager.disablePlugin(this);
            return false;
        }
    }

    public void log(Level level, String message) {
        getServer().getLogger().log(level, message);
    }

    public String formatChat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String formatChatPrefix(String message) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("Chat.Prefix") + " " + message);
    }

    public void formatMessageError(String message) {
        log(Level.WARNING, "Error: " + message);
    }

    public void formatMessageError(String message, Exception exception) {
        log(Level.WARNING, "Error: " + message + ". Exception: " + exception.getClass().getName());
    }
}
