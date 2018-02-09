package niketion.github.controlhacker.bukkit.listener;

import niketion.github.controlhacker.bukkit.Main;
import niketion.github.controlhacker.bukkit.Permissions;
import niketion.github.controlhacker.bukkit.commands.CommandFuctions;
import niketion.github.controlhacker.bukkit.filemanager.FileManager;
import niketion.github.controlhacker.bukkit.util.ControlGUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ListenerControlHacker implements Listener {

    private Main main = Main.getInstance();
    private ControlGUI controlGui = main.getControlGUI();
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        for (String command : main.getConfig().getStringList("event.command-whitelisted")) {
            // Check if command is in the "whitelist"
            if (event.getMessage().contains(command)) {
                return;
            }
        }
        if (playerInControl(event.getPlayer()))
            event.setCancelled(getBoolean("event.stop-command"));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (playerInControl(event.getPlayer())) {
            if (getBoolean("event.stop-move")) {
                // I accept the fly, so the player will be expelled
                event.getPlayer().setAllowFlight(true);
                event.getPlayer().teleport(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cheater
    	if (main.getInCheck().containsKey(event.getPlayer().getName())) {
            new FileManager("playerquit", "playerquit").add("list", event.getPlayer().getName());
            // Notify the entire staff that a player under control has come out
            new CommandFuctions().finishControl(event.getPlayer(), Bukkit.getPlayerExact(main.getInCheck().get(event.getPlayer().getName())));
            
            // Remove the player from the list of players who can close the "control gui"
            if (main.getControlGUI().getCanCloseGui().contains(event.getPlayer()))
            	main.getControlGUI().getCanCloseGui().remove(event.getPlayer());
            
            if (main.getConfig().getBoolean("command-quit-from-control.enabled")) {
                for (String command : main.getConfig().getStringList("command-quit-from-control.commands")) {
                    main.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", event.getPlayer().getName()));
                }
            }

            for (World worlds : Bukkit.getWorlds())
                for (Player players : worlds.getPlayers())
                    if (players.hasPermission(Permissions.CONTROL.toString()))
                        players.sendMessage(main.format(main.getConfig().getString("cheater-is-quit")).replaceAll("%player%", event.getPlayer().getName()));
        }
    	// Checker
        else if (main.getInCheck().containsValue(event.getPlayer().getName())) {
        	Player cheater = Bukkit.getPlayerExact(getKeyFromValue(main.getInCheck(), event.getPlayer().getName()));
        	new CommandFuctions().finishControl(cheater, main.getServer().getConsoleSender());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	try {
            // Finish Gui
        	if (event.getInventory().getTitle().equals(main.getFinishGUI().getTitle())) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);

                if (!(event.getWhoClicked() instanceof Player))
                	return;
                Player checker = (Player)event.getWhoClicked();
                
                String data = event.getCurrentItem().getData().toString();
                Player target = Bukkit.getPlayerExact(getKeyFromValue(main.getInCheck(), event.getWhoClicked().getName()));
                
                if (data.contains("14")) {
                    executeFinishGuiClick(checker, target, "hack", "second");
                } else if (data.contains("4")) {
                    executeFinishGuiClick(checker, target, "admission-refusal", "first");
                } else if (data.contains("5")) {
                    executeFinishGuiClick(checker, target, "clean", "third");
                    
                    // Send Messages
                    target.sendMessage(main.format(main.getConfig().getString("finish-cheater-message")));
                    checker.sendMessage(main.format(main.getConfig().getString("finish-checker-message").replace("%player%", target.getName())));
                } else {
                	return;
                }
                
                checker.closeInventory();
                
                // Remove from HashMap
                main.getInCheck().remove(getKeyFromValue(main.getInCheck(), event.getWhoClicked().getName()));
                new FileManager("top", "stats").set("top." + event.getWhoClicked().getName(), new FileManager(event.getWhoClicked().getName(), "stats").getConfig().getInt("all-controls") + 1);
            }
            // Control Gui
        	else if (event.getInventory().getTitle().equals(main.getControlGUI().getTitle())) {
        		event.setCancelled(true);
        		event.setResult(Event.Result.DENY);
        		
        		if (!(event.getWhoClicked() instanceof Player))
                	return;
                Player cheater = (Player)event.getWhoClicked();
        		
                // Return if the player is not in check
                if (main.getInCheck().get(cheater.getName()) == null) {
                	cheater.closeInventory();
                	return;
                }
                
        		int clickedSlot = event.getSlot();
        		if (controlGui.isSlotEmpty(clickedSlot)) return;
        		
        		cheater.closeInventory();

        		Player checker = Bukkit.getPlayerExact(main.getInCheck().get(cheater.getName()));
        		controlGui.executeClick(cheater, checker, "cheater-control-gui.items." + controlGui.getSlotItemSection(clickedSlot));
        	}
        } catch (NullPointerException ignored) { }
    }

    /**
     * Compact for click event
     * @param nameChecker - Name of checker
     * @param target - Cheater
     * @param option - "clean/admission-refusal/hack"
     * @param numberConfig - "third/first/second"
     */
    private void executeFinishGuiClick(Player checker, Player target, String option, String numberConfig) {
    	String checkerName = checker.getName();
        new FileManager(checkerName, "stats").set("all-controls", new FileManager(checkerName, "stats").getConfig().getInt("all-controls") + 1);
        new FileManager(checkerName, "stats").set(option, new FileManager(checkerName, "stats").getConfig().getInt(option) + 1);
        // Teleport cheater to spawn
        target.teleport(new CommandFuctions().getZone("end"));

        // Disable fly
        target.setAllowFlight(false);

        // Reset title
        if (main.rightVersion())
        	main.getTitle().sendTitle(target, "a", 1, 1, 1);
        
        String configPath = "finish-" + numberConfig + "-option";
        // Dispatch commands
        for (String command : main.getConfig().getStringList(configPath + ".commands")){
        	main.getServer().dispatchCommand(getCmdsExecutor(configPath + ".cmdsExecutor", checker), command
        			.replace("%cheater%", target.getName()) // non c'è bisogno di usare le regex
        			.replace("%checker%", checkerName));
        }
    }
    
    public static CommandSender getCmdsExecutor(String configPath, Player player){
    	String configExecutor = Main.getInstance().getConfig().getString(configPath);
    	// the onEnable already checks is the executor is different from "player" or "console" (non case-sentitive)
    	if (configExecutor.equalsIgnoreCase("checker")){
        	return player;
    	} else if (configExecutor.equalsIgnoreCase("console")) {
    		return Bukkit.getConsoleSender();
    	}
    	Main.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED
    			+"ControlHacker ERROR! Invalid command executor in config.yml. ('" + configPath + "')\n"
    					+ "using CONSOLE as command executor");
       	return Bukkit.getConsoleSender();
    }
    
    /**
     * Prevent players from closing "Control Gui"
     * @param event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    	if (!(event.getPlayer() instanceof Player) || !event.getInventory().getTitle().equals(controlGui.getTitle()))
    		return;
    	
    	// Useful when the checker slog during a control
    	if (!main.getInCheck().containsKey(event.getPlayer().getName())) return;
    	
    	Player player = (Player) event.getPlayer();
    	new BukkitRunnable() {
			@Override
			public void run() {
				if (main.getControlGUI().getCanCloseGui().contains(player)) {
					main.getControlGUI().getCanCloseGui().remove(player);
				} else {
					controlGui.openGui(player);
				}
			}
		}.runTaskLaterAsynchronously(main, 1);
    	
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            // Check if player has quit from a control
            for (String string : new FileManager("playerquit", "playerquit").getConfig().getStringList("list")) {
                if (string.contains(event.getPlayer().getName())) {
                    // Reset title
                    main.getTitle().sendTitle(event.getPlayer(), "a", 1, 1, 1);
                    new FileManager("playerquit", "playerquit").remove("list", event.getPlayer().getName());
                }
            }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (playerInControl(event.getPlayer()))
            event.setCancelled(getBoolean("event.stop-drop"));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (playerInControl(event.getPlayer()))
            event.setCancelled(getBoolean("event.stop-break"));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (playerInControl(event.getPlayer()))
            event.setCancelled(getBoolean("event.stop-place"));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        try {
            if (event.getEntity() instanceof Player || event.getDamager() instanceof Player) {
                if (playerInControl((Player) event.getEntity()) || playerInControl((Player) event.getDamager())) {
                    event.setCancelled(getBoolean("event.stop-damage"));
                }
            }
        } catch (Exception ignored) {}
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        // Cheater
    	if (main.getInCheck().containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getString(event.getMessage(), event.getPlayer()));
            Bukkit.getPlayerExact(main.getInCheck().get(event.getPlayer().getName())).sendMessage(getString(event.getMessage(), event.getPlayer()));
        } 
        // Checker
        else if (main.getInCheck().containsValue(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getString(event.getMessage(), event.getPlayer()));
            Bukkit.getPlayerExact(getKeyFromValue(main.getInCheck(), event.getPlayer().getName())).sendMessage(getString(event.getMessage(), event.getPlayer()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void stopChat(AsyncPlayerChatEvent event) {
        // Stop chat for cheater and checker
        for (World worlds : Bukkit.getWorlds())
            for (Player players : worlds.getPlayers())
                if (main.getInCheck().containsKey(players.getName())) {
                    event.getRecipients().remove(players);
                } else if (main.getInCheck().containsValue(players.getName())) {
                    event.getRecipients().remove(players);
                }
    }

    /**
     * Get key from value
     * @param hm - HashMap
     * @param value - Value
     * @return String
     */
    public static String getKeyFromValue(Map hm, String value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return (String) o;
            }
        }
        return null;
    }

    /**
     * Check if player is in a control
     * @param player - Player
     * @return boolean
     */
    private boolean playerInControl(Player player) {
        return main.getInCheck().containsKey(player.getName());
    }

    /**
     * Get boolean from config.yml
     * @param path - Path
     * @return boolean
     */
    private boolean getBoolean(String path) {
        return main.getConfig().getBoolean(path);
    }

    /**
     * Get string of format-chat from config.yml
     * @param message - Message to replace
     * @param player - Player to replace
     * @return String
     */
    private String getString(String message, Player player) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return main.format(main.getConfig().getString("format-chat.format")
                .replaceAll("%message%", message).replaceAll("%player%", player.getName()).replaceAll("%time%", format.format(now)));
    }
}
