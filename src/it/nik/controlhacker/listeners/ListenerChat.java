package it.nik.controlhacker.listeners;

import it.nik.controlhacker.Fuctions;
import it.nik.controlhacker.Main;
import it.nik.controlhacker.files.FileLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ListenerChat implements Listener {
    private Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isOnControl(player)) {
            event.setCancelled(true);
            player.sendMessage(main.formatChat(getConfigString("Event.Format-Control")).replaceAll("%player%", player.getName()).replaceAll("%message%", event.getMessage()));
            for (Player playerWorld : Bukkit.getOnlinePlayers()) {
                if (playerWorld.hasPermission("controlhacker.mod")) {
                    if (playerWorld.getWorld().getName().equals(FileLocation.getInstance().getLocationConfig().getString("Hacker.World"))) {
                        playerWorld.sendMessage(main.formatChat(getConfigString("Event.Format-Control")).replaceAll("%player%", player.getName()).replaceAll("%message%", event.getMessage()));
                    }
                }
            }
        }
    }

    private boolean isOnControl(Player player) {
        return Fuctions.getInstance().isOnControl(player);
    }
    private boolean isFreezed(Player player) {
        return Fuctions.getInstance().isFreezed(player);
    }

    private String getConfigString(String string) {
        return main.getConfig().getString(string);
    }
}
