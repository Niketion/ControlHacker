package niketion.github.controlhacker.listener;

import niketion.github.controlhacker.Main;
import niketion.github.controlhacker.Permissions;
import niketion.github.controlhacker.commands.CommandFuctions;
import niketion.github.controlhacker.filemanager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ListenerControlHacker implements Listener {

    private Main main = Main.getInstance();

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
        if (main.getInCheck().containsKey(event.getPlayer().getName())) {
            new FileManager("playerquit", "playerquit").add("list", event.getPlayer().getName());
            // Notify the entire staff that a player under control has come out
            new CommandFuctions().finishControl(event.getPlayer(), Bukkit.getConsoleSender());

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
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getTitle().contains("Finish")) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);

            String data = event.getCurrentItem().getData().toString();
            Player target = Bukkit.getPlayerExact(getKeyFromValue(main.getInCheck(), event.getWhoClicked().getName()));

            if (data.contains("14")) {
                executeClick(event.getWhoClicked().getName(), target, "hack", "second");
            } else if (data.contains("4")) {
                executeClick(event.getWhoClicked().getName(), target, "admission-refusal", "first");
            } else if (data.contains("5")) {
                executeClick(event.getWhoClicked().getName(), target, "clean", "third");

                target.sendMessage(main.format(main.getConfig().getString("finish-cheater-message")));
                event.getWhoClicked().sendMessage(main.format(main.getConfig().getString("finish-checker-message").replaceAll("%player%", target.getName())));
            } else {
                return;
            }
            event.getWhoClicked().closeInventory();

            // Remove from HashMap
            main.getInCheck().remove(getKeyFromValue(main.getInCheck(), event.getWhoClicked().getName()));
            new FileManager("top", "stats").set("top."+event.getWhoClicked().getName(), new FileManager(event.getWhoClicked().getName(), "stats").getConfig().getInt("all-controls")+1);
        }
    }

    /**
     * Compact for click event
     * @param nameChecker - Name of checker
     * @param target - Cheater
     * @param option - "clean/admission-refusal/hack"
     * @param numberConfig - "third/first/second"
     */
    private void executeClick(String nameChecker, Player target, String option, String numberConfig) {
        new FileManager(nameChecker, "stats").set("all-controls", new FileManager(nameChecker, "stats").getConfig().getInt("all-controls")+1);
        new FileManager(nameChecker, "stats").set(option, new FileManager(nameChecker, "stats").getConfig().getInt(option)+1);
        // Teleport cheater to spawn
        target.teleport(new CommandFuctions().getZone("end"));

        // Disable fly
        target.setAllowFlight(false);

        // Reset title
        if (!main.getServer().getBukkitVersion().contains("1.7"))
            main.getTitle().sendTitle(target, "a", 1, 1, 1);
        for (String command : main.getConfig().getStringList("finish-"+numberConfig+"-option.commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%cheater%", target.getName()).replaceAll("%checker%", nameChecker));
        }
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
        if (main.getInCheck().containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getString(event.getMessage(), event.getPlayer()));
            Bukkit.getPlayerExact(main.getInCheck().get(event.getPlayer().getName())).getPlayer().sendMessage(getString(event.getMessage(), event.getPlayer()));
        } else if (main.getInCheck().containsValue(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getString(event.getMessage(), event.getPlayer()));
            Bukkit.getPlayerExact(getKeyFromValue(main.getInCheck(), event.getPlayer().getName())).getPlayer().sendMessage(getString(event.getMessage(), event.getPlayer()));
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
    private String getKeyFromValue(Map hm, String value) {
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
