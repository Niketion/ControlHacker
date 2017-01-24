package it.nik.controlhacker.listeners;

import it.nik.controlhacker.Fuctions;
import it.nik.controlhacker.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ListenerBlock implements Listener {
    private Main main = Main.getInstance();

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
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isOnControl(event.getPlayer())) {
            event.setCancelled(!getConfigBoolean("Event.Drop-Item"));
        }
    }

    private boolean isOnControl(Player player) {
        return Fuctions.getInstance().isOnControl(player);
    }

    private boolean getConfigBoolean(String string) {
        return main.getConfig().getBoolean(string);
    }
}
