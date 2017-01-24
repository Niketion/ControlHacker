package it.nik.controlhacker.listeners;

import it.nik.controlhacker.Fuctions;
import it.nik.controlhacker.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class ListenerDamage implements Listener {
    private Main main = Main.getInstance();

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        try {
            if (isOnControl((Player) event.getEntity())) {
                event.setCancelled(!getConfigBoolean("Event.Bow"));
            }
        } catch (Exception ignored) {
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        try {
            if (event.getEntity() instanceof Player) {
                if (isOnControl((Player) event.getEntity())) {
                    event.setCancelled(getConfigBoolean("Event.God"));
                }
            }
        } catch (Exception ignored) {}
    }

    private boolean isOnControl(Player player) {
        return Fuctions.getInstance().isOnControl(player);
    }
    private boolean getConfigBoolean(String string) {
        return main.getConfig().getBoolean(string);
    }
}
