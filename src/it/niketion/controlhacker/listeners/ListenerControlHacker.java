package it.niketion.controlhacker.listeners;

import it.niketion.controlhacker.Fuctions;
import it.niketion.controlhacker.Main;
import it.niketion.controlhacker.commands.CommandControlHacker;
import it.niketion.controlhacker.commands.CommandFinish;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;

import java.util.logging.Level;

public class ListenerControlHacker implements Listener {

    //
    //
    //

    private static Main main = Main.getInstance();
    private Fuctions fuctions = Fuctions.getInstance();

    //
    //
    //

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        try {
            if (!main.getConfig().getBoolean("control.command-in-control")) {
                if (fuctions.hasPlayerControl(event.getPlayer().getName())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(prefixError + noCommands);
                    return;
                }
            }
        } catch (Exception e) {
            if (!getNoErrors()) {
                messageError();
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        try {
            if (fuctions.hasPlayerControl(event.getPlayer().getName())) {
                fuctions.removePlayerControl(event.getPlayer().getName());
                CommandFinish.finishControl(event.getPlayer(), Bukkit.getConsoleSender(), main);
                for (Player mod : Bukkit.getOnlinePlayers()) {
                    if (mod.hasPermission("inv.nik.controlhacker.mod")) {
                        mod.sendMessage(prefixError + playerQuit.replaceAll("%player%", event.getPlayer().getName()));
                    }
                }
                return;
            }
        } catch (Exception e) {
            if (!getNoErrors()) {
                messageError();
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        try {
            FileConfiguration config = CommandControlHacker.getLocationConfig();
            if (fuctions.hasPlayerControl(event.getPlayer().getName())) {
                event.setCancelled(true);
                for (Player mod : Bukkit.getOnlinePlayers()) {
                    if (mod.hasPermission("inv.nik.controlhacker.mod")) {
                        if (mod.getWorld().getName().equalsIgnoreCase(config.getString("hacker.World"))) {
                            mod.sendMessage(formatChatControl.replaceAll("%player%", event.getPlayer().getName())
                                    .replaceAll("%message%", event.getMessage()));
                        }
                    }
                }
                event.getPlayer().sendMessage(formatChatControl.replaceAll("%player%", event.getPlayer().getName())
                        .replaceAll("%message%", event.getMessage()));
            }
        } catch (Exception e) {
            if (!getNoErrors()) {
                messageError();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        try {
            if (!main.getConfig().getBoolean("control.block-break-in-control")) {
                if (fuctions.hasPlayerControl(event.getPlayer().getName())) {
                    event.setCancelled(true);
                    event.getBlock().getWorld().playEffect(event.getBlock().getLocation().add(0, 1, 0), Effect.HEART, 0);
                }
            }
        } catch (Exception e) {
            if (!getNoErrors()) {
                messageError();
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        try {
            if (!main.getConfig().getBoolean("control.block-place-in-control")) {
                if (fuctions.hasPlayerControl(event.getPlayer().getName())) {
                    event.setCancelled(true);
                    event.getBlock().getWorld().playEffect(event.getBlock().getLocation().add(0, 1, 0), Effect.HEART, 0);
                }
            }
        } catch (Exception e) {
            if (!getNoErrors()) {
                messageError();
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        try {
            if (!main.getConfig().getBoolean("control.entity-damage-in-control")) {
                if (event.getEntity() instanceof Player) {
                    if (fuctions.hasPlayerControl(event.getEntity().getName())) {
                        event.setCancelled(true);
                    }
                }
            }
        } catch (Exception e) {
            if (!getNoErrors()) {
                messageError();
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        try {
            if (!main.getConfig().getBoolean("teleport-in-control")) {
                if (fuctions.hasPlayerControl(event.getPlayer().getName())) {
                    event.setCancelled(true);
                }
            }
        } catch (Exception e) {
            if (!getNoErrors()) {
                messageError();
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        try {
            if (!main.getConfig().getBoolean("drop-item-in-control")) {
                if (fuctions.hasPlayerControl(event.getPlayer().getName())) {
                    event.setCancelled(true);
                }
            }
        } catch (Exception e) {
            if (!getNoErrors()) {
                messageError();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final EntityDeathEvent event) {
        Entity entity = event.getEntity();
        EntityDamageEvent lastDamage = entity.getLastDamageCause();
        EntityDamageEvent.DamageCause cause = lastDamage.getCause();
        if (entity instanceof Player && cause == EntityDamageEvent.DamageCause.VOID) {
            //Altro
        }
    }

    //
    //
    //

    private void messageError() {
        main.getLogger().log(Level.WARNING, "There is a error in console.");
        main.getLogger().log(Level.WARNING, "Error of true/false in the config?");
    }

    private boolean getNoErrors() {
        if (main.loadListeners()) {
            if (main.loadConfig()) {
                if (main.getEnable()) {
                    return true;
                } else {
                    main.getLogger().log(Level.WARNING, "There is a error in console.");
                    return false;
                }
            } else {
                main.getLogger().log(Level.WARNING, "There is a error in console.");
                return false;
            }
        } else {
            main.getLogger().log(Level.WARNING, "There is a error in console.");
            return false;
        }
    }

    private String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }

    private String prefixError = main.getConfig().getString(format("prefix.prefix-error")).replaceAll("&", "§");
    private String noCommands = main.getConfig().getString(format("control.command-deny")).replaceAll("&", "§");
    private String playerQuit = main.getConfig().getString(format("control.quit-control")).replaceAll("&", "§");
    private String formatChatControl = main.getConfig().getString(format("control.format-chat")).replaceAll("&", "§");
}
