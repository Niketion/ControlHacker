package niketion.github.controlhacker.bukkit;

import niketion.github.controlhacker.bukkit.commands.CommandControl;
import niketion.github.controlhacker.bukkit.commands.CommandControlHacker;
import niketion.github.controlhacker.bukkit.commands.CommandFinish;
import niketion.github.controlhacker.bukkit.commands.CommandFuctions;
import niketion.github.controlhacker.bukkit.listener.ListenerControlHacker;
import niketion.github.controlhacker.bukkit.title.*;
import niketion.github.controlhacker.bukkit.util.ControlGUI;
import niketion.github.controlhacker.bukkit.util.FinishGUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// 1.7.10 Spigot Javadocs: https://jd.bukkit.org/

public class Main extends JavaPlugin {

    /**
     * Where it is saved the checker and the cheater
     * @key cheater
     * @value checker
     */
    private HashMap<String, String> inCheck = new HashMap<>();

    /**
     * Get title interface (Works for 1.8+)
     */
    private Title title;

    /**
     * The various command functions
     */
    private CommandFuctions commandFuctions = new CommandFuctions();
    
    /**
     * Get /control gui
     */
    private ControlGUI controlGui;
    public ControlGUI getControlGUI() {
    	return controlGui;
    }
    
    /**
     * Get /finish gui
     */
    private FinishGUI finishGui;
    public FinishGUI getFinishGUI() {
    	return finishGui;
    }
    
    /**
     * The instance of main
     */
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    /**
     * The method is called when server startup
     */
    @Override
    public void onEnable() {
        ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();

        // Instance return
        instance = this;

        // Information to console
        consoleCommandSender.sendMessage(ChatColor.GREEN+"ControlHacker loading...");
        consoleCommandSender.sendMessage(ChatColor.GREEN+"Server version detected: " + Bukkit.getBukkitVersion());
        
        // Setup version of title
        setupTitle();

        // Setup default config.yml
        saveDefaultConfig();

        // Create GUIs
        controlGui = new ControlGUI();
        finishGui = new FinishGUI();
        
        // Registration of the events
        getServer().getPluginManager().registerEvents(new ListenerControlHacker(), this);

        // Registration of the commands
        getCommand("controlhacker").setExecutor(new CommandControlHacker());
        getCommand("control").setExecutor(new CommandControl());
        getCommand("finish").setExecutor(new CommandFinish());

        // Information to console
        consoleCommandSender.sendMessage(ChatColor.GREEN+"ControlHacker enabled. Developed by @Niketion, v"+getDescription().getVersion());
        if (!rightVersion()) {
            consoleCommandSender.sendMessage(ChatColor.RED+"Title disabled, version server 1.7");
        } else {
            consoleCommandSender.sendMessage(ChatColor.GREEN+"Title enabled.");
        }
        
    }

    /**
     * The method is called when server shutdown
     */
    @Override
    public void onDisable() {
        // When the plugin is disabled, it remove all from the control
        // (I use this loop for 1.9+ version support)
        for (World worlds : getServer().getWorlds())
            for (Player players : worlds.getPlayers())
                if (getInCheck().containsKey(players.getName())) {
                    if (!getServer().getBukkitVersion().contains("1.7")) {
                        getTitle().sendTitle(players, "a", 1, 1, 1);
                    }

                    // Teleport cheater to spawn
                    players.teleport(commandFuctions.getZone("end"));

                    // Disable fly
                    players.setAllowFlight(false);

                    players.sendMessage(format(getConfig().getString("finish-cheater-message")));
                    Bukkit.getConsoleSender().sendMessage(format(getConfig().getString("finish-checker-message").replaceAll("%player%", players.getName())));
                }
        
        // Clear the list of players who can close the "control gui"
        getControlGUI().getCanCloseGui().clear();
    }

    /**
     * Translated message to chatcolor, with the character "&"
     * @param message - Messages to translate
     * @return String
     */
    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Colors the lore
     * @param lore - list of lore
     * @return
     */
    public List<String> colorLore(List<String> lore) {
		List<String> coloredLore = new ArrayList<>();
		for (String line : lore) {
			coloredLore.add(format(line));
		}
		return coloredLore;
	}
    
    /**
     * Check if version of server is 1.8+
     * @return boolean
     */
    public boolean rightVersion() {
        return !getServer().getBukkitVersion().contains("1.7");
    }

    /**
     * Setup version of title (NMS)
     * (if server is 1.8+)
     * @return boolean
     */
    private boolean setupTitle() {
        if (rightVersion()) {
            String version;
            try {
                // Output "v1_x_Rx"
                version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            } catch (ArrayIndexOutOfBoundsException exception) {
                // If it does not find the version
                return false;
            }

            switch (version) {
                case "v1_12_R1":
                    title = new ControlHacker_1_12_R1();
                    break;
                case "v1_11_R1":
                    title = new ControlHacker_1_11_R1();
                    break;
                case "v1_10_R1":
                    title = new ControlHacker_1_10_R1();
                    break;
                case "v1_9_R2":
                    title = new ControlHacker_1_9_R2();
                    break;
                case "v1_9_R1":
                    title = new ControlHacker_1_9_R1();
                    break;
                case "v1_8_R1":
                    title = new ControlHacker_1_8_R1();
                    break;
                case "v1_8_R2":
                    title = new ControlHacker_1_8_R2();
                    break;
                case "v1_8_R3":
                    title = new ControlHacker_1_8_R3();
                    break;
            }
            return title != null;
        }
        return false;
    }

    /**
     * Where it is saved the checker and the cheater (getter)
     * @key cheater
     * @value checker
     */
    public HashMap<String, String> getInCheck() {
        return inCheck;
    }

    /**
     * Get right version of title
     * @return Title
     */
    public Title getTitle() {
        return title;
    }
}
