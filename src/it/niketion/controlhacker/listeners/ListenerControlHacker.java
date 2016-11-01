package it.niketion.controlhacker.listeners;

import it.niketion.controlhacker.Fuctions;
import it.niketion.controlhacker.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
    public void onBlockBreak(BlockBreakEvent event ) {
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
    public void onBlockPlace(BlockPlaceEvent event ) {
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
}
