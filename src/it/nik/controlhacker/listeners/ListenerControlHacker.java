package it.nik.controlhacker.listeners;

import it.nik.controlhacker.Fuctions;
import it.nik.controlhacker.Main;
import it.nik.controlhacker.files.FileLocation;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.*;

public class ListenerControlHacker implements Listener {
    private Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isOnControl(player)) {
            event.setCancelled(true);
            for (Player playerWorld : Bukkit.getOnlinePlayers()) {
                if (playerWorld.hasPermission("controlhacker.mod")) {
                    if (playerWorld.getWorld().getName().equals(FileLocation.getInstance().getLocationConfig().getString("Hacker.World"))) {
                        playerWorld.sendMessage(main.formatChat(getConfigString("Event.Format-Control")).replaceAll("%player%", player.getName())
                                .replaceAll("%message%", event.getMessage()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isOnControl(player)) {
            event.setCancelled(true);
            player.sendMessage(main.formatChatPrefix(getConfigString("Event.Command-Blocked")));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isOnControl(event.getPlayer())) {
            for (Player staffer : Bukkit.getServer().getOnlinePlayers()) {
                if (staffer.hasPermission("controlhacker.mod")) {
                    staffer.sendMessage(main.formatChatPrefix(main.getConfig().getString("Chat.Quit")).replaceAll("%player%", event
                            .getPlayer().getName()));
                    try {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf(main.getConfig().getString("Sound.Start-Control")
                                .toUpperCase().replaceAll("-", "_")), 1, 0);
                    } catch (NullPointerException ignored) {
                    }
                    Fuctions.getInstance().finishControl(event.getPlayer(), Bukkit.getConsoleSender());
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (isOnControl(event.getPlayer())) {
            event.setCancelled(!getConfigBoolean("Event.Block-Break"));
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (isOnControl(event.getPlayer())) {
            event.setCancelled(!getConfigBoolean("Event.Block-Place"));
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (isOnControl(event.getPlayer())) {
            event.setCancelled(!getConfigBoolean("Event.Teleport"));
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isOnControl(event.getPlayer())) {
            event.setCancelled(!getConfigBoolean("Event.Drop-Item"));
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        try {
            if (isOnControl((Player) event.getEntity())) {
                event.setCancelled(!getConfigBoolean("Event.Bow"));
            }
        } catch (ClassCastException ignored) {
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (isOnControl((Player) event.getEntity())) {
                event.setCancelled(getConfigBoolean("Event.God"));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(getConfigBoolean("Event.Block-Break"));
        }
    }

    private boolean isOnControl(Player player) {
        return Fuctions.getInstance().isOnControl(player);
    }

    private String getConfigString(String string) {
        return main.getConfig().getString(string);
    }

    private boolean getConfigBoolean(String string) {
        return main.getConfig().getBoolean(string);
    }
}
